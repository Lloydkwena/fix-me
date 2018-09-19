package com.khweditech.fixme.broker.controllers;

import java.nio.*;
import java.nio.channels.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

import com.khweditech.BuyDB;
import com.khweditech.MessagesHub;
import com.khweditech.SellDB;
import com.khweditech.fixme.broker.models.*;
import com.khweditech.fixme.broker.repository.*;
public class BrokerController
{
    private static int qty = 15;
    private static int cash = 1000;
    private static Attachment attach;
    private static final String MSGSTART = "8=FIX.4.2";
    public static final String SOH = ""+(char)1;
    public static int brokerState;
    public static int destinatonID;
    public BrokerController(int portID, int option)
    {
        destinatonID = portID;
        brokerState = option;
    }
    public void startClient() throws Exception
    {
        AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
        SocketAddress serverAddr = new InetSocketAddress("localhost", 5000);
        Future<Void> result = channel.connect(serverAddr);
        result.get();
        MessagesHub.msgBrokerConnected = "Brokers connected...";
        attach = new Attachment();
        attach.client = channel;
        attach.buffer = ByteBuffer.allocate(2048);
        attach.isRead = true;
        
        attach.mainThread = Thread.currentThread();

        ReadWriteHandler readWriteHandler = new ReadWriteHandler();
        channel.read(attach.buffer, attach, readWriteHandler);
    }
    public static String sellProduct(int dst)
    {
        SellDB.getSellMessage();
        Map sell = new HashMap();
        sell.put("", MSGSTART + SOH);
        sell.put("ID", attach.clientId + SOH);
        sell.put(35, MessagesHub.msgSellType + SOH);
        sell.put(55, MessagesHub.msgSellImnt + SOH + dst + SOH);
        sell.put(34, MessagesHub.msgSellPrice + SOH);
        sell.put(49, MessagesHub.msgSellQnt + SOH);
        sell.put(50, MessagesHub.msgSellMarket + SOH);
        sell.put(56, MessagesHub.msgSellTargetcompID + SOH);
        sell.put(52, MessagesHub.msgSellSendingTime + SOH);
        sell.put(1, MessagesHub.msgSellAccount + SOH);
        sell.put(11, MessagesHub.msgSellClientOrd + SOH);

        if (qty > 0)
           return sell.entrySet().toString();
        else
           return sell.toString();
    }
    public static String buyProduct(int dst)
    {
        BuyDB.getRouterMessage();
        Map buy = new HashMap();
        buy.put("", MSGSTART + SOH);
        buy.put("ID", attach.clientId + SOH);
        buy.put(35, MessagesHub.msgSellType + SOH);
        buy.put(55, MessagesHub.msgSellImnt + SOH + dst + SOH);
        buy.put(34, MessagesHub.msgSellPrice + SOH);
        buy.put(49, MessagesHub.msgSellQnt + SOH);
        buy.put(50, MessagesHub.msgSellMarket + SOH);
        buy.put(56, MessagesHub.msgSellTargetcompID + SOH);
        buy.put(52, MessagesHub.msgSellSendingTime + SOH);
        buy.put(1, MessagesHub.msgSellAccount + SOH);
        buy.put(11, MessagesHub.msgSellClientOrd + SOH);

        if (cash > 0)
           return buy.entrySet().toString();
        else
           return buy.toString();
    }

    public static boolean proccessReply(String reply)
    {
        SellDB.getSellMessage();
        BuyDB.getRouterMessage();
        Map data = new HashMap();
        data.get(reply.split(SOH));
        if ((data.containsKey(35) && data.containsValue(MessagesHub.msgSellType))){
            MessagesHub.msgProccessReplyRejected = "\nMarket[" + destinatonID + "] Rejected Order\n";
            return false;
        }
        if ((data.containsKey(49) && data.containsValue(MessagesHub.msgSellQnt))){
            MessagesHub.msgProccessReplyAccepted = "\nMarket[" + destinatonID + "] Accepted Order\n";
            return true;
        }
        return false;
    }
    public static void updateData(boolean state)
    {
        if (state == false)
        {
            qty -= 3;
            cash += 200;
        }
        else
        {
            qty += 3;
            cash -= 190;
        }   
    }
}