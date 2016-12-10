package cz.silesnet.dao;

import cz.silesnet.model.Event;

import java.util.List;

public interface EventDAO extends DAO {
  List<Event> events(String entity, long entityId);
}
