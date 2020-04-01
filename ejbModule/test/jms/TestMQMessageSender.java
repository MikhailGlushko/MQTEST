package test.jms;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.annotation.Resource.AuthenticationType;
import javax.ejb.Asynchronous;
import javax.ejb.EJBContext;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSRuntimeException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.naming.NamingException;

import org.nbu.sz1.jms.mq.MessageService;
import org.nbu.sz1.jms.mq.Resources;

/**
 * Session Bean implementation class TestMQMessageSender
 */
@Singleton
@Startup
public class TestMQMessageSender {

	@Resource(lookup="S0HPSEP100",authenticationType=AuthenticationType.CONTAINER, shareable=true)
	private ConnectionFactory connectionFactory;
	
	@Inject @JMSConnectionFactory("S0HPSEP100") 
	private JMSContext context;
	
	@Resource(lookup = "SSEP")
    private Queue queue;
    @Resource(lookup = "SSEP_IN")
    private Queue queueIn;
    @Resource(lookup = "SSEP_OUT")
    private Queue queueOut;
	@Inject MessageService messageService;
	
	@Resource private EJBContext ejbContext;
	
    public TestMQMessageSender() {
        // TODO Auto-generated constructor stub
    }

    @PostConstruct
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Asynchronous
    public void test() {
    	try {
    		// Відправка повідомлення через messageService
    		messageService.sendMessageJMS20(connectionFactory, queue, "Test1");
    		System.out.println("Send message (messageService): Test1");   	
    		
    		// Відправка повідомлення напряму через context
    		context.createProducer().send(queueIn,"Test2");
    		System.out.println("Send message(context): Test2");
    		
    		// Отримання повідомлень через context
    		String receiveBody = context.createConsumer(queue).receiveBody(String.class,5000);
    		System.out.println("Get message(context): "+receiveBody);
    		
    		// Отримання повідомлень через messageService
    		List<String> receiveAll = messageService.receiveAllMessageJMS20(connectionFactory, queueOut,5000, String.class);
    		receiveAll.stream().map(result -> result + "; ").forEach(System.out::print);
    		
		} catch (JMSRuntimeException | JMSException  e) {
    		//e.printStackTrace();
    		ejbContext.setRollbackOnly();
		}
    }    
}
