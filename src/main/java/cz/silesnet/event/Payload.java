package cz.silesnet.event;

/**
 * User: der3k
 * Date: 5.2.12
 * Time: 15:41
 */
public interface Payload {
    <T> T value(String key, Class<T> type);
}
