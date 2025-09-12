package com.noah.jwt.db.repository;

import com.noah.jwt.db.Authority;
import com.noah.jwt.db.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
  List<Authority> findByUser(User user);
}
