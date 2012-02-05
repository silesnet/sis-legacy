package cz.silesnet.event;

/**
 * User: der3k
 * Date: 5.2.12
 * Time: 15:44
 */
public interface EventConsumer {
    void consume(PublishedEvent event);
}
