package cz.silesnet.event.impl;

import cz.silesnet.event.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: der3k
 * Date: 5.2.12
 * Time: 17:55
 */
public class SimpleEventBus implements EventBus {
    private final Map<EventKeyPattern, List<EventConsumer>> consumers = new HashMap<EventKeyPattern, List<EventConsumer>>();

    public void publish(final EventKey key, final Event event) {
        for (EventKeyPattern pattern : consumers.keySet())
            if (pattern.matches(key))
                for (EventConsumer consumer : consumers.get(pattern))
                    consumer.consume(new PublishedEvent() {
                        public EventId id() {
                            return null;
                        }

                        public EventKey key() {
                            return key;
                        }

                        public <T> T value(final String key, final Class<T> type) {
                            return event.value(key, type);
                        }

                    });
    }

    public void subscribe(final EventConsumer consumer, final EventKeyPattern pattern) {
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

    public static class ConsumerWithPattern {
        private EventConsumer consumer;
        private EventKeyPattern pattern;

        public ConsumerWithPattern() { }

        public void setConsumer(final EventConsumer consumer) {
            this.consumer = consumer;
        }

        public void setPattern(final String pattern) {
            this.pattern = new EventKeyPattern(pattern);
        }

    }
}
