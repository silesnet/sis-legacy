package cz.silesnet.command;

import cz.silesnet.event.Payload;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 15.5.12
 * Time: 21:24
 * To change this template use File | Settings | File Templates.
 */
public interface CommandDispatcher {
    CommandResult dispatch(Command command);
}
