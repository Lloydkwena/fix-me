package com.khweditech.fixme.market.repository;

import com.khweditech.MessagesHub;
import com.khweditech.fixme.market.controllers.MarketController;
import java.nio.charset.*;
import java.nio.channels.*;
import java.io.*;
import com.khweditech.fixme.market.models.*;

public class ReadWriteHandler implements CompletionHandler<Integer, Attachment> {
    public static final String SOH = ""+(char)1;
    @Override
    public void completed(Integer result, Attachment attach) {
      if (result == -1)
      {
        attach.mainThread.interrupt();
        System.out.println("Server shutdown unexpectedly, Market going offline...");
        return ;
      }
      if (attach.isRead) {
        attach.buffer.flip();
        Charset cs = Charset.forName("UTF-8");
        int limits = attach.buffer.limit();
        byte bytes[] = new byte[limits];
        attach.buffer.get(bytes, 0, limits);
        String msg = new String(bytes, cs);
        if (attach.clientId == 0)
        {
          attach.clientId = Integer.parseInt(msg);
          MessagesHub.msgCompletedMarketResponedID =  "Server Responded with Id: "+ attach.clientId;
          attach.isRead = false;
          attach.client.read(attach.buffer, attach, this);
          //return ; //not stopping inorder to read Markets
          //Exception in thread "Thread-2" java.nio.channels.ReadPendingException
        }
        else
          MessagesHub.msgCompletedMarketResponed = "Server Responded: "+ msg + SOH;
          attach.buffer.clear();

        msg = MarketController.processRequest(msg);
        int i = 0;
        if (msg.equals(null) || i >= 1) {
              attach.mainThread.interrupt();
              return;
          }
          i++; //To stop looping, to make Thread interrupt(), If decide to active them

        try {
          MessagesHub.msgCompleteMarketResponse = "Market Response: "+ msg + SOH;
        } catch (Exception e) {
         
        }
        byte[] data = msg.getBytes(cs);
        attach.buffer.put(data);
        attach.buffer.flip();
        attach.isRead = false; // It is a write
        attach.client.write(attach.buffer, attach, this);
      }else {
        attach.isRead = true;
        attach.buffer.clear();
        attach.client.read(attach.buffer, attach, this);
      }
    }
    @Override
    public void failed(Throwable e, Attachment attach) {
      e.printStackTrace();
    }
}