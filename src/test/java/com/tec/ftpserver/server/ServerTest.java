package com.tec.ftpserver.server;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ServerTest {

    @Mock
    MongoUserManager userManager;

    @Test
    public void shouldRunFtpServer() {
        Server ftpServer = new Server(userManager);

        assertEquals(userManager, ftpServer.getUserManager());
        assertTrue(!ftpServer.getFtpServer().isStopped());

        ftpServer.getFtpServer().stop();
    }
}
