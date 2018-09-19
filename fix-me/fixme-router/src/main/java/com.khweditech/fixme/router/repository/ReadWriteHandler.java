package com.khweditech.fixme.router.repository;

import com.khweditech.MessagesHub;
import com.khweditech.fixme.router.controllers.RouterController;
import java.io.*;
import com.khweditech.fixme.router.models.*;
import java.nio.channels.*;
import java.nio.charset.*;

public class ReadWriteHandler implements CompletionHandler<Integer, Attachment> {
    private String SOH; //From my FIX tutorial is ^
    public ReadWriteHandler()
    {
      SOH = "^" + (char)1;
    }
    @Override
    public void completed(Integer result, Attachment attach) {
      //Result -1 to limit, or programming stop
      if (result == -1) {
        try {
          attach.client.close();
          RouterController.removeClient(attach.clientId);
          String port = attach.server.getLocalAddress().toString().split(":")[1];
          MessagesHub.msgGetServerNameStopped = "[" + getServerName(port) + "]Stopped   listening to the   client %s%n"+
                  attach.clientAddr;
        } catch (IOException completedError) {
            MessagesHub.msgCompletedError = completedError.getMessage();
        }
        return;
      }
  
      if (attach.isRead) {
        attach.buffer.flip();
        int limits = attach.buffer.limit();
        byte bytes[] = new byte[limits];
        attach.buffer.get(bytes, 0, limits);
        Charset cs = Charset.forName("UTF-8");
        String msg = new String(bytes, cs);
        String datum[] = msg.split(SOH);
        attach.msg = datum;
        try
        {
            //== Get Read write Server [0], i.e both Market and Broker
            String port = attach.server.getLocalAddress().toString().split(":")[1];
            MessagesHub.msgServerPortName = "["+ getServerName(port) +"]Client at says: "+
                    attach.clientAddr + SOH + msg + SOH;
        }
        catch(Exception e)
        {
            MessagesHub.msgCompletedError = "No communication between clients" + e.toString();
        }
        attach.isRead = false; // It is a write
        attach.buffer.rewind();
        attach.buffer.clear();
        byte[] data = msg.getBytes(cs);
        attach.buffer.put(data);
        attach.buffer.flip();
        if (attach.client.isOpen() && RouterController.getClientPosition() > 1)
        {
            new CheckSum().performAction(attach, 1);
        }

      } else {
        attach.isRead = true;
        attach.buffer.clear();
        attach.client.read(attach.buffer, attach, this);

      }
    }
    @Override
    public void failed(Throwable e, Attachment attach) {
      e.printStackTrace();
    }
    private String getServerName(String port)
    {
        if (port.equals("5000")) {
            return MessagesHub.msgGetServerNameBroker = "Broker Server ->";
        }
        else {
            return MessagesHub.msgGetServerNameMarket = "Market Server ->";
        }
    }
  }