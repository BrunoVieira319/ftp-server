package com.tec.ftpserver.domain;

import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.AuthorizationRequest;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginRequest;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.apache.ftpserver.usermanager.impl.WriteRequest;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class MongoUserTest {

    @After
    public void deleteCreatedFolderForUserFiles() {
        new File(String.format("%s/ftp-server/%s", System.getProperty("user.home"), "João")).delete();
    }

    @Test
    public void shouldCreateMongoUser() {
        List<Authority> authorities = new LinkedList<>();
        authorities.add(new WritePermission());
        authorities.add(new ConcurrentLoginPermission(1, 1));
        MongoUser user = new MongoUser("João", "123", authorities);

        assertNull(user.getId());
        assertEquals("João", user.getName());
        assertEquals("123", user.getPassword());
        assertEquals(authorities, user.getAuthorities());
        assertEquals(1, user.getAuthorities(WritePermission.class).size());
        assertEquals(0, user.getMaxIdleTime());
        assertTrue(user.getEnabled());
        assertEquals(String.format("%s/ftp-server/%s", System.getProperty("user.home"), "João"), user.getHomeDirectory());
        assertTrue(new File(user.getHomeDirectory()).isDirectory());

    }

    @Test
    public void shouldEncryptUserPassword() {
        List<Authority> authorities = new LinkedList<>();
        MongoUser user = new MongoUser("João", "123", authorities);

        user.encryptPassword();
        assertEquals("202CB962AC59075B964B07152D234B70", user.getPassword());
    }

    @Test
    public void shouldAuthorizeUser() {
        List<Authority> authorities = new LinkedList<>();
        authorities.add(new WritePermission());
        MongoUser user = new MongoUser("João", "123", authorities);

        AuthorizationRequest request = user.authorize(new WriteRequest());

        assertNotNull(request);
    }

    @Test
    public void shouldNotAuthorizeUser() {
        List<Authority> authorities = new LinkedList<>();
        authorities.add(new WritePermission());
        MongoUser user = new MongoUser("João", "123", authorities);

        AuthorizationRequest request = user.authorize(new ConcurrentLoginRequest(1,1));

        assertNull(request);
    }

}
