/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.sample_maven_web_app;

import static com.mycompany.sample_maven_web_app.UserService.logger;
import data.ModelMessages;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import objects.Message;
import objects.User;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author wlloyd
 */
@Path("Messages")
public class MessageService {
    
    static final Logger logger = Logger.getLogger(MessageService.class.getName());

    public MessageService() 
    {
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getMessages() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body><style>table, th, td {font-family:Arial,Verdana,sans-serif;font-size:16px;padding: 0px;border-spacing: 0px;}</style><b>MESSAGES LIST:</b><br><br><table cellpadding=10 border=1><tr><td>Message Id</td><td>User Id</td><td>Message</td><td>Date Added</td></tr>");
        try
        {
            ModelMessages db = ModelMessages.singleton();
            Message[] msgs = db.getMessages();
            for (int i=0;i<msgs.length;i++)
                sb.append("<tr><td>" + msgs[i].getMessageId()+ "</td><td>" + msgs[i].getUserId()+ "</td><td>" + msgs[i].getMessage()+ "</td><td>" + msgs[i].getDateadded()+ "</td></tr>");
        }
        catch (Exception e)
        {
            sb.append("</table><br>Error getting messages: " + e.toString() + "<br>");
        }
        sb.append("</table></body></html>");
        return sb.toString();
    }
    
    @PUT
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public String updateMessage(String jobj) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        Message msg = mapper.readValue(jobj.toString(), Message.class);
        StringBuilder text = new StringBuilder();
        try {
            ModelMessages db = ModelMessages.singleton();
            int messageid = msg.getMessageId();
            db.updateMessage(msg);
            logger.log(Level.INFO, "update msg with messageid=" + messageid);
            text.append("Message id updated with message id=" + messageid + "\n");
        }
        catch (SQLException sqle)
        {
            String errText = "Error updating user after db connection made:\n" + sqle.getMessage() + " --- " + sqle.getSQLState() + "\n";
            logger.log(Level.SEVERE, errText);
            text.append(errText);
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Error connecting to db.");
            text.append("Error connecting to db.");
        }
        return text.toString();
    }
    
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public String deleteMessage(String jobj) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        Message msg = mapper.readValue(jobj.toString(), Message.class);
        StringBuilder text = new StringBuilder();
        try {
            ModelMessages db = ModelMessages.singleton();
            int messageid = msg.getMessageId();
            db.deleteMessage(messageid);
            logger.log(Level.INFO, "message deleted from db=" + messageid);
            text.append("Message id deleted with id=" + messageid);
        }
        catch (SQLException sqle)
        {
            String errText = "Error deleteing msg after db connection made:\n" + sqle.getMessage() + " --- " + sqle.getSQLState() + "\n";
            logger.log(Level.SEVERE, errText);
            text.append(errText);
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Error connecting to db.");
            text.append("Error connecting to db.");
        }
        return text.toString();
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public String CreateMessage(String jobj) throws IOException 
    {
        ObjectMapper mapper = new ObjectMapper();
        Message msg = mapper.readValue(jobj.toString(), Message.class);
        StringBuilder text = new StringBuilder();
        
        try {
            ModelMessages db = ModelMessages.singleton();
            int msgid = db.newMessage(msg);
            logger.log(Level.INFO, "msg persisted to db with msgid=" + msgid);
            text.append("Msg id persisted with id=" + msgid);
        }
        catch (SQLException sqle)
        {
            String errText = "Error persisting user after db connection made:\n" + sqle.getMessage() + " --- " + sqle.getSQLState() + "\n";
            logger.log(Level.SEVERE, errText);
            text.append(errText);
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Error connecting to db.");
        }
        
        return text.toString();
    }
    
}
