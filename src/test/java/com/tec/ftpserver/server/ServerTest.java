package com.tec.ftpserver.server;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ServerTest {

    @Autowired
    Server ftpServer;

    @Test
    public void shouldRunFtpServer() {
        assertTrue(!ftpServer.getFtpServer().isStopped());
    }
}
