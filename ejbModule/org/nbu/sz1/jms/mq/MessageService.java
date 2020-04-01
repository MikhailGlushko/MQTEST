package org.nbu.sz1.jms.mq;

import java.util.LinkedList;
import java.util.List;

import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.JMSRuntimeException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 * Використання JMS 1.1/JMS2.0 API для відправки та отримання повідомлень
 * 
 * @author Mykhailo Hlushko
 */
@Stateless
public class MessageService {

	/**
	 * Відправка повідомлення з використанням JMS 1.1
	 * 
	 * @param connectionFactory
	 * @param queue
	 * @param text
	 * @throws JMSException
	 */
	public void sendMessageJMS11(ConnectionFactory connectionFactory, Queue queue, String text) throws JMSException {
		try (Connection connection = connectionFactory.createConnection()) {
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer messageProducer = session.createProducer(queue);
			TextMessage textMessage = session.createTextMessage(text);
			messageProducer.send(textMessage);
		}
	}

	/**
	 * Відправка текстового повідомлення з використанням JMS 2.0
	 * 
	 * @param connectionFactory
	 * @param queue
	 * @param messageBody	-	повідомлення, тип String, Message, byte[]
	 * @throws JMSException
	 */
	public void sendMessageJMS20(ConnectionFactory connectionFactory, Queue queue, Message messageBody)
			throws JMSRuntimeException {
		try (JMSContext context = connectionFactory.createContext()) {
			JMSProducer jmsProducer = context.createProducer();
			jmsProducer.send(queue, messageBody);
		}
	}
	
	public void sendMessageJMS20(ConnectionFactory connectionFactory, Queue queue, String messageBody)
			throws JMSRuntimeException {
		try (JMSContext context = connectionFactory.createContext()) {
			JMSProducer jmsProducer = context.createProducer();
			jmsProducer.send(queue, messageBody);
		}
	}
	
	public void sendMessageJMS20(ConnectionFactory connectionFactory, Queue queue, byte[] messageBody)
			throws JMSRuntimeException {
		try (JMSContext context = connectionFactory.createContext()) {
			JMSProducer jmsProducer = context.createProducer();
			jmsProducer.send(queue, messageBody);
		}
	}

	/**
	 * Отримання повідомлення з використанням JMS 1.1
	 * 
	 * @param connectionFactory
	 * @param queue
	 * @param text
	 * @param timeoutInMillis
	 * @throws JMSException
	 * @return String			- повідомлення, тип String
	 */
	public String receiveMessageJMS11(ConnectionFactory connectionFactory, Queue queue, int timeoutInMillis)
			throws JMSException {
		String body = null;
		try (Connection connection = connectionFactory.createConnection()) {
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageConsumer messageConsumer = session.createConsumer(queue);
			connection.start();
			TextMessage textMessage = (TextMessage) messageConsumer.receive(timeoutInMillis);
			body = textMessage.getText();
		}
		return body;
	}

	/**
	 * Отримання повідомлення з використанням JMS 2.0
	 * 
	 * @param T					-	тип повідомлення, приймає тип String, Message, byte[] 
	 * @param connectionFactory
	 * @param queue
	 * @param text
	 * @param timeoutInMillis
	 * @throws JMSException
	 * @return T				- 	повідомлення, String, Message, byte[]
	 */
	
	@SuppressWarnings("unchecked")
	public <T> T receiveMessageJMS20(ConnectionFactory connectionFactory, Queue queue, int timeoutInMillis, Class type)
			throws JMSRuntimeException {
		T body = null;
		try (JMSContext context = connectionFactory.createContext()) {
			JMSConsumer consumer = context.createConsumer(queue);
			body = (T) consumer.receiveBody(type, timeoutInMillis);
		}
		return (T)body;
	}
	
	public Message receiveMessageJMS20(ConnectionFactory connectionFactory, Queue queue, int timeoutInMillis)
			throws JMSRuntimeException {
		Message body = null;
		try (JMSContext context = connectionFactory.createContext()) {
			JMSConsumer consumer = context.createConsumer(queue);
			body = consumer.receiveBody(Message.class, timeoutInMillis);
		}
		return body;
	}
	
	public String receiveTextMessageJMS20(ConnectionFactory connectionFactory, Queue queue, int timeoutInMillis)
			throws JMSRuntimeException {
		String body = null;
		try (JMSContext context = connectionFactory.createContext()) {
			JMSConsumer consumer = context.createConsumer(queue);
			body = consumer.receiveBody(String.class, timeoutInMillis);
		}
		return body;
	}
	
	public byte[] receiveByteMessageJMS20(ConnectionFactory connectionFactory, Queue queue, int timeoutInMillis)
			throws JMSRuntimeException {
		byte[] body = null;
		try (JMSContext context = connectionFactory.createContext()) {
			JMSConsumer consumer = context.createConsumer(queue);
			body = consumer.receiveBody(byte[].class, timeoutInMillis);
		}
		return body;
	}
	
	/**
	 * Отримання всіх (однотипних) повідомлення з використанням JMS 2.0
	 * 
	 * @param T 				- тип повідомлення, приймає тип String, Message, byte[]
	 * @param connectionFactory
	 * @param queue
	 * @param text
	 * @param timeoutInMillis
	 * @throws JMSException
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> receiveAllMessageJMS20(ConnectionFactory connectionFactory, Queue queue, int timeoutInMillis, Class type)
			throws JMSException {
		List<T> list = new LinkedList<T>();
		try (JMSContext context = connectionFactory.createContext()) {
			QueueBrowser browser = context.createBrowser(queue);
			while (browser.getEnumeration().hasMoreElements()) {
				Object receiveBody = context.createConsumer(queue).receiveBody(type,timeoutInMillis);
				list.add((T)receiveBody);
			}
		}
		return list;
	}
	
	public List<Message> receiveAllMessageJMS20(ConnectionFactory connectionFactory, Queue queue, int timeoutInMillis)
			throws JMSException {
		List<Message> list = new LinkedList<>();
		try (JMSContext context = connectionFactory.createContext()) {
			QueueBrowser browser = context.createBrowser(queue);
			while (browser.getEnumeration().hasMoreElements()) {
				Message receiveBody = context.createConsumer(queue).receiveBody(Message.class,timeoutInMillis);
				list.add(receiveBody);
			}
		}
		return list;
	}
	
	public List<String> receiveAllTextMessageJMS20(ConnectionFactory connectionFactory, Queue queue, int timeoutInMillis)
			throws JMSException {
		List<String> list = new LinkedList<>();
		try (JMSContext context = connectionFactory.createContext()) {
			QueueBrowser browser = context.createBrowser(queue);
			while (browser.getEnumeration().hasMoreElements()) {
				String receiveBody = context.createConsumer(queue).receiveBody(String.class,timeoutInMillis);
				list.add(receiveBody);
			}
		}
		return list;
	}
	
	public List<byte[]> receiveAllByteMessageJMS20(ConnectionFactory connectionFactory, Queue queue, int timeoutInMillis)
			throws JMSException {
		List<byte[]> list = new LinkedList<>();
		try (JMSContext context = connectionFactory.createContext()) {
			QueueBrowser browser = context.createBrowser(queue);
			while (browser.getEnumeration().hasMoreElements()) {
				byte[] receiveBody = context.createConsumer(queue).receiveBody(byte[].class,timeoutInMillis);
				list.add(receiveBody);
			}
		}
		return list;
	}
}
