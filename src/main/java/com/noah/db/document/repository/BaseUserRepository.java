package com.noah.db.document.repository;

import com.noah.db.document.User;

import java.util.Optional;

public interface BaseUserRepository {
	Optional<User> findByUsername(String username);
	boolean existsByUsername(String username);
	void deleteUserByUsername(String username);
	User save(User user);
	Optional<User> findById(String id);
}
