package cz.silesnet.web.rest;

import cz.silesnet.command.Command;
import cz.silesnet.command.CommandDispatcher;
import cz.silesnet.command.CommandName;
import cz.silesnet.command.CommandResult;
import cz.silesnet.event.Payload;
import cz.silesnet.event.support.JsonPayload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 15.5.12
 * Time: 22:27
 * To change this template use File | Settings | File Templates.
 */
@Path("/do")
public class CommandController {
    private final Log log = LogFactory.getLog(getClass());
    private CommandDispatcher commandDispatcher;

    public void setCommandDispatcher(CommandDispatcher commandDispatcher) {
        this.commandDispatcher = commandDispatcher;
    }

    @POST
    @Path("/{cmd}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Object execute(final Map data, @PathParam("cmd") final String cmd) {
        final JsonPayload payload = JsonPayload.of(data);
        final CommandName commandName = CommandName.of(cmd);
        final Command command = new Command() {
            public CommandName name() {
                return commandName;
            }

            public Payload payload() {
                return payload;
            }
        };
        log.debug("dispatching '" + cmd + "' command");
        final CommandResult result;
        try {
            result = commandDispatcher.dispatch(command);
        } catch (RuntimeException e) {
            throw new WebApplicationException(e, 500);
        }
        log.debug("'" + cmd + "' completed successfully");
        return result.data();
    }

}
