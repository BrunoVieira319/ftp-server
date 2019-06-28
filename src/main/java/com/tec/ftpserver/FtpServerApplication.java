package com.tec.ftpserver;

import com.tec.ftpserver.server.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FtpServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FtpServerApplication.class, args);
    }

}
