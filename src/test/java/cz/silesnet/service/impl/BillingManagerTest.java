package cz.silesnet.service.impl;

import cz.silesnet.model.Bill;
import cz.silesnet.model.Customer;
import cz.silesnet.model.PrepareMixture;
import cz.silesnet.service.BillingManager;
import cz.silesnet.service.CustomerManager;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.testng.annotations.Test;
import org.unitils.spring.annotation.SpringBean;

import static org.testng.Assert.*;

public class BillingManagerTest extends BaseServiceTestCase {

  @SpringBean("billingManager")
  private BillingManager bmgr;

  @SpringBean("customerManager")
  private CustomerManager cmgr;

  @Test
  public void testCRUD() {
    Customer customer = PrepareMixture.getCustomer();
    Customer c = customer;
    // String customerName = c.getName();
    cmgr.insert(c);

    Bill b = PrepareMixture.getBillSimple();
    b.setInvoicedCustomer(c);

    log.debug("Persist bill.");
    bmgr.insert(b);
    assertNotNull(b.getId());
    Long billId = b.getId();

    // update
    b.getItems().get(0).setText("Modified First Line");
    b.setIsConfirmed(true);
    String secondLine = b.getItems().get(1).getText();

    // persist changes
    log.debug("Update bill.");
    bmgr.update(b);
    String customerName = c.getName();

    // try to retrieve it
    b = null;
    c = null;
    b = bmgr.get(billId);
    assertNotNull(b);
    assertTrue("Modified First Line".equals(b.getItems().get(0).getText()));
    assertTrue(secondLine.equals(b.getItems().get(1).getText()));

    c = b.getInvoicedCustomer();
    log.debug(c.getName());
    assertTrue(customerName.equals(c.getName()));

    log.debug("Tidy up.");
    // tidy up
    bmgr.delete(b);
    cmgr.delete(customer);

    b = null;
    try {
      b = bmgr.get(billId);
      fail("Retrieved deleted bill!");
    } catch (ObjectRetrievalFailureException e) {
      log.debug("Got expected exception " + e);
    }
    assertNull(b);

  }

}
