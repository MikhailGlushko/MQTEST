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
 *	ClassicMessageSender -  ������������� ��� MQ ��� JMS 1.0, JMS 2.0
 *	��� ������ ������ ��� �������:
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
     * ³������� ����������� � �����.
     * ����� ����� ���������������, ���� �� �������� ��� ���������� � ���������: ConnectionFactory, Destination
     * �����, ��-������������, ������ ���������� Resources.CLASSIC_QUEUE
     *
     * @param message - �����������.
     * @throws JMSRuntimeException - ���� ������� �� �����.
     * @throws JMSParameterIsNullException - �������� �������� NULL
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
     * ³������� ����������� � �����.
     * ����� ����� ���������������, ���� �� �������� ��� ���������� � ���������: ConnectionFactory
     *
     * @param queueName - ����� �����
     * @param message - �����������.
     * @throws JMSRuntimeException - ���� ������� �� �����.
     * @throws NamingException - �� ���� �����
     * @throws JMSParameterIsNullException - �������� �������� NULL
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
     * ³������� ����������� � �����.
     * ����� ����� ���������������, ���� �� �������� ��� ����������� � �����, � ���������� ��������� ������������
     *
     * @param queueName - ����� �����
     * @param message - �����������.
     * @param queueMamagerName - ���� ��������� ����
     * @throws JMSRuntimeException - ���� ������� �� �����.
     * @throws NamingException - �� ���� �����
     * @throws JMSParameterIsNullException - �������� �������� NULL
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
