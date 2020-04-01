package org.nbu.sz1.jms.mq.classic;

import java.util.Objects;

import javax.annotation.Resource;
import javax.annotation.Resource.AuthenticationType;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.JMSRuntimeException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.nbu.sz1.jms.MessageSender;
import org.nbu.sz1.jms.mq.Resources;

import com.ibm.mq.jms.JMSParameterIsNullException;

/*
 *	ClassicMessageSender -  імплементація для MQ для JMS 1.0, JMS 2.0
 *	для роботи потрібні такі ресурси:
 *		- ConnectionFactory connectionFactory // S0HPSEP100
 *		- Destination inboundQueue; 		  // SSEP
 */
//@Stateless
//@Alternative
public class ClassicMessageSender implements MessageSender{

	@Resource(lookup="S0HPSEP100",authenticationType=AuthenticationType.CONTAINER, shareable=true)
	ConnectionFactory connectionFactory;
	
	@Resource(lookup = Resources.CLASSIC_QUEUE)
	Destination outboundQueue;
	
    /**
     * Відправка повідомлення в чергу.
     * Даний метод використовується, коли всі необхідні дані налаштовані у контейнері: ConnectionFactory, Destination
     * Черга, по-замовчуванню, задана параметром Resources.CLASSIC_QUEUE
     *
     * @param message - повідомлення.
     * @throws JMSRuntimeException - немає доступу до черги.
     * @throws JMSParameterIsNullException - отримано параметр NULL
     */
	@Deprecated
	public void sendMessage(String message) 
			throws JMSException, JMSParameterIsNullException {
    	
		if(Objects.isNull(message))
    		throw new JMSParameterIsNullException("Parameter message is NULL");
    	
		try(Connection connection = connectionFactory.createConnection ()){
			connection.start();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer messageProducer = session.createProducer(outboundQueue);
			TextMessage textMessage = session.createTextMessage(message);
			messageProducer.send(textMessage) ;
		};
	}

    /**
     * Відправка повідомлення в чергу.
     * Даний метод використовується, коли всі необхідні дані налаштовані у контейнері: ConnectionFactory
     *
     * @param queueName - назва черги
     * @param message - повідомлення.
     * @throws JMSRuntimeException - немає доступу до черги.
     * @throws NamingException - не існує черги
     * @throws JMSParameterIsNullException - отримано параметр NULL
     */
	@SuppressWarnings("deprecation")
	public void sendMessage(String queueName, String message) 
			throws JMSException, NamingException, JMSParameterIsNullException {
    	
		if(Objects.isNull(message))
    		throw new JMSParameterIsNullException("Parameter message is NULL");
    	if(Objects.isNull(queueName))
    		throw new JMSParameterIsNullException("Parameter queueName is NULL");
    	
		try(Connection connection = connectionFactory.createConnection ()){
			connection.start();
			Context ctx_q = new InitialContext();
			Destination outboundQueue = (Destination) ctx_q.lookup(queueName);
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer messageProducer = session.createProducer(outboundQueue);
			TextMessage textMessage = session.createTextMessage(message);
			messageProducer.send(textMessage) ;
		};
	}
	
    /**
     * Відправка повідомлення в чергу.
     * Даний метод використовується, коли всі необхідні дані передаються в метод, і виконується програмне налаштування
     *
     * @param queueName - назва черги
     * @param message - повідомлення.
     * @param queueMamagerName - азва менеджера черг
     * @throws JMSRuntimeException - немає доступу до черги.
     * @throws NamingException - не існує черги
     * @throws JMSParameterIsNullException - отримано параметр NULL
     */
	@SuppressWarnings("deprecation")
	public void sendMessage(String queueMamagerName, String queueName, String message) 
			throws JMSException, NamingException, JMSParameterIsNullException{
    	
		if(Objects.isNull(message))
    		throw new JMSParameterIsNullException("Parameter message is NULL");
    	if(Objects.isNull(queueName))
    		throw new JMSParameterIsNullException("Parameter queueName is NULL");
    	if(Objects.isNull(queueMamagerName))
    		throw new JMSParameterIsNullException("Parameter queueMamagerName is NULL");
    	
		Context ctx_q = new InitialContext();
		ConnectionFactory connectionFactory = (ConnectionFactory) ctx_q.lookup(queueMamagerName);
		
		try(Connection connection = connectionFactory.createConnection ()){
			connection.start();
			Destination outboundQueue = (Destination) ctx_q.lookup(queueName);
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer messageProducer = session.createProducer(outboundQueue);
			TextMessage textMessage = session.createTextMessage(message);
			messageProducer.send(textMessage) ;
		}
	}
}
