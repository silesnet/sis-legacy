package cz.silesnet.dao.hibernate;

import cz.silesnet.dao.EventDAO;
import cz.silesnet.model.Event;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.List;

public class EventDAOHibernate extends HibernateDaoSupport implements EventDAO {

  @Override
  public List<Event> events(String entity, long entityId) {
    return getHibernateTemplate().find(
        "from Event as e where e.entity=? and e.entityId=? order by e.id",
        entity,
        entityId);
  }
}
