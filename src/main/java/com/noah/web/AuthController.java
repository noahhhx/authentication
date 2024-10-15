package com.noah.web;

import com.noah.db.document.User;
import com.noah.dto.LoginDTO;
import com.noah.dto.TokenDTO;
import com.noah.security.TokenGenerator;
import com.noah.service.UserManager;
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
	private final UserManager userManager;
	private final DaoAuthenticationProvider daoAuthenticationProvider;
	private final JwtAuthenticationProvider refreshTokenAuthProvider;

	@PostMapping("/register")
	public ResponseEntity<Object> register(@RequestBody LoginDTO LoginDTO) {
		User user = new User(LoginDTO.username(), LoginDTO.password(), LocalDateTime.now());
		if (isUserExists(LoginDTO.username())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
		}
		userDetailsManager.createUser(user);
		Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(user, LoginDTO.password(), Collections.emptyList());
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
		User user = userManager.findById(jwt.getSubject());
		if (!user.isAccountNonLocked()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Account is locked");
		}
		return ResponseEntity.ok(tokenGenerator.createToken(authentication));
	}

	private boolean isUserExists(String username) {
		try {
			userManager.loadUserByUsername(username);
			return true;
		} catch (UsernameNotFoundException e) {
			return false;
		}
	}
}
