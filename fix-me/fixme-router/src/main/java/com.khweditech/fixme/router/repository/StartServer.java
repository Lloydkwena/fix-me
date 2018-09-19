package com.khweditech.fixme.router.repository;

import java.nio.channels.*;
import java.net.*;

import com.khweditech.MessagesHub;
import com.khweditech.fixme.router.models.*;
public class StartServer implements Runnable
{
    private String host;
	private int port;
	public StartServer(String host, int port)
    {
        this.host = host;
        this.port = port;
    }

    @Override
    public void run()
    {
        try
        {
            AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open();//w w w  .  j  a  v  a2s .com
            InetSocketAddress sAddr = new InetSocketAddress(host, port);
            server.bind(sAddr);
            if (port % 2 == 0)
                MessagesHub.msgStartRouterBroker = "Broker Server is listening at " + sAddr.toString();
            else
                MessagesHub.msgStartRouterMarket = "Market Server is listening at "+ sAddr.toString();
            Attachment attach = new Attachment();
            attach.server = server;
            server.accept(attach, new ConnectionHandler());
            Thread.currentThread().join();
        }
        catch(Exception e)
        {
            MessagesHub.msgCompletedError = ("Note -> " + e);
        }
    }

}