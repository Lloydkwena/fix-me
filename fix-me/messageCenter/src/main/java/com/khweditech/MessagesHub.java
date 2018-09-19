package com.khweditech;

public class MessagesHub {
    //========== Router ============
    public static String msgStartRouterBroker;
    public static String msgStartRouterMarket;

    public static String msgGetServerNameBroker;
    public static String msgGetServerNameMarket;

    public static String msgAcceptedClientConn;
    public static String msgServerPortName;
    public static String msgGetServerNameStopped;

    //========== Markets ===========
    public static String msgStartMarketClient;
    public static String msgCompletedMarketResponedID;
    public static String msgCompletedMarketResponed;
    public static String msgCompleteMarketResponse;

    //========== Brokers ===========
    public static String msgProccessReplyRejected;
    public static String msgProccessReplyAccepted;
    public static String msgBrokerConnected;

    public static String msgBrokerServerResponded;
    public static String msgCompletedBrokerResponse;

    public static String msgBrokerResponse;

    //====== Errors below ===========
    public static String msgCompletedError;

    //====== Database below == Sell =====
    public static String msgDBconnected;

    public static String msgSellType;
    public static int msgSellImnt;
    public static String msgSellPrice;
    public static int msgSellQnt;
    public static int msgSellMarket;
    public static int msgSellTargetcompID;
    public static String msgSellSendingTime;
    public static int msgSellAccount;
    public static int msgSellClientOrd;

    //====== Buy =======================
    public static String msgBuyType;
    public static int msgBuyImnt;
    public static String msgBuyPrice;
    public static int msgBuyQnt;
    public static int msgBuyMarket;
    public static int msgBuyTargetcompID;
    public static String msgBuySendingTime;
    public static int msgBuyAccount;
    public static int msgBuyClientOrd;
}
