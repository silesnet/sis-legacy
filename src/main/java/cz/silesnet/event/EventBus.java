package cz.silesnet.event;

/**
 * User: der3k
 * Date: 5.2.12
 * Time: 15:41
 */
public interface EventBus {
    void publish(Payload payload, Key key);

    void subscribe(EventConsumer consumer, KeyPattern pattern);

    void unsubscribe(EventConsumer consumer);
}
