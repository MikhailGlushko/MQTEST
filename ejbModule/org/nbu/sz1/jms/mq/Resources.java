package org.nbu.sz1.jms.mq;

import javax.jms.JMSDestinationDefinition;
import javax.jms.JMSDestinationDefinitions;

/**
 * Application scoped JMS resources for the samples.
 * @author Patrik Dudits
 */
@JMSDestinationDefinitions({
    @JMSDestinationDefinition(name = Resources.CLASSIC_QUEUE,
        resourceAdapter = "jmsra",
        interfaceName = "javax.jms.Queue",
        destinationName = "SSEP",
        description = "My Sync Queue"),
    @JMSDestinationDefinition(name = Resources.ASYNC_QUEUE,
        resourceAdapter = "jmsra",
        interfaceName = "javax.jms.Queue",
        destinationName = "SSEP",
        description = "My Async Queue"),
    @JMSDestinationDefinition(name = Resources.SYNC_APP_MANAGED_QUEUE,
        resourceAdapter = "jmsra",
        interfaceName = "javax.jms.Queue",
        destinationName = "SSEP",
        description = "My Sync Queue for App-managed JMSContext"),
    @JMSDestinationDefinition(name = Resources.SYNC_CONTAINER_MANAGED_QUEUE,
        resourceAdapter = "jmsra",
        interfaceName = "javax.jms.Queue",
        destinationName = "SSEP",
        description = "My Sync Queue for Container-managed JMSContext")
})
public class Resources {
    public static final String SYNC_APP_MANAGED_QUEUE = "SSEP";
    public static final String SYNC_CONTAINER_MANAGED_QUEUE = "SSEP";
    public static final String ASYNC_QUEUE = "SSEP";
    public static final String CLASSIC_QUEUE = "SSEP";
}
