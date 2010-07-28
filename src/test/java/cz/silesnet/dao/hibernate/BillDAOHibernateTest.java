package cz.silesnet.dao.hibernate;

import cz.silesnet.dao.BillDAO;
import cz.silesnet.dao.BillDAOTest;
import org.hibernate.SessionFactory;
import org.unitils.orm.hibernate.annotation.HibernateSessionFactory;
import org.unitils.spring.annotation.SpringApplicationContext;

/**
 * User: der3k
 * Date: May 19, 2010
 * Time: 9:55:39 PM
 */
@SpringApplicationContext({"context/sis-properties.xml", "context/sis-db.xml", "context/sis-hibernate.xml"})
public class BillDAOHibernateTest extends BillDAOTest {

  @HibernateSessionFactory
  private SessionFactory sessionFactory;

  @Override
  protected BillDAO configureDao() {
    BillDAOHibernate hibernateDao = new BillDAOHibernate();
    hibernateDao.setSessionFactory(sessionFactory);
    hibernateDao.afterPropertiesSet();
    return hibernateDao;
  }

}