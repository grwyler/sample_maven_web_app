/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import objects.Message;
import objects.User;

/**
 *
 * @author wlloyd
 */
public class ModelMessages {
    static final Logger logger = Logger.getLogger(ModelMessages.class.getName());
    private static ModelMessages instance;
    private Connection conn;
    
    public static ModelMessages singleton() throws Exception {
        if (instance == null) {
            instance = new ModelMessages();
        }
        return instance;
    }
    
    ModelMessages() throws Exception
    {
        Class.forName("org.postgresql.Driver");
        String dbUrl = System.getenv("JDBC_DATABASE_URL");
        if ((dbUrl == null) || (dbUrl.length() < 1))
            dbUrl = System.getProperties().getProperty("DBCONN");
        logger.log(Level.INFO, "dbUrl=" + dbUrl);  
        logger.log(Level.INFO, "attempting db connection");
        conn = DriverManager.getConnection(dbUrl);
        logger.log(Level.INFO, "db connection ok.");
    }
    
    private Connection getConnection()
    {
        return conn;
    }
    
    private Statement createStatement() throws SQLException
    {
        Connection conn = getConnection();
        if ((conn != null) && (!conn.isClosed()))
        {
            logger.log(Level.INFO, "attempting statement create");
            Statement st = conn.createStatement();
            logger.log(Level.INFO, "statement created");
            return st;
        }
        else
        {
            // Handle connection failure
        }
        return null;
    }
    
    private PreparedStatement createPreparedStatement(String sql) throws SQLException
    {
        Connection conn = getConnection();
        if ((conn != null) && (!conn.isClosed()))
        {
            logger.log(Level.INFO, "attempting statement create");
            PreparedStatement pst = conn.prepareStatement(sql);
            logger.log(Level.INFO, "prepared statement created");
            return pst;
        }
        else
        {
            // Handle connection failure
        }
        return null;
    }
    
    public Message[] getMessages() throws SQLException
    {
        LinkedList<Message> ll = new LinkedList<Message>();
        String sqlQuery ="select * from messages;";
        logger.log(Level.INFO, "Get messages wit sql=" + sqlQuery);
        Statement st = createStatement();
        ResultSet rows = st.executeQuery(sqlQuery);
        while (rows.next())
        {
            logger.log(Level.INFO, "Reading row...");
            Message msg = new Message();
            msg.setMessageId(rows.getInt("messageid"));
            msg.setUserId(rows.getInt("userid"));
            msg.setMessage(rows.getString("message"));
            msg.setDateadded(rows.getDate("dateadded"));
            logger.log(Level.INFO, "Adding message to list with id=" + msg.getMessageId());
            ll.add(msg);
        }
        logger.log(Level.INFO, "Done reading messages data...");
        return ll.toArray(new Message[ll.size()]);
    }
   
    public void deleteMessage(int messageid) throws SQLException
    {
        String sqlDelete="delete from messages where messageid=?";
        PreparedStatement pst = createPreparedStatement(sqlDelete);
        pst.setInt(1, messageid);
        pst.execute();
    }
    
    public boolean updateMessage(Message msg) throws SQLException
    {
        StringBuilder sqlQuery = new StringBuilder();
        sqlQuery.append("update messages ");
        sqlQuery.append("set userid=" + msg.getUserId()+ ", ");
        sqlQuery.append("message='" + msg.getMessage()+ "', ");
        sqlQuery.append("dateadded='" + msg.getDateadded()+ "' ");
        sqlQuery.append("where messageid=" + msg.getMessageId() + ";");
        Statement st = createStatement();
        logger.log(Level.INFO, "UPDATE SQL=" + sqlQuery.toString());
        return st.execute(sqlQuery.toString());
    }
    
    public int newMessage(Message msg) throws SQLException
    {
        String sqlInsert = (msg.getDateadded() != null ?
                "insert into messages (userid, message, dateadded) values (" + msg.getUserId() + ",'" + msg.getMessage()+ "','" + msg.getDateadded().toString() + "');" :
                "insert into messages (userid, message, dateadded) values (" + msg.getUserId() + ",'" + msg.getMessage()+ "',now());");
                
        Statement s = createStatement();
        logger.log(Level.INFO, "attempting statement execute");
        s.execute(sqlInsert,Statement.RETURN_GENERATED_KEYS);
        logger.log(Level.INFO, "statement executed.  atempting get generated keys");
        ResultSet rs = s.getGeneratedKeys();
        logger.log(Level.INFO, "retrieved keys from statement");
        int msgid = -1;
        while (rs.next())
            msgid = rs.getInt(1);   // assuming 1st column is msgid
        logger.log(Level.INFO, "The new msg id=" + msgid);
        return msgid;
    }
}
