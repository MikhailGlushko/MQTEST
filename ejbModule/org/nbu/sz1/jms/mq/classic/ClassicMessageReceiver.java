package org.nbu.sz1.jms.mq.classic;


import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeoutException;
import javax.annotation.Resource;
import javax.annotation.Resource.AuthenticationType;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.JMSRuntimeException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.nbu.sz1.jms.MessageReceiver;
import org.nbu.sz1.jms.mq.Resources;

import com.ibm.mq.jms.JMSNotSupportedException;
import com.ibm.mq.jms.JMSParameterIsNullException;

/*
 *	ClassicMessageReceiver -  імплементація для MQ для JMS 1.0, JMS 2.0
 *	для роботи потрібні такі ресурси:
 *		- ConnectionFactory connectionFactory // S0HPSEP100
 *		- Destination inboundQueue; 		  // SSEP
 */
//@Stateless
//@Alternative
public class ClassicMessageReceiver implements MessageReceiver {

	@Resource(lookup="S0HPSEP100",authenticationType=AuthenticationType.CONTAINER, shareable=true)
	ConnectionFactory connectionFactory;
	
	@Resource(lookup = Resources.CLASSIC_QUEUE)
	Queue inboundQueue;

    /**
     * отримуємо повідомлення з черги, якщо повідомлення немає в черзі, чекаємо зазначений час. 
     * Якщо задати час очікуваняя 0 - чекаємо поки не зявиться повідомлення
     * Даний метод використовується, коли всі необхідні дані налаштовані у контейнері: ConnectionFactory, Queue
     * Черга, по-замовчуванню, задана параметром Resources.CLASSIC_QUEUE
     *
     * @param timeoutInMillis  - час очікування повідомлення, до отримання exception.
     * @return - текст повідомлення.
     * @throws JMSRuntimeException - помолка доступу до черги.
     * @throws TimeoutException - час очікування вичерпано.
     */
	@Deprecated
	public String receiveMessage(int timeoutInMillis) 
			throws JMSException, TimeoutException {
        
		String response = null;
        try (Connection connection = connectionFactory.createConnection()) {
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer messageConsumer = session.createConsumer(inboundQueue);
            Message message = messageConsumer.receive(timeoutInMillis);
            if (message == null) {
                throw new TimeoutException("No message received after " + timeoutInMillis + "ms");
            }
            response = message.getBody(String.class);
        }
        return response;
    }

	   /**
     * отримуємо повідомлення з черги, якщо повідомлення немає в черзі, чекаємо зазначений час. 
     * Якщо задати час очікуваняя 0 - чекаємо поки не зявиться повідомлення
     * Даний метод використовується, коли всі необхідні дані налаштовані у контейнері: ConnectionFactory
     *
     * @param timeoutInMillis  - час очікування повідомлення, до отримання exception.
     * @param queueName - назва черги
     * @return - текст повідомлення.
     * @throws JMSRuntimeException - помолка доступу до черги.
     * @throws TimeoutException - час очікування вичерпано
     * @throws NamingException - черга не знайдена в налаштуваннях контейнера
     * @throws JMSParameterIsNullException - отримано параметр NULL 
     */
	@SuppressWarnings("deprecation")
	public String receiveMessage(String queueName, int timeoutInMillis) 
			throws JMSException, TimeoutException, NamingException, JMSParameterIsNullException {
		
    	if(Objects.isNull(queueName))
    		throw new JMSParameterIsNullException("Parameter queueName is NULL");
    	
		String response = null;
		try (Connection connection = connectionFactory.createConnection()) {
            connection.start();
            Context ctx_q = new InitialContext();
			Destination inboundQueue = (Destination) ctx_q.lookup(queueName);
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer messageConsumer = session.createConsumer(inboundQueue);
            Message message = messageConsumer.receive(timeoutInMillis);
            if (message == null) {
                throw new TimeoutException("No message received after " + timeoutInMillis + "ms");
            }
            response = message.getBody(String.class);
        }
		return response;
	}
	
    /**
     * отримуємо повідомлення з черги, якщо повідомлення немає в черзі, чекаємо зазначений час. 
     * Якщо задати час очікуваняя 0 - чекаємо поки не зявиться повідомлення
     * Даний метод використовується, коли всі необхідні дані передаються в метод, і виконується програмне налаштування
     *
     * @param timeoutInMillis  - час очікування повідомлення, до отримання exception.
     * @param queueName - назва черги
     * @return - текст повідомлення.
     * @throws JMSRuntimeException - помолка доступу до черги.
     * @throws TimeoutException - час очікування вичерпано
     * @throws NamingException - черга не знайдена в налаштуваннях контейнера
     * @throws JMSParameterIsNullException - отримано параметр NULL 
     */
	@SuppressWarnings("deprecation")
	public String receiveMessage(String queueMamagerName, String queueName, int timeoutInMillis) 
			throws JMSException, TimeoutException, NamingException, JMSParameterIsNullException {
		
		if(Objects.isNull(queueName))
    		throw new JMSParameterIsNullException("Parameter queueName is NULL");
    	if(Objects.isNull(queueMamagerName))
    		throw new JMSParameterIsNullException("Parameter queueMamagerName is NULL");
    	
		String response = null;
		Context ctx_q = new InitialContext();
		ConnectionFactory connectionFactory = (ConnectionFactory) ctx_q.lookup(queueMamagerName);
		
		try (Connection connection = connectionFactory.createConnection()) {
            connection.start();
			Destination inboundQueue = (Destination) ctx_q.lookup(queueName);
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer messageConsumer = session.createConsumer(inboundQueue);
            Message message = messageConsumer.receive(timeoutInMillis);
            if (message == null) {
                throw new TimeoutException("No message received after " + timeoutInMillis + "ms");
            }
            response = message.getBody(String.class);
        }
		return response;
	}

	/**
	 * Прочитати всі повідомлення з черги
	 * Даний метод використовується, коли всі необхідні дані налаштовані у контейнері: ConnectionFactory, Queue
	 */
	@Deprecated
	public List<String> receiveAll(int i) throws JMSException, NamingException, JMSNotSupportedException {
		// TODO Auto-generated method stub
		throw new JMSNotSupportedException("Method not supported");
	}
	
	/**
	 * Прочитати всі повідомлення з черги
	 * Даний метод використовується, коли всі необхідні дані налаштовані у контейнері: ConnectionFactory
	 */
	@Deprecated
	public List<String> receiveAll(String string, int i) throws JMSException, NamingException, JMSNotSupportedException {
		// TODO Auto-generated method stub
		throw new JMSNotSupportedException("Method not supported");
	}

	/**
	 * Прочитати всі повідомлення з черги
	 * Даний метод використовується, коли всі необхідні дані передаються в метод, і виконується програмне налаштування
	 */
	@Deprecated
	public List<String> receiveAll(String queueMamagerName, String queueName, int timeoutInMillis)
			throws JMSException, NamingException {
		// TODO Auto-generated method stub
		return null;
	}
}
