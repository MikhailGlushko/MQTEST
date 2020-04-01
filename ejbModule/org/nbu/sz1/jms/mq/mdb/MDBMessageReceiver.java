package org.nbu.sz1.jms.mq.mdb;

import java.util.Arrays;

import javax.annotation.Resource;
import javax.annotation.Resource.AuthenticationType;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.BytesMessage;

/**
 * Message-Driven Bean implementation class for: MyMessageBean
 */
@MessageDriven(activationConfig = { 
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class MDBMessageReceiver implements MessageListener {

	@Resource
    private MessageDrivenContext mdCtx;
	
	@Resource(lookup="S0HPSEP100",authenticationType=AuthenticationType.CONTAINER, shareable=true)
	private ConnectionFactory connectionFactoryRef;
	
	@Resource(name = "SSEP_OUT")
    private Destination resourceDestRef;
	
    /**
     * Default constructor. 
     */
    public MDBMessageReceiver() {
        // TODO Auto-generated constructor stub
    }
	
	/**
     * @see MessageListener#onMessage(Message)
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void onMessage(Message message) {
        // TODO Auto-generated method stub
    	String name = null;
        try {
            // Retrieve request
            name = ((TextMessage) message).getText();
            System.out.println("Get message(MDB): "+name);
        } catch (JMSException e) {
            e.printStackTrace();
            mdCtx.setRollbackOnly();
        }
        
        try {
            // Process request
            String responseData = "Hello, " + name;
            
            // Create and send response message
            sendResponse(message, responseData);
        } catch (JMSException e) {
            e.printStackTrace();
            // TODO error handling goes here
            // consider producing a copy of the message onto a dead letter queue
            // (the same way a message is produced onto the response queue, but without expiry)
            mdCtx.setRollbackOnly();
        } catch (Exception e) {
            // NOTE: Do not allow the MDB the throw an exception or it will stop processing messages
            e.printStackTrace();
            
            // TODO error handling goes here
            // consider producing a copy of the message onto a dead letter queue
            // (the same way a message is produced onto the response queue, but without expiry)
        }
    }
    
    private void sendResponse(Message requestMessage, String responseData) throws JMSException {
        Connection jmsConnection = null;
        Session session = null;
        
        try {
            jmsConnection = connectionFactoryRef.createConnection();
            session = jmsConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            
            TextMessage responseMessage = session.createTextMessage();
            // ! Set correlation id from our original message id
            responseMessage.setJMSCorrelationID(requestMessage.getJMSMessageID());
            responseMessage.setText(responseData);
            
            MessageProducer mp = session.createProducer(null);
            // use 30 minute expiry
            mp.setTimeToLive(1000 * 60 * 30);
            mp.send(resourceDestRef, responseMessage);
            System.out.println("Save message(MDB): "+responseData);
        } finally {
            try {
                if (session != null) {
                    session.close();
                }
            } catch (JMSException e) {
                
            }
            
            try {
                if (jmsConnection != null) {
                    jmsConnection.close();
                }
            } catch (JMSException e) {
                
            }
        }
    }

}
