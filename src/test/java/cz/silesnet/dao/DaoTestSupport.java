package cz.silesnet.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.unitils.UnitilsTestNG;

/**
 * Created by IntelliJ IDEA.
 * User: sikorric
 * Date: May 18, 2010
 * Time: 4:58:46 PM
 */
@Test(groups = "integration")
public abstract class DaoTestSupport<D> extends UnitilsTestNG {

  protected D dao;
  protected final Log log = LogFactory.getLog(getClass());

  protected abstract D configureDao();

  @BeforeMethod
  public void setUp() {
    if (dao == null)
      dao = configureDao();
  }

}
