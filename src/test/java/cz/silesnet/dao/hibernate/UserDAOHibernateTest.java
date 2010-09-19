package cz.silesnet.dao.hibernate;

import cz.silesnet.dao.UserDAO;
import cz.silesnet.dao.UserDAOTest;
import org.hibernate.SessionFactory;
import org.unitils.orm.hibernate.annotation.HibernateSessionFactory;
import org.unitils.spring.annotation.SpringApplicationContext;

@SpringApplicationContext({"context/sis-properties.xml", "context/sis-db.xml", "context/sis-hibernate.xml"})
public class UserDAOHibernateTest extends UserDAOTest {

  @HibernateSessionFactory
  private SessionFactory sessionFactory;

  @Override
  protected UserDAO configureDao() {
    UserDAOHibernate hibernateDao = new UserDAOHibernate();
    hibernateDao.setSessionFactory(sessionFactory);
    hibernateDao.afterPropertiesSet();
    return hibernateDao;
  }
}