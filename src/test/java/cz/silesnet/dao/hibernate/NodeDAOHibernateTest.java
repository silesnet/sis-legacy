package cz.silesnet.dao.hibernate;

import cz.silesnet.dao.NodeDAO;
import cz.silesnet.dao.NodeDAOTest;
import org.hibernate.SessionFactory;
import org.unitils.orm.hibernate.annotation.HibernateSessionFactory;
import org.unitils.spring.annotation.SpringApplicationContext;

@SpringApplicationContext({"context/sis-properties.xml", "context/sis-db.xml", "context/sis-hibernate.xml"})
public class NodeDAOHibernateTest extends NodeDAOTest {

  @HibernateSessionFactory
  private SessionFactory sessionFactory;

  @Override
  protected NodeDAO configureDao() {
    NodeDAOHibernate hibernateDao = new NodeDAOHibernate();
    hibernateDao.setSessionFactory(sessionFactory);
    hibernateDao.afterPropertiesSet();
    return hibernateDao;
  }
}