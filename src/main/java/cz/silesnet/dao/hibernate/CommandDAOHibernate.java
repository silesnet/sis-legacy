package cz.silesnet.dao.hibernate;

import cz.silesnet.dao.CommandDAO;
import cz.silesnet.model.Command;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class CommandDAOHibernate extends HibernateDaoSupport implements CommandDAO {
  @Override
  public Command get(long commandId) {
    return getHibernateTemplate().get(Command.class, commandId);
  }

  @Override
  public void save(Command command) {
    getHibernateTemplate().saveOrUpdate(command);
  }
}
