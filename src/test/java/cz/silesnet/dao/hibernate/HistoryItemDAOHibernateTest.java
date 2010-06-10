package cz.silesnet.dao.hibernate;

import cz.silesnet.dao.HistoryItemDAO;
import cz.silesnet.dao.HistoryItemDAOTest;
import org.hibernate.SessionFactory;
import org.unitils.orm.hibernate.annotation.HibernateSessionFactory;
import org.unitils.spring.annotation.SpringApplicationContext;

@SpringApplicationContext({"context/sis-properties.xml", "context/sis-db.xml", "context/sis-hibernate.xml"})
public class HistoryItemDAOHibernateTest extends HistoryItemDAOTest {

    @HibernateSessionFactory
    private SessionFactory sessionFactory;

    @Override
    protected HistoryItemDAO configureDao() {
        HistoryItemDAOHibernate hibernateDao = new HistoryItemDAOHibernate();
        hibernateDao.setSessionFactory(sessionFactory);
        hibernateDao.afterPropertiesSet();
        return hibernateDao;
    }
}
