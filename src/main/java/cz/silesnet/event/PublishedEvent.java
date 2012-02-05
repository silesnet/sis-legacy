package cz.silesnet.event;

/**
 * User: der3k
 * Date: 5.2.12
 * Time: 15:52
 */
public interface PublishedEvent extends Event{
    EventId id();

    EventKey key();
}
