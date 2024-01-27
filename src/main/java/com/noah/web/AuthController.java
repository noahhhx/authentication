package com.noah.web;

import com.noah.db.document.User;
import com.noah.db.document.repository.UserRepository;
import com.noah.dto.LoginDTO;
import com.noah.dto.SignupDTO;
import com.noah.dto.TokenDTO;
import com.noah.security.TokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    UserDetailsManager userDetailsManager;
    @Autowired
    TokenGenerator tokenGenerator;
    @Autowired
    UserRepository userRepository;
    @Autowired
    @Qualifier("daoAuthenticationProvider")
    DaoAuthenticationProvider daoAuthenticationProvider;
    @Autowired
    @Qualifier("jwtRefreshTokenAuthProvider")
    JwtAuthenticationProvider refreshTokenAuthProvider;

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody SignupDTO signupDTO) {
        User user = new User(signupDTO.getUsername(), signupDTO.getPassword(), LocalDateTime.now());
        if (!userRepository.findByUsername(signupDTO.getUsername()).equals(Optional.empty())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
        }
        userDetailsManager.createUser(user);
        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(user, signupDTO.getPassword(), Collections.EMPTY_LIST);
        return ResponseEntity.ok(tokenGenerator.createToken(authentication));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody LoginDTO loginDTO) {
        Authentication authentication = daoAuthenticationProvider.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(loginDTO.username(), loginDTO.password()));
        return ResponseEntity.ok(tokenGenerator.createToken(authentication));
    }

    @PostMapping("/token")
    public ResponseEntity<TokenDTO> token(@RequestBody TokenDTO tokenDTO) {
        Authentication authentication = refreshTokenAuthProvider.authenticate(new BearerTokenAuthenticationToken(tokenDTO.getRefreshToken()));
        Jwt jwt = (Jwt) authentication.getCredentials();
        // check if present in db and not revoked, etc
        return ResponseEntity.ok(tokenGenerator.createToken(authentication));
    }
}
