package cz.silesnet.event.consumer;

import cz.silesnet.event.EventConsumer;
import cz.silesnet.event.PublishedEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * User: der3k
 * Date: 5.2.12
 * Time: 18:59
 */
public class ServiceEventConsumer implements EventConsumer {

    protected final Log log = LogFactory.getLog(getClass());

    public void consume(final PublishedEvent event) {
        log.debug("consumed '" + event.key() + "' " + event.toString());
    }
}
