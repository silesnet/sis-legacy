package cz.silesnet.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.Test;
import org.unitils.UnitilsTestNG;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.util.TransactionMode;
import org.unitils.spring.annotation.SpringApplicationContext;

/**
 * Base test case for services testing. Instantiates log and context.
 *
 * @author Richard Sikora
 */
@SpringApplicationContext({"context/sis-properties.xml", "context/sis-db.xml", "context/sis-hibernate.xml", "context/sis-dao.xml", "context/sis-bus.xml", "context/sis-transaction.xml", "context/sis-service.xml", "context/sis-email.xml", "context/sis-template.xml", "context/sis-billing.xml"})
@Transactional(TransactionMode.ROLLBACK)
@Test(groups = "integration")
public abstract class BaseServiceTestCase extends UnitilsTestNG {

  protected final Log log = LogFactory.getLog(getClass());

}