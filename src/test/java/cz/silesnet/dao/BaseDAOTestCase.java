package cz.silesnet.dao;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Base test case for DAO testing. Instantiates log and context.
 *
 * @author Richard Sikora
 */
public abstract class BaseDAOTestCase extends TestCase {

    protected final Log log = LogFactory.getLog(getClass());
    protected ApplicationContext ctx = null;

    public BaseDAOTestCase() {
        String[] paths = {"context/sis-properties.xml", "context/sis-db.xml", "context/sis-hibernate.xml", "context/sis-dao.xml"};
        ctx = new ClassPathXmlApplicationContext(paths);
    }
}