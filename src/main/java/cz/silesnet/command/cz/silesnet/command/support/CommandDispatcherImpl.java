package cz.silesnet.command.cz.silesnet.command.support;

import cz.silesnet.command.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 15.5.12
 * Time: 21:30
 * To change this template use File | Settings | File Templates.
 */
public class CommandDispatcherImpl implements CommandDispatcher, BeanPostProcessor {
    private final Map<CommandName, CommandHandler> handlers = new HashMap<CommandName, CommandHandler>();
    private final Log log = LogFactory.getLog(getClass());

    public CommandResult dispatch(Command command) {
        final CommandHandler handler = handlers.get(command.name());
        if (handler == null)
            throw new IllegalArgumentException("there is no handler registered for this command");
        return handler.execute(command);
    }

    public void addHandler(final CommandHandler handler) {
        for (CommandName commandName : handler.handles()) {
            this.handlers.put(commandName, handler);
        }
    }

    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        return o;
    }

    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        if (o instanceof CommandHandler) {
            final CommandHandler handler = (CommandHandler) o;
            addHandler(handler);
            log.debug("added command handler '" + handler + "' to command dispatcher");
        }
        return o;
    }
}
