package cz.silesnet.utils;

import cz.silesnet.model.Customer;
import cz.silesnet.model.Label;
import cz.silesnet.model.PrepareMixture;
import cz.silesnet.model.Wireless;
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
  public void testNodeDiff() {
    Wireless node1 = new Wireless();
    Wireless node2 = new Wireless();

    node1.setName("node-1");
    node2.setName("node-2");

    Label label1 = new Label();
    Label label2 = new Label();

    label1.setName("Label 1");
    label2.setName("Label 2");

    node1.setDomain(label1);
    node2.setDomain(label2);

    Map<String, String[]> diffMap = DiffUtils.getDiffMap(node1, node2,
        node1.getDiffExcludeFields());
    log.debug(diffMap);
    assertTrue(diffMap.size() == 2);
    assertTrue(diffMap.get("wireless.name")[0].equals("node-1"));
    assertTrue(diffMap.get("wireless.name")[1].equals("node-2"));
    assertTrue(diffMap.get("wireless.domain")[0].equals("Label 1"));
    assertTrue(diffMap.get("wireless.domain")[1].equals("Label 2"));

    // try it with some nulls
    diffMap = DiffUtils.getDiffMap(node1, node2, null);
    assertTrue(diffMap.size() == 2);
    assertTrue(diffMap.get("wireless.name")[0].equals("node-1"));
    assertTrue(diffMap.get("wireless.name")[1].equals("node-2"));
    assertTrue(diffMap.get("wireless.domain")[0].equals("Label 1"));
    assertTrue(diffMap.get("wireless.domain")[1].equals("Label 2"));
    log.debug(diffMap);

    diffMap = DiffUtils.getDiffMap(null, node1, node1
        .getDiffExcludeFields());
    assertTrue(diffMap.size() == 5);
    assertTrue(diffMap.get("wireless.name")[0].equals(""));
    assertTrue(diffMap.get("wireless.name")[1].equals("node-1"));
    assertTrue(diffMap.get("wireless.domain")[0].equals(""));
    assertTrue(diffMap.get("wireless.domain")[1].equals("Label 1"));
    log.debug(diffMap);

    diffMap = DiffUtils.getDiffMap(null, new Object(), null);
    log.debug(diffMap);
  }

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