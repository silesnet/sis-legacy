package cz.silesnet.command;

import cz.silesnet.event.Payload;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 15.5.12
 * Time: 21:25
 * To change this template use File | Settings | File Templates.
 */
public interface CommandHandler {
    Set<CommandName> handles();

    CommandResult execute(Command command);
}
