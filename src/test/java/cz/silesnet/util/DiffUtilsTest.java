package cz.silesnet.util;

import cz.silesnet.model.Customer;
import cz.silesnet.model.PrepareMixture;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.*;

public class DiffUtilsTest {

  // ~ Instance fields
  // --------------------------------------------------------

  protected final Log log = LogFactory.getLog(getClass());

  // ~ Methods
  // ----------------------------------------------------------------

  @Test
  public void testColectionDiffs() {

    Customer c = PrepareMixture.getCustomer();

    log.debug("Persisting Customer: " + c);

    Map<String, String[]> diffMap = DiffUtils.getDiffMap(null, c, c
        .getDiffExcludeFields());
    assertTrue(diffMap.size() > 0);
    assertTrue("".equals(diffMap.get("Customer.fInfo")[0]));
    assertTrue(c.getInfo().equals(diffMap.get("Customer.fInfo")[1]));

    // is off koz Services are temporaily excluded from diff fields
    Customer c2 = PrepareMixture.getCustomer();
    c2.getServices().remove(1);

    diffMap = DiffUtils.getDiffMap(c, c2, c.getDiffExcludeFields());
    log.debug(diffMap.size());
    assertTrue(diffMap.size() == 2);

  }
}