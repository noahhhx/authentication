package com.noah.db.document.repository;

import com.noah.db.document.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String>,
        BaseUserRepository {
}
