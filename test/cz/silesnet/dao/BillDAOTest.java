package cz.silesnet.dao;

import org.springframework.orm.ObjectRetrievalFailureException;

import cz.silesnet.model.Bill;
import cz.silesnet.model.Customer;
import cz.silesnet.model.PrepareMixture;
import cz.silesnet.service.CustomerManager;

public class BillDAOTest extends BaseDAOTestCase {

    public void testCRUD() {
        BillDAO dao = (BillDAO) ctx.getBean("billDAO");
        CustomerManager cmgr = (CustomerManager) ctx.getBean("customerManager");
        assertNotNull(dao);
        assertNotNull(cmgr);

        Customer customer = PrepareMixture.getCustomer();
        Customer c = customer;
        // String customerName = c.getName();
        cmgr.insert(c);

        Bill b = PrepareMixture.getBillSimple();
        b.setInvoicedCustomer(c);
        String billHash = b.getHashCode();

        log.debug("Persist bill.");
        dao.save(b);
        assertNotNull(b.getId());
        Long billId = b.getId();

        // update
        b.getItems().get(0).setText("Modified First Line");
        b.setIsConfirmed(true);
        String secondLine = b.getItems().get(1).getText();

        // persist changes
        log.debug("Update bill.");
        dao.save(b);

        // try to retrieve it
        b = null;
        c = null;
        b = dao.get(billId);
        assertNotNull(b);
        assertEquals("Modified First Line", b.getItems().get(0).getText());
        assertEquals(secondLine, b.getItems().get(1).getText());

        c = b.getInvoicedCustomer();
        assertNull(c);
        // assertTrue(customerName.equals(c.getName()));

        log.debug("Get by hashCode.");
        b = null;
        try {
            b = dao.get("hashxxWRONG");
            fail("Retrieved bill by non existing hashcode");
        } catch (ObjectRetrievalFailureException e) {
            log.debug("Got expected exception " + e);
        }
        assertNull(b);

        b = dao.get(billHash);
        assertNotNull(b);
        assertTrue(b.getId().equals(billId));

        log.debug("Tidy up.");
        // tidy up
        dao.remove(b);
        cmgr.delete(customer);

        b = null;
        try {
            b = dao.get(billId);
            fail("Retrieved deleted bill!");
        } catch (ObjectRetrievalFailureException e) {
            log.debug("Got expected exception " + e);
        }
        assertNull(b);

    }
}
