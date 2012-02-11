package cz.silesnet.event.impl;

import cz.silesnet.event.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: der3k
 * Date: 5.2.12
 * Time: 17:55
 */
public class SimpleEventBus implements EventBus, BeanPostProcessor {
    private final Map<KeyPattern, List<EventConsumer>> consumers = new HashMap<KeyPattern, List<EventConsumer>>();
    private final Log log = LogFactory.getLog(getClass());

    public void publish(final Payload payload, final Key key) {
        final DateTime now = new DateTime();
        for (KeyPattern pattern : consumers.keySet())
            if (pattern.matches(key))
                for (EventConsumer consumer : consumers.get(pattern))
                    consumer.consume(new Event() {
                        public String name() {
                            return key.name();
                        }

                        public String domain() {
                            return key.domain();
                        }

                        public DateTime timestamp() {
                            return now;
                        }

                        public <T> T value(final String key, final Class<T> type) {
                            return payload.value(key, type);
                        }

                        public String toString() {
                            final StringBuilder builder = new StringBuilder();
                            builder.append("[key='")
                                    .append(key.toString())
                                    .append("', timestamp=")
                                    .append(now)
                                    .append(", ")
                                    .append(payload.toString())
                                    .append("]");
                            return builder.toString();
                        }
                    });
    }

    public void subscribe(final EventConsumer consumer, final KeyPattern pattern) {
        if (!consumers.containsKey(pattern))
            consumers.put(pattern, new ArrayList<EventConsumer>());
        consumers.get(pattern).add(consumer);
    }

    public void setConsumers(final List<ConsumerWithPattern> consumersWithPattern) {
        for (ConsumerWithPattern consumerWithPattern : consumersWithPattern)
            subscribe(consumerWithPattern.consumer, consumerWithPattern.pattern);
    }

    public void unsubscribe(final EventConsumer consumer) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object postProcessBeforeInitialization(final Object o, final String s) throws BeansException {
        return o;
    }

    public Object postProcessAfterInitialization(final Object o, final String s) throws BeansException {
        if (o instanceof ConsumerWithPattern) {
            ConsumerWithPattern consumerWithPattern = (ConsumerWithPattern) o;
            subscribe(consumerWithPattern.consumer, consumerWithPattern.pattern);
            log.debug("subscribed '" + s + "' as event bus consumer");
        }
        return o;
    }

    public static class ConsumerWithPattern {
        private EventConsumer consumer;
        private KeyPattern pattern;

        public ConsumerWithPattern() { }

        public void setConsumer(final EventConsumer consumer) {
            this.consumer = consumer;
        }

        public void setPattern(final String regex) {
            this.pattern = KeyPattern.of(regex);
        }

    }
}
