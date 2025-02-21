package com.noah.jwt.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity(name = "users")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;
  
  @Column(unique = true, nullable = false)
  private String username;

  @Column(nullable = false)
  private String password;

  @Email
  @Column(unique = true)
  private String email;

  @Column(nullable = false)
  @Builder.Default
  private LocalDateTime createdAt = LocalDateTime.now();

  @OneToMany(mappedBy = "user")
  @ToString.Exclude
  private Set<Authority> authorities = new HashSet<>();

  @Column(nullable = false)
  @Builder.Default
  private Integer failedAttempts = 0;

  @Column(nullable = false)
  @Builder.Default
  private Boolean accountLocked = false;

  @Column(nullable = false)
  @Builder.Default
  private Boolean credentialsExpired = false;

  @Column(nullable = false)
  @Builder.Default
  private Boolean enabled = true;


  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    if (authorities == null) {
      return Collections.emptyList();
    }
    return authorities;
  }

  @Override
  public @NonNull String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    if (accountLocked == null) {
      return true;
    }
    return !accountLocked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    if (credentialsExpired == null) {
      return true;
    }
    return !credentialsExpired;
  }

  @Override
  public boolean isEnabled() {
    if (enabled == null) {
      return true;
    }
    return enabled;
  }

}
