package com.tec.ftpserver.server;

import com.tec.ftpserver.domain.MongoUser;
import com.tec.ftpserver.repository.UserRepository;
import org.apache.ftpserver.ftplet.*;
import org.apache.ftpserver.usermanager.AnonymousAuthentication;
import org.apache.ftpserver.usermanager.UsernamePasswordAuthentication;
import org.apache.ftpserver.usermanager.impl.AbstractUserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class MongoUserManager extends AbstractUserManager {

    private UserRepository userRepository;

    @Autowired
    public MongoUserManager(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUserByName(String username) throws FtpException {
        Optional<User> user = userRepository.findByName(username);
        return user.orElse(null);
    }

    @Override
    public String[] getAllUserNames() throws FtpException {
        List<User> names = userRepository.findAllUserNames();
        Object[] objects = names.stream()
                .map(User::getName)
                .toArray();
        return (String[]) objects;
    }

    @Override
    public void delete(String username) throws FtpException {
        userRepository.deleteByName(username);
    }

    @Override
    public void save(User user) throws FtpException {
        MongoUser baseUser = (MongoUser) user;
        baseUser.encryptPassword();
        try {
            userRepository.save(baseUser);
        } catch (DuplicateKeyException e) {
            throw new FtpException(e.getMessage());
        }
    }

    @Override
    public boolean doesExist(String username) throws FtpException {
        Optional<User> user = userRepository.findByName(username);
        return user.isPresent();
    }

    @Override
    public User authenticate(Authentication authentication) throws AuthenticationFailedException {
        try {
            if (authentication instanceof UsernamePasswordAuthentication) {
                UsernamePasswordAuthentication upauth = (UsernamePasswordAuthentication) authentication;

                String user = upauth.getUsername();
                if (user != null) {
                    String password = upauth.getPassword();
                    if (password == null) password = "";

                    String storedPassword = getUserByName(user).getPassword();
                    if (storedPassword != null && getPasswordEncryptor().matches(password, storedPassword)) {
                        return getUserByName(user);
                    }
                }
                throw new AuthenticationFailedException("Authentication failed");

            } else if (authentication instanceof AnonymousAuthentication) {
                if (doesExist("anonymous")) return getUserByName("anonymous");
                throw new AuthenticationFailedException("Authentication failed");
            } else {
                throw new IllegalArgumentException(
                        "Authentication not supported by this user manager");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
