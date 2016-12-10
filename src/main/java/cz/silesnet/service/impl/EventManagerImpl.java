package cz.silesnet.service.impl;

import cz.silesnet.dao.EventDAO;
import cz.silesnet.model.Event;
import cz.silesnet.service.EventManager;

import java.util.ArrayList;
import java.util.List;

public class EventManagerImpl implements EventManager {

  private EventDAO eventDAO;

  public void setEventDAO(EventDAO eventDAO) {
    this.eventDAO = eventDAO;
  }

  @Override
  public List<Event> events(String entity, long entityId) {
    final List<Event> events = eventDAO.events(entity, entityId);
    return events != null ? events : new ArrayList<Event>(0);
  }
}
