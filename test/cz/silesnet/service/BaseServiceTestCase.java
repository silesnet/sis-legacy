package cz.silesnet.service;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Base test case for services testing. Instantiates log and context.
 * 
 * @author Richard Sikora
 * 
 */
public abstract class BaseServiceTestCase extends TestCase {

    // ~ Instance fields
    // --------------------------------------------------------

    protected final Log log = LogFactory.getLog(getClass());
    protected ApplicationContext ctx = null;

    // ~ Constructors
    // -----------------------------------------------------------

    public BaseServiceTestCase() {
        String[] paths = { "/WEB-INF/applicationContext-hibernate.xml", "/WEB-INF/applicationContext.xml" };
        ctx = new ClassPathXmlApplicationContext(paths);
    }
}