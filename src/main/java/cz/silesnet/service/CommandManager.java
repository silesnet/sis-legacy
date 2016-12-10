package cz.silesnet.service;

import cz.silesnet.model.Command;

public interface CommandManager {
  long submit(Command command);

  String status(long commandId);
}
