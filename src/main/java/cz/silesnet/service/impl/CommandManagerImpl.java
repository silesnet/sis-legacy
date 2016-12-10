package cz.silesnet.service.impl;

import cz.silesnet.dao.CommandDAO;
import cz.silesnet.model.Command;
import cz.silesnet.service.CommandManager;

public class CommandManagerImpl implements CommandManager {

  private CommandDAO commandDAO;

  public void setCommandDAO(CommandDAO commandDAO) {
    this.commandDAO = commandDAO;
  }

  @Override
  public long submit(Command command) {
    command.setId(null);
    commandDAO.save(command);
    return command.getId();
  }

  @Override
  public String status(long commandId) {
    final Command command = commandDAO.get(commandId);
    return command != null ? command.getStatus() : "" ;
  }
}
