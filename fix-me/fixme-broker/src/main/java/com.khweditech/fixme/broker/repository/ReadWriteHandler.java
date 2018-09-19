package com.khweditech.fixme.broker.repository;

import java.nio.charset.*;
import java.nio.channels.*;

import com.khweditech.MessagesHub;
import com.khweditech.fixme.broker.controllers.BrokerController;
import com.khweditech.fixme.broker.models.*;

public class ReadWriteHandler implements CompletionHandler<Integer, Attachment> {
  @Override
  public void completed(Integer result, Attachment attach) {
    if (result == -1)
    {
      attach.mainThread.interrupt();
      MessagesHub.msgCompletedError =  "Server shutdown unexpectedly, Broker going offline...";
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
        MessagesHub.msgBrokerServerResponded = "Server responded with Id: " + attach.clientId;
      }
      else
        MessagesHub.msgCompletedBrokerResponse = "Server Responded: "+ msg.replace((char)1, '^');
      try {
        boolean s = BrokerController.proccessReply(msg);
        if (s == true && BrokerController.brokerState == 1)
          BrokerController.updateData(true);
        if (s == true && BrokerController.brokerState == 0)
          BrokerController.updateData(false);
      } catch (Exception e) {
        e.printStackTrace();
      }

      attach.buffer.clear();
      msg = doChecksum(attach);
      if (msg.equals(null) || i > 1) {
        attach.mainThread.interrupt();
        return;
      }
      i++; //To stop looping, to make Thread interrupt()
      MessagesHub.msgBrokerResponse = "\nBroker response:" + msg.replace((char)1, '^');
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
  private String doChecksum(Attachment attach)
  {
    String msg;

    if (BrokerController.brokerState == 1)
      msg = BrokerController.buyProduct(BrokerController.destinatonID);
    else
      msg = BrokerController.sellProduct(BrokerController.destinatonID);
    return msg + getCheckSum(msg);

  }
  /**
   * The checksum of a FIX message is always the last field in the message.
   * It is composed of three characters and has tag 10.[4]
   * It is given by summing the ASCII value of all characters in the message,
   * except for those of the checksum field itself,
   * and performing modulo 256 over the resulting summation.[5]
   * For example, in the message above, the summation of all ASCII values (including the SOH character,
   * which has a value of 1 in the ASCII table) results in 4158.
   * Performing the modulo operation gives the value 62. Since the checksum is composed of three characters,
   * 062 is used.
   */
  private String getCheckSum(String msg)
  {
    int charTotal = 0;
    char totalArray[];
    String soh = "" + (char)1;
    //Put all '^' separated into datum[] inorder to get the length of all characters
    String datum[] = msg.split(soh);
    for(int k = 0; k < datum.length; k++)
    {
      // converting to char [];
      totalArray = datum[k].toCharArray();
      for(int i = 0; i < totalArray.length; i++)
      {
      // Converting t[] to ascii
        charTotal += (int)totalArray[i];
      }
      //Adding 1 for separator which is (char)1
      charTotal += 1;
    }
    //Adding tag 10, and SOH
    return ("10="+ (charTotal % 256) + soh);
  }

  private static int i = 0;
}