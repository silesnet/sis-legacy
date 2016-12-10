package cz.silesnet.service;

import cz.silesnet.model.Event;

import java.util.List;

public interface EventManager {
  List<Event> events(String entity, long entityId);
}
