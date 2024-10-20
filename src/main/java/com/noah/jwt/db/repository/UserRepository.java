package com.noah.jwt.db.repository;

import com.noah.jwt.db.User;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);

  boolean existsByUsername(String username);

  void deleteUserByUsername(String username);

  Optional<User> findById(@NonNull UUID id);
}
