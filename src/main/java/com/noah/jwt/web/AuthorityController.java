package com.noah.jwt.web;

import com.noah.jwt.db.Authority;
import com.noah.jwt.db.User;
import com.noah.jwt.db.repository.AuthorityRepository;
import com.noah.jwt.service.UserManager;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/authorities")
@AllArgsConstructor
public class AuthorityController {

  private final UserManager userManager;
  private final AuthorityRepository authorityRepository;

  @PostMapping("/{userId}")
  public ResponseEntity<Set<String>> addAuthority(
          @PathVariable UUID userId, @RequestBody Map<String, String> body) {
    String authorityName = body.get("authority");
    User user = userManager.findById(userId);

    boolean exists = user.getAuthorities() != null
            && user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(authorityName));
    if (!exists) {
      Authority authority = new Authority();
      authority.setAuthority(authorityName);
      authority.setUser(user);
      authorityRepository.save(authority);
    }
    Set<String> names = new HashSet<>();
    authorityRepository.findByUser(user).forEach(a -> names.add(a.getAuthority()));
    return ResponseEntity.ok(names);
  }
}
