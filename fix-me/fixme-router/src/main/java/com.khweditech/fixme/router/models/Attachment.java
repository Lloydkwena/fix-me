package com.khweditech.fixme.router.models;

import com.khweditech.fixme.router.repository.ReadWriteHandler;
import java.nio.channels.*;
import java.nio.*;
import java.net.*;

public class Attachment
{
    public AsynchronousServerSocketChannel server;
    public AsynchronousSocketChannel client;
    public int clientId;
    public ByteBuffer buffer;
    public SocketAddress clientAddr;
    public boolean isRead;
    //FIX below
    public String msg[];
    public ReadWriteHandler rwHandler;

}
  