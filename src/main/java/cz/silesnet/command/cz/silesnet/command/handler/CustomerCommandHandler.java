package cz.silesnet.command.cz.silesnet.command.handler;

import cz.silesnet.command.Command;
import cz.silesnet.command.CommandHandler;
import cz.silesnet.command.CommandName;
import cz.silesnet.command.CommandResult;
import cz.silesnet.service.CustomerManager;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 15.5.12
 * Time: 22:02
 * To change this template use File | Settings | File Templates.
 */
public class CustomerCommandHandler implements CommandHandler {
    private final static Set<CommandName> COMMANDS = new HashSet<CommandName>();

    static {
        COMMANDS.add(CommandName.of("updateCustomerName"));
        COMMANDS.add(CommandName.of("updateCustomerAddress"));
    }

    private CustomerManager customerManager;

    public void setCustomerManager(CustomerManager customerManager) {
        this.customerManager = customerManager;
    }

    public Set<CommandName> handles() {
        return Collections.unmodifiableSet(COMMANDS);
    }

    public CommandResult execute(final Command command) {
        return new CommandResult() {
            public Map data() {
                Map<String, String> data = new HashMap<String, String>();
                data.put("commandName", command.name().toString());
                data.put("payload", command.payload().toString());
                data.put("finished", "" + System.currentTimeMillis());
                return data;
            }
        };
    }
}
