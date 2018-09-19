package com.khweditech.fixme.router.controllers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.khweditech.fixme.router.models.*;
import com.khweditech.fixme.router.repository.*;
import java.util.*;
public class RouterController
{
    private String host;//localhost
    private int port;//5000 & 5001
    //To loop Attachments class. Hence clients.add AsynchronousSocketChannel client
    private static List<Attachment> clients = new ArrayList<Attachment>();
    public RouterController(String host, int port)
    {
        this.host = host;
        this.port = port;
    }
    //AsynchronousSocketChannel client from Attachments class
    public static void addClient(Attachment client)
    {
        clients.add(client);
    }
    //Loop above private static List<Attachment> clients for 5000 & 5001
    public static Attachment getClient(int id)
    {
        for(Attachment client : clients)
            {
                if (client.clientId == id)
                    return client;
            }
            return null;
    }
    public static int getClientPosition()
    {
        return clients.size();
    }
    //Call import java.util.concurrent.ExecutorService; PDF requirement
    public void startServers()
    {
        ExecutorService threads = Executors.newCachedThreadPool();
        threads.submit(new StartServer(host, port));
        threads.submit(new StartServer(host, port + 1));
        threads.shutdown();
    }
   public static void removeClient(int id)
    {
        try
        {
            clients.remove(getClient(id));
        }
        catch(Exception e)
        {

        }
    }

}