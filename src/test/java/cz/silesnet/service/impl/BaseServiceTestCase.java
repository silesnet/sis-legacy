package cz.silesnet.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Base test case for services testing. Instantiates log and context.
 *
 * @author Richard Sikora
 */
public abstract class BaseServiceTestCase {

    // ~ Instance fields
    // --------------------------------------------------------

    protected final Log log = LogFactory.getLog(getClass());

    protected ApplicationContext ctx = null;

    // ~ Constructors
    // -----------------------------------------------------------

    public BaseServiceTestCase() {
        String[] paths = {"context/sis-properties.xml", "context/sis-db.xml", "context/sis-hibernate.xml", "context/sis-dao.xml", "context/sis-transaction.xml", "context/sis-service.xml", "context/sis-email.xml"};
        ctx = new ClassPathXmlApplicationContext(paths);
    }
}