package com.tec.ftpserver.repository;

import org.apache.ftpserver.ftplet.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByName(String name);

    @Query(value = "{}", fields = "{name: 1, _id: 0}")
    List<User> findAllUserNames();

    void deleteByName(String name);
}
