package com.noah.jwt.security;

import com.noah.jwt.db.User;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtToUserConverter implements Converter<Jwt, UsernamePasswordAuthenticationToken> {

  @Override
  public UsernamePasswordAuthenticationToken convert(Jwt jwt) {
    User user = new User();
    user.setId(UUID.fromString(jwt.getSubject()));
    List<String> roles = jwt.getClaim("authorities");
    List<SimpleGrantedAuthority> authorities = roles == null ? Collections.emptyList() :
            roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    return new UsernamePasswordAuthenticationToken(user, jwt, authorities);
  }
}
