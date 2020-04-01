/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.nbu.sz1.jms.mq.simple;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeoutException;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSRuntimeException;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.nbu.sz1.jms.MessageReceiver;
import org.nbu.sz1.jms.mq.Resources;

import com.ibm.mq.jms.JMSNotSupportedException;
import com.ibm.mq.jms.JMSParameterIsNullException;
import com.ibm.rmi.util.list.LinkedList;

/*
 *	SimpleMessageReceiver -  імплементація для MQ для JMS 2.0
 *	для роботи потрібні такі ресурси:
 *		- JMSContext/ConnectionFactory connectionFactory // S0HPSEP100
 *		- Queue outboundQueue; 		  // SSEP
 */
//@Stateless
public class SimpleMessageReceiver implements MessageReceiver{

    @Inject
    @JMSConnectionFactory("S0HPSEP100")
    private JMSContext context;

    @Resource(lookup = Resources.SYNC_CONTAINER_MANAGED_QUEUE)
    Queue outboundQueue;

    /**
     * отримуємо повідомлення з черги, якщо повідомлення немає в черзі, чекаємо зазначений час. 
     * Якщо задати час очікуваняя 0 - чекаємо поки не зявиться повідомлення
     * Даний метод використовується, коли всі необхідні дані налаштовані у контейнері: JMSContext/ConnectionFactory, Queue
     * Черга, по-замовчуванню, задана параметром Resources.SYNC_CONTAINER_MANAGED_QUEUE
     *
     * @param timeoutInMillis  - час очікування повідомлення, до отримання exception.
     * @return - текст повідомлення.
     * @throws JMSRuntimeException - помолка доступу до черги.
     * @throws TimeoutException - час очікування вичерпано.
     */
    @Deprecated
    public String receiveMessage(int timeoutInMillis) 
    		throws JMSRuntimeException, TimeoutException {
        
    	String message = context.createConsumer(outboundQueue).receiveBody(String.class, timeoutInMillis);
        if (message == null) {
            throw new TimeoutException("No message received after " + timeoutInMillis + "ms");
        }
        return message;
    }

    /**
     * отримуємо повідомлення з черги, якщо повідомлення немає в черзі, чекаємо зазначений час. 
     * Якщо задати час очікуваняя 0 - чекаємо поки не зявиться повідомлення
     * Даний метод використовується, коли всі необхідні дані налаштовані у контейнері: JMSContext/ConnectionFactory
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
    public String receiveMessage(String queueName,int timeoutInMillis) 
    		throws JMSRuntimeException, TimeoutException, NamingException, JMSParameterIsNullException {
    	
    	if(Objects.isNull(queueName))
    		throw new JMSParameterIsNullException("Parameter queueName is NULL");
    	
    	Context ctx_q = new InitialContext();
    	Queue inboundQueue = (Queue) ctx_q.lookup(queueName);
        String message = context.createConsumer(inboundQueue).receiveBody(String.class, timeoutInMillis);
        if (message == null) {
            throw new TimeoutException("No message received after " + timeoutInMillis + "ms");
        }
        return message;
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
    	
		String message = null;
		Context ctx_q = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) ctx_q.lookup(queueMamagerName);
		Queue inboundQueue = (Queue) ctx_q.lookup(queueName);
        try (JMSContext context = factory.createContext()) {
        	message = context.createConsumer(inboundQueue).receiveBody(String.class, timeoutInMillis);
        	if (message == null) {
                throw new TimeoutException("No message received after " + timeoutInMillis + "ms");
            }	
        }
        return message;
	}
	
	/**
	 * Прочитати всі повідомлення з черги
	 * Даний метод використовується, коли всі необхідні дані налаштовані у контейнері: JMSContext/ConnectionFactory, Queue
	 */
    @Deprecated
    public List<String> receiveAll(int timeoutInMillis) 
    		throws JMSException {
    	
    	List<String> list = new java.util.LinkedList<String>();
        QueueBrowser browser = context.createBrowser(outboundQueue);
        while (browser.getEnumeration().hasMoreElements()) {
            String receiveBody = context.createConsumer(outboundQueue).receiveBody(String.class, timeoutInMillis);
            list.add(receiveBody);
        }
        return list;
    }
    
	/**
	 * Прочитати всі повідомлення з черги
	 * Даний метод використовується, коли всі необхідні дані налаштовані у контейнері: JMSContext/ConnectionFactory
	 */
    @SuppressWarnings("deprecation")
    public List<String> receiveAll(String queueName, int timeoutInMillis) 
    		throws JMSException, NamingException, JMSParameterIsNullException {
    	
    	if(Objects.isNull(queueName))
    		throw new JMSParameterIsNullException("Parameter queueName is NULL");
    	
    	List<String> list = new java.util.LinkedList<String>();
    	Context ctx_q = new InitialContext();
    	Queue inboundQueue = (Queue) ctx_q.lookup(queueName);
        QueueBrowser browser = context.createBrowser(inboundQueue);
        while (browser.getEnumeration().hasMoreElements()) {
            String receiveBody = context.createConsumer(inboundQueue).receiveBody(String.class, timeoutInMillis);
            list.add(receiveBody);
        }
        return list;
    }
    
	/**
	 * Прочитати всі повідомлення з черги
	 * Даний метод використовується, коли всі необхідні дані передаються в метод, і виконується програмне налаштування
	 */
    @SuppressWarnings("deprecation")
    public  List<String> receiveAll(String queueMamagerName, String queueName, int timeoutInMillis) 
    		throws JMSException, NamingException, JMSParameterIsNullException{
    	if(Objects.isNull(queueName))
    		throw new JMSParameterIsNullException("Parameter queueName is NULL");
    	if(Objects.isNull(queueMamagerName))
    		throw new JMSParameterIsNullException("Parameter queueMamagerName is NULL");
    	
    	List<String> list = new java.util.LinkedList<String>();
		Context ctx_q = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) ctx_q.lookup(queueMamagerName);
		Queue inboundQueue = (Queue) ctx_q.lookup(queueName);	
		try (JMSContext context = factory.createContext()) {
			QueueBrowser browser = context.createBrowser(inboundQueue);
	        while (browser.getEnumeration().hasMoreElements()) {
	            String receiveBody = context.createConsumer(inboundQueue).receiveBody(String.class, timeoutInMillis);
	            list.add(receiveBody);
	        }	
		}
		return list;
    }
}
