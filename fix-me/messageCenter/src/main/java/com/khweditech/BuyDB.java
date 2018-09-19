package com.khweditech;
import java.sql.*;

public class BuyDB {
   // private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost/fixme_db";
    //  Database credentials
    private static final String USER = "root";
    private static final String PASS = "makwena";
    //public static void main(String[] args){
    public static void getRouterMessage(){
        Connection conn = null;
        Statement stmt = null;
        try{
            //STEP 2: Register JDBC driver
            //Class.forName("com.mysql.jdbc.Driver");

            //STEP 3: Open a connection
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            //STEP 4: Execute a query
            MessagesHub.msgDBconnected = "Connected to database: fixme_db";
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT * FROM buy";
            ResultSet rs = stmt.executeQuery(sql);

            //STEP 5: Extract data from result set
            while(rs.next()){
                //Retrieve by column name
                String type  = rs.getString("messageType");
                String instrument = rs.getString("instrument");
                int price = rs.getInt("price");
                int quantity = rs.getInt("quantity");
                int market = rs.getInt("market");
                int targetCom = rs.getInt("targetCompID");
                String sendingTime  = rs.getString("sendingTime");
                int acc = rs.getInt("account");
                int clientOrd = rs.getInt("clientOrdID");

                //Display values
                MessagesHub.msgSellType = type;
                MessagesHub.msgSellPrice = instrument;
                MessagesHub.msgSellImnt = price;
                MessagesHub.msgSellQnt = quantity;
                MessagesHub.msgSellMarket = market;
                MessagesHub.msgSellTargetcompID = targetCom;
                MessagesHub.msgSellSendingTime = sendingTime;
                MessagesHub.msgSellAccount = acc;
                MessagesHub.msgSellClientOrd = clientOrd;
            }
            //STEP 6: Clean-up environment
            rs.close();
            stmt.close();
            conn.close();
        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
        }finally{
            //finally block used to close resources
            try{
                if(stmt!=null)
                    stmt.close();
            }catch(SQLException se2){
            }// nothing we can do
            try{
                if(conn!=null)
                    conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }//end finally try
        }//end try
    }
}
