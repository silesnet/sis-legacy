package cz.silesnet.dao.hibernate;

import cz.silesnet.dao.CustomerDAO;
import cz.silesnet.dao.CustomerDAOTest;
import cz.silesnet.dao.ServiceDAO;
import cz.silesnet.dao.ServiceDAOTest;
import org.hibernate.SessionFactory;
import org.unitils.orm.hibernate.annotation.HibernateSessionFactory;
import org.unitils.spring.annotation.SpringApplicationContext;

/**
 * User: der3k
 * Date: May 19, 2010
 * Time: 9:55:39 PM
 */
@SpringApplicationContext({"context/sis-properties.xml", "context/sis-db.xml", "context/sis-hibernate.xml"})
public class CustomerDAOHibernateTest extends CustomerDAOTest {
    @HibernateSessionFactory
    private SessionFactory sessionFactory;

    @Override
    protected CustomerDAO configureDao() {
        CustomerDAOHibernate hibernateDao = new CustomerDAOHibernate();
        hibernateDao.setSessionFactory(sessionFactory);
        hibernateDao.afterPropertiesSet();
        return hibernateDao;
    }

}