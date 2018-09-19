package com.khweditech.fixme.router.repository;

import com.khweditech.MessagesHub;
import com.khweditech.fixme.router.LandingPage;
import com.khweditech.fixme.router.controllers.RouterController;
import com.khweditech.fixme.router.models.Attachment;

public class CheckOption {
    public void performAction(Attachment attach, int resp)
    {
        //To check chain of responsibility: 1, 2, and 3 options
        if (resp != 2)
        {
            attach.isRead = false;
            attach.client.write(attach.buffer, attach, attach.rwHandler);
            return ;
        }
        int id = getDestination(attach.msg);
        int srcId = getSource(attach.msg);
        if (srcId != attach.clientId && resp == 3)
        {
            MessagesHub.msgServerPortName = "src = " + srcId + " clientId = "+ attach.clientId+'\n';
            attach.isRead = false;
            attach.client.write(attach.buffer, attach, attach.rwHandler);
            return ;
        }  
        try
        {
            if (attach.client.isOpen() && RouterController.getClientPosition() > 1)
            {
                Attachment att = RouterController.getClient(id);
                if (att == null && resp == 3)
                {
                    attach.isRead = false;
                    attach.client.write(attach.buffer, attach, attach.rwHandler);
                    return ;
                }
                att.isRead = false;
                att.client.write(attach.buffer, att, attach.rwHandler);
            }
        }
        catch(Exception e)
        {
            attach.isRead = false;
            attach.client.write(attach.buffer, attach, attach.rwHandler);
        }
    }
    private int getDestination(String datum[])
    {
        try
        {
            for(int i = 0; i < datum.length; i++)
            {
                if (datum[i].contains("35"))
                    return Integer.parseInt(datum[i].split("=")[1]);
            }
        }
        catch(Exception e)
        {

        }
        return -1;
    }
    private int getSource(String datum[])
    {
        try
        {
            if (datum[0].split("=")[0].equalsIgnoreCase("id"))
                return Integer.parseInt(datum[0].split("=")[1]);
        }
        catch(Exception e)
        {
        }
        return -1;
    }
}
