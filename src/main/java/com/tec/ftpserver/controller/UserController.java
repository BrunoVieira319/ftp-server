package com.tec.ftpserver.controller;

import com.tec.ftpserver.domain.MongoUser;
import com.tec.ftpserver.dto.UserDto;
import com.tec.ftpserver.server.MongoUserManager;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    private MongoUserManager service;

    @Autowired
    public UserController(MongoUserManager mongoUserManager) {
        this.service = mongoUserManager;
    }

    @PostMapping
    public ResponseEntity createNewUser(@RequestBody UserDto userDto) {
        List<Authority> authorities = new ArrayList<>();
        authorities.add(new WritePermission());
        authorities.add(new ConcurrentLoginPermission(1, 1));

        MongoUser user = new MongoUser(userDto.getName(), userDto.getPassword(), authorities);
        try {
            service.save(user);
        } catch (FtpException e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}