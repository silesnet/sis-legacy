package cz.silesnet.event;

import org.joda.time.DateTime;

/**
 * User: der3k
 * Date: 5.2.12
 * Time: 15:52
 */
public interface Event extends Payload {
    String name();

    String domain();

    DateTime timestamp();
}
