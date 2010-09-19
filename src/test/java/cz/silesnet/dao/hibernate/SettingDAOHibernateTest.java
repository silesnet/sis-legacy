package cz.silesnet.dao.hibernate;

import cz.silesnet.dao.SettingDAO;
import cz.silesnet.dao.SettingDAOTest;
import org.hibernate.SessionFactory;
import org.unitils.orm.hibernate.annotation.HibernateSessionFactory;
import org.unitils.spring.annotation.SpringApplicationContext;

@SpringApplicationContext({"context/sis-properties.xml", "context/sis-db.xml", "context/sis-hibernate.xml"})
public class SettingDAOHibernateTest extends SettingDAOTest {

  @HibernateSessionFactory
  private SessionFactory sessionFactory;

  @Override
  protected SettingDAO configureDao() {
    SettingDAOHibernate hibernateDao = new SettingDAOHibernate();
    hibernateDao.setSessionFactory(sessionFactory);
    hibernateDao.afterPropertiesSet();
    return hibernateDao;
  }
}