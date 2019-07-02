package com.tec.ftpserver.server;

import lombok.Getter;
import org.apache.ftpserver.ConnectionConfigFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Getter
public class Server {

    private UserManager userManager;
    private FtpServer ftpServer;

    @Autowired
    public Server(MongoUserManager userManager) {
        this.userManager = userManager;
        run();
    }

    public void run() {
        ListenerFactory listenerFactory = new ListenerFactory();
        listenerFactory.setPort(2221);

        ConnectionConfigFactory configFactory = new ConnectionConfigFactory();
        configFactory.setMaxLogins(100);
        configFactory.setMaxThreads(10);

        FtpServerFactory serverFactory = new FtpServerFactory();
        serverFactory.addListener("default", listenerFactory.createListener());
        serverFactory.setUserManager(userManager);
        serverFactory.setConnectionConfig(configFactory.createConnectionConfig());

        ftpServer = serverFactory.createServer();
        try {
            ftpServer.start();
        } catch (FtpException e) {
            e.printStackTrace();
        }
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public FtpServer getFtpServer() {
        return ftpServer;
    }
}
