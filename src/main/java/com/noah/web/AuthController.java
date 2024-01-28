package com.noah.web;

import com.noah.db.document.User;
import com.noah.db.document.repository.UserRepository;
import com.noah.dto.LoginDTO;
import com.noah.dto.SignupDTO;
import com.noah.dto.TokenDTO;
import com.noah.security.TokenGenerator;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final UserDetailsManager userDetailsManager;
    private final TokenGenerator tokenGenerator;
    private final UserRepository userRepository;
    private final DaoAuthenticationProvider daoAuthenticationProvider;
    private final JwtAuthenticationProvider refreshTokenAuthProvider;

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody SignupDTO signupDTO) {
        User user = new User(signupDTO.username(), signupDTO.password(), LocalDateTime.now());
        if (!userRepository.findByUsername(signupDTO.username()).equals(Optional.empty())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
        }
        userDetailsManager.createUser(user);
        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(user, signupDTO.password(), Collections.emptyList());
        return ResponseEntity.ok(tokenGenerator.createToken(authentication));
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginDTO loginDTO) {
        if (!userDetailsManager.userExists(loginDTO.username())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
        Authentication authentication = daoAuthenticationProvider.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(loginDTO.username(), loginDTO.password()));
        return ResponseEntity.ok(tokenGenerator.createToken(authentication));
    }

    @PostMapping("/token")
    public ResponseEntity<Object> token(@RequestBody TokenDTO tokenDTO) {
        Authentication authentication = refreshTokenAuthProvider.authenticate(new BearerTokenAuthenticationToken(tokenDTO.getRefreshToken()));
        Jwt jwt = (Jwt) authentication.getCredentials();
        User user = userRepository.findById(jwt.getSubject())
                .orElseThrow(() -> new UsernameNotFoundException(
                        MessageFormat.format("ID {0} not found for any user", jwt.getSubject())
                ));
        if (!user.isAccountNonLocked()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Account is locked");
        }
        return ResponseEntity.ok(tokenGenerator.createToken(authentication));
    }
}
