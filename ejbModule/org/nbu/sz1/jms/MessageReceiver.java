package org.nbu.sz1.jms;

import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.jms.JMSException;
import javax.naming.NamingException;

public interface MessageReceiver {

	String receiveMessage(int timeoutInMillis) throws JMSException, TimeoutException;

	String receiveMessage(String queueName, int timeoutInMillis) throws JMSException, TimeoutException, NamingException;

	String receiveMessage(String queueNamagerName, String queueName, int timeoutInMillis)
			throws JMSException, TimeoutException, NamingException;

	List<String> receiveAll(int i) throws JMSException, NamingException;
	
	List<String> receiveAll(String string, int i) throws JMSException, NamingException;

	List<String> receiveAll(String queueMamagerName, String queueName, int timeoutInMillis) throws JMSException, NamingException;
}