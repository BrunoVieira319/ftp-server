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
        List<User> result = userRepository.findAllUserNames();

        return result.stream()
                .map(User::getName)
                .toArray(String[]::new);
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
        if (authentication instanceof UsernamePasswordAuthentication) {
            UsernamePasswordAuthentication upauth = (UsernamePasswordAuthentication) authentication;

            String user = upauth.getUsername();
            if (user != null) {
                String password = upauth.getPassword();
                if (password == null) password = "";

                try {
                    String storedPassword = getUserByName(user).getPassword();
                    if (storedPassword != null && getPasswordEncryptor().matches(password, storedPassword)) {
                        return getUserByName(user);
                    }
                } catch (FtpException e) {
                    e.getMessage();
                }
            }
            throw new AuthenticationFailedException("Authentication failed");

        } else if (authentication instanceof AnonymousAuthentication) {
            try {
                if (doesExist("anonymous")) return getUserByName("anonymous");
            } catch (FtpException e) {
                e.printStackTrace();
            }
            throw new AuthenticationFailedException("Authentication failed");
        }
        throw new IllegalArgumentException(
                "Authentication not supported by this user manager");
    }
}
