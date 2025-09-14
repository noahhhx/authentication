# JWT-Authentication

This service issues short-lived access tokens and long-lived refresh tokens using RSA. You can use the access tokens to protect other services (resource servers).

Quick facts:
- Signing algorithm: RSA (asymmetric); downstream services validate with the public key only.
- Issuer: `myApp` (embedded in the `iss` claim).
- Access token lifetime: 5 minutes.
- Refresh token lifetime: 30 days.
- JWKS endpoint for public key discovery: `GET /.well-known/jwks.json`

## Endpoints in this service
- TODO

## Using these JWTs to protect another service (Spring Boot Resource Server)
If you have another Spring Boot application that you want to protect using tokens from this service, configure it as an OAuth2 Resource Server.

Option A: Validate via JWKS endpoint (recommended)
1. Expose this auth service to your network (e.g., http://auth.local:8080).
2. In the resource server application, add Spring Security OAuth2 Resource Server dependency.
3. Configure `spring.security.oauth2.resourceserver.jwt.jwk-set-uri` to point to this service's JWKS.

Example `application.yml` in the protected service:

```
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          jwk-set-uri: http://auth.local:8080/.well-known/jwks.json
```

And a basic security configuration plus issuer validation:

```
@Configuration
@EnableMethodSecurity
public class SecurityConfig {
  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .authorizeHttpRequests(reg -> reg
        .requestMatchers("/actuator/health").permitAll()
        .anyRequest().authenticated()
      )
      .oauth2ResourceServer(oauth -> oauth.jwt())
      .csrf(AbstractHttpConfigurer::disable)
      .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    return http.build();
  }

  @Bean
  JwtDecoder jwtDecoder() {
    NimbusJwtDecoder decoder = NimbusJwtDecoder
      .withJwkSetUri("http://auth.local:8080/.well-known/jwks.json")
      .build();
    OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer("myApp");
    decoder.setJwtValidator(withIssuer);
    return decoder;
  }
}
```

Option B: Validate with a static public key
1. Download the public JWK from `/.well-known/jwks.json` and convert to PEM if desired, or embed the JWK.
2. Configure your resource server with the public key instead of JWKS URI.

## Node.js/Express example
```
import express from 'express';
import jwt from 'jsonwebtoken';
import jwksRsa from 'jwks-rsa';

const app = express();
const jwksClient = jwksRsa({
  jwksUri: 'http://auth.local:8080/.well-known/jwks.json',
  cache: true,
  cacheMaxEntries: 5,
  cacheMaxAge: 10 * 60 * 1000,
});

function getKey(header, callback) {
  jwksClient.getSigningKey(header.kid, (err, key) => {
    if (err) return callback(err);
    const signingKey = key.getPublicKey();
    callback(null, signingKey);
  });
}

app.use((req, res, next) => {
  const auth = req.headers.authorization || '';
  const token = auth.startsWith('Bearer ') ? auth.substring(7) : null;
  if (!token) return res.status(401).send('Missing token');

  jwt.verify(token, getKey, { algorithms: ['RS256'], issuer: 'myApp' }, (err, decoded) => {
    if (err) return res.status(401).send('Invalid token');
    req.user = decoded;
    next();
  });
});

app.get('/protected', (req, res) => res.json({ ok: true, sub: req.user.sub }));
app.listen(3000);
```

## Protecting controllers by authorities/roles in your resource server
This auth service now includes an `authorities` claim in the access token, containing the user's Spring Security authorities (for example: `ROLE_ADMIN`, `ROLE_USER`).

In your downstream Spring Boot resource server, configure a JwtAuthenticationConverter to read that claim and convert it to GrantedAuthorities:

```
@Configuration
@EnableMethodSecurity
public class SecurityConfig {
  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .authorizeHttpRequests(reg -> reg
        .requestMatchers("/actuator/health").permitAll()
        .requestMatchers(HttpMethod.GET, "/admin/**").hasRole("ADMIN")
        .anyRequest().authenticated()
      )
      .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
      .csrf(AbstractHttpConfigurer::disable)
      .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    return http.build();
  }

  @Bean
  Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
    JwtGrantedAuthoritiesConverter delegate = new JwtGrantedAuthoritiesConverter();
    delegate.setAuthoritiesClaimName("authorities");
    // Our authorities already include the full value (e.g., ROLE_ADMIN), so no extra prefix
    delegate.setAuthorityPrefix("");

    return jwt -> {
      Collection<GrantedAuthority> authorities = delegate.convert(jwt);
      return new JwtAuthenticationToken(jwt, authorities);
    };
  }
}
```

You can then protect controllers or methods using annotations:
- `@PreAuthorize("hasRole('ADMIN')")`
- `@PreAuthorize("hasAuthority('ROLE_ADMIN')")`

If you prefer to use scopes instead, add a `scope` or `scp` claim in the token and configure `JwtGrantedAuthoritiesConverter` accordingly.

## Accessing roles from JWT in your downstream controllers
Below are simple patterns you can use inside your protected service to pull roles from the JWT and pass them to your own service methods.

Option A: Inject Authentication and read GrantedAuthorities

```
@RestController
@RequestMapping("/api/example")
public class ExampleController {
  private final MyService myService;
  public ExampleController(MyService myService) { this.myService = myService; }

  @GetMapping("/do")
  public ResponseEntity<?> doSomething(Authentication authentication) {
    // If you've configured JwtGrantedAuthoritiesConverter as in the README,
    // authorities already come from the JWT "authorities" claim.
    List<String> roles = authentication.getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .toList();

    Object result = myService.doSomethingWithRoles(roles);
    return ResponseEntity.ok(result);
  }
}
```

Option B: Inject Jwt directly with @AuthenticationPrincipal

```
@RestController
@RequestMapping("/api/example2")
public class Example2Controller {
  private final MyService myService;
  public Example2Controller(MyService myService) { this.myService = myService; }

  @GetMapping("/do")
  public ResponseEntity<?> doSomething(@AuthenticationPrincipal Jwt jwt) {
    // Read raw claim from token
    List<String> roles = jwt.getClaimAsStringList("authorities");
    Object result = myService.doSomethingWithRoles(roles);
    return ResponseEntity.ok(result);
  }
}
```