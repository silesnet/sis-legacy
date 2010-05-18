package cz.silesnet.dao.hibernate;

import cz.silesnet.dao.LabelDAO;
import cz.silesnet.dao.LabelDAOTest;
import org.hibernate.SessionFactory;
import org.unitils.orm.hibernate.annotation.HibernateSessionFactory;
import org.unitils.spring.annotation.SpringApplicationContext;

/**
 * Created by IntelliJ IDEA.
 * User: sikorric
 * Date: May 18, 2010
 * Time: 3:09:42 PM
 */
@SpringApplicationContext({"context/sis-properties.xml", "context/sis-db.xml", "context/sis-hibernate.xml"})
public class LabelDAOHibernateTest extends LabelDAOTest {

    @HibernateSessionFactory
    private SessionFactory sessionFactory;

    @Override
    protected LabelDAO configureDao() {
        LabelDAOHibernate hibernateDao = new LabelDAOHibernate();
        hibernateDao.setSessionFactory(sessionFactory);
        hibernateDao.afterPropertiesSet();
        return hibernateDao;
    }
}
