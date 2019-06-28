package com.tec.ftpserver.server;

import org.apache.ftpserver.ftplet.*;

import java.io.IOException;

public class CustomFtplet implements Ftplet {
    @Override
    public void init(FtpletContext ftpletContext) throws FtpException {

    }

    @Override
    public void destroy() {

    }

    @Override
    public FtpletResult beforeCommand(FtpSession session, FtpRequest request) throws FtpException, IOException {
        return null;
    }

    @Override
    public FtpletResult afterCommand(FtpSession session, FtpRequest request, FtpReply reply) throws FtpException, IOException {
        return null;
    }

    @Override
    public FtpletResult onConnect(FtpSession session) throws FtpException, IOException {
        return null;
    }

    @Override
    public FtpletResult onDisconnect(FtpSession session) throws FtpException, IOException {
        return null;
    }
}
