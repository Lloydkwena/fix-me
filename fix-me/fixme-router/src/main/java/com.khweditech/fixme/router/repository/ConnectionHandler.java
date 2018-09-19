package com.khweditech.fixme.router.repository;

import java.nio.channels.*;
import java.nio.*;
import java.net.*;

import com.khweditech.MessagesHub;
import com.khweditech.fixme.router.LandingPage;
import com.khweditech.fixme.router.controllers.RouterController;
import com.khweditech.fixme.router.models.*;
import java.io.*;
import java.nio.charset.*;

public class ConnectionHandler implements
    CompletionHandler<AsynchronousSocketChannel, Attachment> {
      private static int clientId = 100000;//Required 6 digit ID by PDF
  @Override
  public void completed(AsynchronousSocketChannel client, Attachment attach) {
    try {
      SocketAddress clientAddr = client.getRemoteAddress();
      MessagesHub.msgAcceptedClientConn = "Accepted a  connection from " + clientAddr.toString();
      attach.server.accept(attach, this);
      ReadWriteHandler rwHandler = new ReadWriteHandler();
      Attachment newAttach = new Attachment();
      newAttach.server = attach.server;
      newAttach.client = client;
      newAttach.clientId = clientId++; //Increment ID per connection by PDF
      newAttach.buffer = ByteBuffer.allocate(2048);
      newAttach.isRead = false;
      newAttach.clientAddr = clientAddr;
      Charset cs = Charset.forName("UTF-8");
      byte data [] = Integer.toString(newAttach.clientId).getBytes(cs);
      newAttach.rwHandler = rwHandler;
      newAttach.buffer.put(data);
      newAttach.buffer.flip();
      RouterController.addClient(newAttach);
      client.write(newAttach.buffer, newAttach, rwHandler);
    } catch (IOException e) {
       e.printStackTrace();
      MessagesHub.msgCompletedError = "Errors -> " + e.getMessage();
    }
  }

  @Override
  public void failed(Throwable e, Attachment attach) {
      MessagesHub.msgCompletedError = "Failed to accept a  connection.";
   e.printStackTrace();
  }
}
