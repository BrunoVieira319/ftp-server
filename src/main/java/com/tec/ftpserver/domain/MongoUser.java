package com.tec.ftpserver.domain;

import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.AuthorizationRequest;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.util.EncryptUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Document
public class MongoUser implements User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String name;
    private String password;
    private int maxIdleTimeSec;
    private String homeDir;
    private boolean isEnabled;
    private List<? extends Authority> authorities;

    public MongoUser(String name, String password, List<Authority> authorities) {
        this.name = name;
        this.password = password;
        this.authorities = authorities;
        this.maxIdleTimeSec = 0;
        this.isEnabled = true;
        String createdDir = createUserDirectory();
        this.homeDir = createdDir;
    }

    private String createUserDirectory() {
        Path path = Paths.get(String.format("%s/ftp-server/%s", System.getProperty("user.home"), this.name));
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return path.toString();
    }

    public String getId() {
        return id;
    }


    public void encryptPassword() {
        this.password = EncryptUtils.encryptMD5(this.password);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public List<? extends Authority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public List<? extends Authority> getAuthorities(Class<? extends Authority> clazz) {
        return Collections.unmodifiableList(
                authorities.stream()
                        .filter(a -> a.getClass().equals(clazz))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public AuthorizationRequest authorize(AuthorizationRequest request) {
        if (authorities == null) return null;

        for (Authority authority : authorities) {
            if (authority.canAuthorize(request)) {
                request = authority.authorize(request);
                if (request != null) return request;
            }
        }

        return null;
    }

    @Override
    public int getMaxIdleTime() {
        return this.maxIdleTimeSec;
    }

    @Override
    public boolean getEnabled() {
        return this.isEnabled;
    }

    @Override
    public String getHomeDirectory() {
        return this.homeDir;
    }
}
