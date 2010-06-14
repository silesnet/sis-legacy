package cz.silesnet.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.unitils.UnitilsTestNG;
import org.unitils.spring.annotation.SpringApplicationContext;

/**
 * Base test case for services testing. Instantiates log and context.
 *
 * @author Richard Sikora
 */
@SpringApplicationContext({"context/sis-properties.xml", "context/sis-db.xml", "context/sis-hibernate.xml", "context/sis-dao.xml", "context/sis-transaction.xml", "context/sis-service.xml", "context/sis-email.xml"})
public abstract class BaseServiceTestCase extends UnitilsTestNG {

    protected final Log log = LogFactory.getLog(getClass());

}