package com.khweditech.fixme.market.controllers;

import java.nio.*;
import java.nio.channels.*;
import java.net.*;

import com.khweditech.BuyDB;
import com.khweditech.MessagesHub;
import com.khweditech.SellDB;
import com.khweditech.fixme.market.models.*;
import com.khweditech.fixme.market.repository.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;
public class MarketController
{
    private static int qty;
    private static int price;
    private static int request;
    private static int destinatonID;
    private static final String MSGSTART = "8=FIX.4.2";
    public static final String SOH = "^"+(char)1;
    private static Attachment attach;
    public MarketController(int argsQty, int argsPrice)
    {
        qty = argsQty;
        price = argsPrice;
        try
        {
            Random rand = new Random();
            request = rand.nextInt(3) + 1;//Conditions, i.e chain of response
        }
        catch(Exception e)
        {
            //They are random that why I return any value
            request = 2;
        }
    }

    public void startClient() throws Exception
    {
        AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
        SocketAddress serverAddr = new InetSocketAddress("localhost", 5001);
        Future<Void> result = channel.connect(serverAddr);
        result.get();
        MessagesHub.msgStartMarketClient = "Markets connected...";
        attach = new Attachment();
        attach.client = channel;
        attach.buffer = ByteBuffer.allocate(2048);
        attach.isRead = true;
        attach.mainThread = Thread.currentThread();

        ReadWriteHandler readWriteHandler = new ReadWriteHandler();
        channel.read(attach.buffer, attach, readWriteHandler);
    }
    public static String processRequest(String res) {
        Map data = new HashMap();
        Iterator entries = data.entrySet().iterator();
        while (entries.hasNext()) {
            if (data.containsKey(35))
                MessagesHub.msgSellType = (String) data.get(res.split(SOH));
            else if (data.containsKey(34))
                MessagesHub.msgSellPrice = (String) data.get(res.split(SOH));
            else if (data.containsKey(43))
                MessagesHub.msgSellImnt = (Integer) data.get(res.split(SOH));
            else if (data.containsKey(49))
                MessagesHub.msgSellQnt = (Integer) data.get(res.split(SOH));
            else if (data.containsKey(50))
                MessagesHub.msgSellMarket = (Integer) data.get(res.split(SOH));
            else if (data.containsKey(56))
                MessagesHub.msgSellTargetcompID = (Integer) data.get(res.split(SOH));
            else if (data.containsKey(52))
                MessagesHub.msgSellSendingTime = (String) data.get(res.split(SOH));
            else if (data.containsKey(1))
                MessagesHub.msgSellAccount = (Integer) data.get(res.split(SOH));
            else if (data.containsKey(11))
                MessagesHub.msgSellClientOrd = (Integer) data.get(res.split(SOH));
            else if (data.containsKey("id"))
                destinatonID = (Integer) data.get(res.split(SOH));
        }
        return process(MessagesHub.msgSellType,
        MessagesHub.msgSellSendingTime,
        MessagesHub.msgSellPrice,
        MessagesHub.msgSellImnt,
        MessagesHub.msgSellQnt,
        MessagesHub.msgSellMarket,
        MessagesHub.msgSellTargetcompID,
        MessagesHub.msgSellAccount,
        MessagesHub.msgSellClientOrd);
    }
    private static String process(String msgType, String msgSendingTime,
                                  String msgPrice, int msgImnt,
                                  int msgQnt, int msgMarket,
                                  int msgTargetcompID, int msgAccount, int msgClientOrd)
    {
        int p = price;
        int q = qty;
        if (msgType.equals(35) && msgPrice.equals(34)
                && String.valueOf(msgImnt).equals(43)
                && String.valueOf(msgQnt).equals(49)
                && String.valueOf(msgMarket).equals(50)
                && String.valueOf(msgTargetcompID).equals(56)
                && String.valueOf(msgAccount).equals(1)
                && String.valueOf(msgClientOrd).equals(11) || !(msgSendingTime.isEmpty())
                && p < price && (request == 2 || request == 3))
            //Chain of response implementation by random
            return getMessage(2, Integer.parseInt(String.valueOf(qty))); //buy from broker
        else if (msgType.equals(35) && msgPrice.equals(34)
                && String.valueOf(msgImnt).equals(43)
                && String.valueOf(msgQnt).equals(49)
                && String.valueOf(msgMarket).equals(50)
                && String.valueOf(msgTargetcompID).equals(56)
                && String.valueOf(msgAccount).equals(1)
                && String.valueOf(msgClientOrd).equals(11) || !(msgSendingTime.isEmpty())
                && p >= price && qty - q >= 0 && (request == 2 || request == 3))
            //Chain of response implementation by random
            return getMessage(1, Integer.parseInt(String.valueOf(qty))); //sell to broker
        else
            return getMessage(3, Integer.parseInt(String.valueOf(qty))); //reject broker request
    }
    public static String getMessage(int code, int quant)
    {
        SellDB.getSellMessage();
        BuyDB.getRouterMessage();
        String msg = "";
         switch (code){//Chain of response implementation by random
            case 1:
                msg = "ID="+attach.clientId+SOH+ MSGSTART +SOH+MessagesHub.msgSellType
                        +SOH+MessagesHub.msgSellSendingTime +SOH+MessagesHub.msgSellPrice +SOH+
                        MessagesHub.msgSellImnt +SOH+MessagesHub.msgSellQnt +SOH+
                        +attach.clientId+SOH+MessagesHub.msgSellQnt +SOH+
                        MessagesHub.msgSellAccount +SOH+MessagesHub.msgSellClientOrd +SOH+
                        +attach.clientId+SOH+
                        MessagesHub.msgSellTargetcompID + destinatonID +SOH+" -- Can Sell -- ";
                break;
           case 2:
               msg = "ID="+attach.clientId+SOH+ MSGSTART +SOH+MessagesHub.msgBuyType
                       +SOH+MessagesHub.msgBuySendingTime +SOH+MessagesHub.msgBuyPrice +SOH+
                       MessagesHub.msgBuyImnt +SOH+MessagesHub.msgBuyQnt +SOH+
                       +attach.clientId+SOH+MessagesHub.msgBuyQnt +SOH+
                       MessagesHub.msgBuyAccount +SOH+MessagesHub.msgBuyClientOrd +SOH+
                       +attach.clientId+SOH+
                       MessagesHub.msgBuyTargetcompID + destinatonID +SOH+" -- Can Buy -- ";
                qty -= quant;
                break;
            case 3:
                msg = "ID="+attach.clientId+SOH+ MSGSTART +SOH+MessagesHub.msgSellType
                        +SOH+MessagesHub.msgSellSendingTime +SOH+MessagesHub.msgSellPrice +SOH+
                        MessagesHub.msgSellImnt +SOH+MessagesHub.msgSellQnt +SOH+
                        +attach.clientId+SOH+MessagesHub.msgSellQnt +SOH+
                        MessagesHub.msgSellAccount +SOH+MessagesHub.msgSellClientOrd +SOH+
                        +attach.clientId+SOH+
                        MessagesHub.msgSellTargetcompID + destinatonID +SOH+" -- Rejected -- ";
                qty += quant;
                break;
            default:
                MessagesHub.msgCompletedError = "";
                break;
        }
        return msg + getCheckSum(msg);
    }
    private static String getCheckSum(String msg)
    {
        int charTotal = 0;
        char totalArray[];
       // String soh = "" + (char)1;
        //Put all '^' separated into datum[] inorder to get the length of all characters
        String datum[] = msg.split(SOH);
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
        return ("10="+ (charTotal % 256) + SOH);
    }
}