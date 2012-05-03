package cz.silesnet.event.consumer;

import cz.silesnet.event.EventConsumer;
import cz.silesnet.event.Event;
import cz.silesnet.service.CustomerManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

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
        final ArrayList<Integer> ids = new ArrayList<Integer>();
        final Object id = event.value("id", Object.class);
        if (id instanceof Iterable) {
            for (Integer serviceId : (Iterable<Integer>) id)
                ids.add(serviceId);
        } else {
            ids.add(Integer.valueOf(id.toString()));
        }
        for (Integer serviceId : ids) {
            log.info("service:\n" + customerManager.getService(serviceId.longValue()));
        }
    }

    public void setCustomerManager(final CustomerManager customerManager) {
        this.customerManager = customerManager;
    }
}
