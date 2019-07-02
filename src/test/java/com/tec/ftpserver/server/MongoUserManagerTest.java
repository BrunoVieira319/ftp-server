package com.tec.ftpserver.server;

import com.tec.ftpserver.domain.MongoUser;
import com.tec.ftpserver.repository.UserRepository;
import org.apache.ftpserver.ftplet.AuthenticationFailedException;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.usermanager.AnonymousAuthentication;
import org.apache.ftpserver.usermanager.UsernamePasswordAuthentication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DuplicateKeyException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MongoUserManagerTest {

    @Mock
    UserRepository repository;

    @InjectMocks
    MongoUserManager userManager;

    private MongoUser userTest;

    @Before
    public void createUserForTests() {
        userTest = new MongoUser("João", "123", null);
    }

    @Test
    public void shouldSaveUser() throws FtpException {
        when(repository.save(any(MongoUser.class))).thenReturn(userTest);
        userManager.save(userTest);

        assertEquals("202CB962AC59075B964B07152D234B70", userTest.getPassword());
        verify(repository, times(1)).save(any());
    }

    @Test(expected = FtpException.class)
    public void shouldNotSaveUsersWithSameName() throws FtpException {
        when(repository.save(any(MongoUser.class))).thenThrow(new DuplicateKeyException(""));
        userManager.save(userTest);
    }

    @Test
    public void shouldFetchUserByName() throws FtpException {
        when(repository.findByName(anyString())).thenReturn(Optional.of(userTest));
        User user = userManager.getUserByName("João");

        assertEquals(userTest, user);
        verify(repository, times(1)).findByName(anyString());
    }

    @Test
    public void shouldFetchAllUserNames() throws FtpException {
        List<User> users = new ArrayList<>();
        IntStream.range(0, 10).forEach(i -> {
                    users.add(new MongoUser("User:" + i, null, null));
                }
        );
        when(repository.findAllUserNames()).thenReturn(users);
        String[] names = userManager.getAllUserNames();

        assertEquals(10, names.length);
        assertEquals("User:0", names[0]);
        assertEquals("User:9", names[9]);
        verify(repository, times(1)).findAllUserNames();
    }

    @Test
    public void shouldDeleteUserByName() throws FtpException {
        doNothing().when(repository).deleteByName(anyString());

        userManager.delete("João");
        verify(repository, times(1)).deleteByName(anyString());
    }

    @Test
    public void shouldVerifyIfUserExists() throws FtpException {
        when(repository.findByName(anyString())).thenReturn(Optional.of(userTest));

        assertTrue(userManager.doesExist("João"));
        verify(repository, times(1)).findByName(anyString());
    }

    @Test
    public void shouldAuthenticateWithSuccessWhenUserIsValid() throws AuthenticationFailedException {
        userTest.encryptPassword();
        when(repository.findByName(anyString())).thenReturn(Optional.of(userTest));

        UsernamePasswordAuthentication upauth = new UsernamePasswordAuthentication("João", "123");
        User authorized = userManager.authenticate(upauth);

        assertNotNull(authorized);
        assertEquals(userTest, authorized);
    }

    @Test(expected = AuthenticationFailedException.class)
    public void shouldFailAuthenticationWhenUserIsInvalid() throws AuthenticationFailedException {
        userTest.encryptPassword();
        when(repository.findByName(anyString())).thenReturn(Optional.of(userTest));

        UsernamePasswordAuthentication upauth = new UsernamePasswordAuthentication("João", "ABCDEFGH");
        userManager.authenticate(upauth);
    }

    @Test
    public void shouldAuthenticateAsAnonymous() throws AuthenticationFailedException {
        when(repository.findByName("anonymous")).thenReturn(Optional.of(userTest));

        AnonymousAuthentication anonymousAuthentication = new AnonymousAuthentication();
        User anonymous = userManager.authenticate(anonymousAuthentication);

        assertNotNull(anonymous);
    }

    @Test(expected = AuthenticationFailedException.class)
    public void shouldNotAuthenticateAsAnonymousIfThereIsNotAnonymousUserInFtpServer()
            throws AuthenticationFailedException {
        when(repository.findByName("anonymous")).thenReturn(Optional.ofNullable(null));

        AnonymousAuthentication anonymousAuthentication = new AnonymousAuthentication();
        userManager.authenticate(anonymousAuthentication);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenAuthenticateWithDifferentThanUserPasswordAndAnonymous()
            throws AuthenticationFailedException {
        userManager.authenticate(null);
    }
}
