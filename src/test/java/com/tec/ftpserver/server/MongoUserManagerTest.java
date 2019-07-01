package com.tec.ftpserver.server;

import com.tec.ftpserver.domain.MongoUser;
import com.tec.ftpserver.repository.UserRepository;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DuplicateKeyException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MongoUserManagerTest {

    @Mock
    UserRepository repository;

    @InjectMocks
    MongoUserManager userManager;

    MongoUser userTest;

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
        verify(repository, times(1)).findAllUserNames();

    }
}
