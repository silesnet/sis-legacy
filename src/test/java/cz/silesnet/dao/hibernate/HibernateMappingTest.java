package cz.silesnet.dao.hibernate;

import org.testng.annotations.Test;
import org.unitils.UnitilsTestNG;
import org.unitils.orm.hibernate.HibernateUnitils;
import org.unitils.spring.annotation.SpringApplicationContext;

/**
 * User: der3k
 * Date: May 15, 2010
 * Time: 11:31:20 AM
 */
@SpringApplicationContext({"context/sis-properties.xml", "context/sis-db.xml", "context/sis-hibernate.xml"})
@Test(groups = "integration")
public class HibernateMappingTest extends UnitilsTestNG {

  @Test
  public void hibernateMappings() throws Exception {
    HibernateUnitils.assertMappingWithDatabaseConsistent();
  }
}

