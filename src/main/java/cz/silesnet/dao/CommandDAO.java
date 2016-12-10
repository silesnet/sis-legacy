package cz.silesnet.dao;

import cz.silesnet.model.Command;

public interface CommandDAO extends DAO {
  Command get(long commandId);
  void save(Command command);
}
