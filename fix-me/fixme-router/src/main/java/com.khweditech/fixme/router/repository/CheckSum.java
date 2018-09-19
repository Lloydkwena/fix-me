package com.khweditech.fixme.router.repository;

import com.khweditech.MessagesHub;
import com.khweditech.fixme.router.models.Attachment;

public class CheckSum
{
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

    public void performAction(Attachment attach, int resp)
    {
        if (resp != 1)
        {
            new CheckOption().performAction(attach, resp);
            return ;
        }
        int size = getMsgSize(attach.msg);
        int checksum = getCheckSum(attach.msg[attach.msg.length - 1]);
        int action;
        if (size % 256 != checksum)
            action = 3;
        else
            action = 2;
        new CheckOption().performAction(attach, action);
    }

   private int getMsgSize(String datum[])
   {
       int j = 0;
       char t[];
       for(int k = 0; k < datum.length - 1; k++)
       {
           t = datum[k].toCharArray();
           for(int i = 0; i < t.length; i++)
           {
               j += (int)t[i];
           }
           j += 1;
       }
       return (j);
   }
    private int getCheckSum(String part)
    {
        int tag, value;
        try
        {
            String ops[] = part.split("=");
            tag = Integer.parseInt(ops[0]);
            value = Integer.parseInt(ops[1]);
            if (tag == 10)
                return value;
        }
        catch(Exception e)
        {
            MessagesHub.msgCompletedError = "Checksum message passed";
        }
        return (0);
    }
}