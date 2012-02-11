package cz.silesnet.event.consumer;

import cz.silesnet.event.EventConsumer;
import cz.silesnet.event.Event;
import cz.silesnet.service.CustomerManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * User: der3k
 * Date: 5.2.12
 * Time: 18:59
 */
public class ServiceEventConsumer implements EventConsumer {

    private final Log log = LogFactory.getLog(getClass());

    private CustomerManager customerManager;

    public void consume(final Event event) {
        log.info("consumed " + event.toString());
        log.info("service:\n" + customerManager.getService(event.value("id", Long.class)));
    }

    public void setCustomerManager(final CustomerManager customerManager) {
        this.customerManager = customerManager;
    }
}
