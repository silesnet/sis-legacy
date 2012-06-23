package cz.silesnet.event.consumer;

import cz.silesnet.event.Event;
import cz.silesnet.event.EventConsumer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 17.6.12
 * Time: 23:01
 * To change this template use File | Settings | File Templates.
 */
public class InvoiceEventConsumer implements EventConsumer {
    private final Log log = LogFactory.getLog(getClass());

    public void consume(Event event) {
        log.info("consumed: " + event.name());
        final Iterable payments = event.value("clearances", Iterable.class);
        for (Object payment : payments) {
            log.info(payment);
        }
    }
}
