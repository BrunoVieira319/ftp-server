package com.tec.ftpserver.server;

import com.tec.ftpserver.repository.UserRepository;
import org.apache.ftpserver.ftplet.*;
import org.apache.ftpserver.usermanager.AnonymousAuthentication;
import org.apache.ftpserver.usermanager.UsernamePasswordAuthentication;
import org.apache.ftpserver.usermanager.impl.AbstractUserManager;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CustomUserManager extends AbstractUserManager {

    private UserRepository userRepository;

    @Autowired
    public CustomUserManager(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUserByName(String username) throws FtpException {
        Optional<User> user = userRepository.findByName(username);
        if(user.isPresent()) return user.get();
        return null;
    }

    @Override
    public String[] getAllUserNames() throws FtpException {
        List<User> allUserNames = userRepository.findAllUserNames();
        String[] names = (String[]) allUserNames.stream().map(User::getName).toArray();
        return names;
    }

    @Override
    public void delete(String username) throws FtpException {
        userRepository.deleteByName(username);
    }

    @Override
    public void save(User user) throws FtpException {
        BaseUser baseUser = (BaseUser) user;
        String encryptPassword = getPasswordEncryptor().encrypt(user.getPassword());
        baseUser.setPassword(encryptPassword);

        userRepository.save(baseUser);
    }

    @Override
    public boolean doesExist(String username) throws FtpException {
        Optional<User> user = userRepository.findByName(username);
        if (user.isPresent()) return true;
        return false;
    }

    @Override
    public User authenticate(Authentication authentication) throws AuthenticationFailedException {
        try {
            if (authentication instanceof UsernamePasswordAuthentication) {
                UsernamePasswordAuthentication upauth = (UsernamePasswordAuthentication) authentication;

                String user = upauth.getUsername();
                String password = upauth.getPassword();

                if (user == null) {
                    throw new AuthenticationFailedException("Authentication failed");
                }

                if (password == null) {
                    password = "";
                }

                String storedPassword = getUserByName(user).getPassword();
                System.out.println(storedPassword);
                System.out.println(password);

                if (storedPassword == null) {
                    throw new AuthenticationFailedException("Authentication failed");
                }

                if (getPasswordEncryptor().matches(password, storedPassword)) {
                    return getUserByName(user);
                } else {
                    throw new AuthenticationFailedException("Authentication failed");
                }

            } else if (authentication instanceof AnonymousAuthentication) {
                if (doesExist("anonymous")) {
                    return getUserByName("anonymous");
                } else {
                    throw new AuthenticationFailedException("Authentication failed");
                }
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
