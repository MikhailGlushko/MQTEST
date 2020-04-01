package org.nbu.sz1.jms;

import javax.jms.JMSException;
import javax.naming.NamingException;

import com.ibm.mq.jms.JMSParameterIsNullException;

public interface MessageSender {

	//@Override
	void sendMessage(String message) throws JMSException, JMSParameterIsNullException;

	void sendMessage(String queue, String message) throws JMSException, NamingException, JMSParameterIsNullException;

	void sendMessage(String queueNamagerName, String queueName, String message) throws JMSException, NamingException, JMSParameterIsNullException;

}