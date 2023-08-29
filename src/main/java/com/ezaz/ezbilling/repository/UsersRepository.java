package com.ezaz.ezbilling.repository;

import com.ezaz.ezbilling.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UsersRepository extends MongoRepository<User, String> {
    User findByUsername(String username);
}
