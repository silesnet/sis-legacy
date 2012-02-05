package cz.silesnet.event;

/**
 * User: der3k
 * Date: 5.2.12
 * Time: 15:41
 */
public interface EventBus {
    void publish(EventKey key, Event event);

    void subscribe(EventConsumer consumer, EventKeyPattern pattern);

    void unsubscribe(EventConsumer consumer);
}
