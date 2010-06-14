package cz.silesnet.service.impl;

import cz.silesnet.model.Customer;
import cz.silesnet.model.PrepareMixture;
import cz.silesnet.model.enums.Frequency;
import cz.silesnet.service.CustomerManager;
import cz.silesnet.service.HistoryManager;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

public class CustomerManagerTest extends BaseServiceTestCase {

    @Test
    public void testCRUD() {
        // get managers from context
        CustomerManager cmgr = (CustomerManager) ctx.getBean("customerManager");
        HistoryManager hmgr = (HistoryManager) ctx.getBean("historyManager");

        assertNotNull(cmgr);

        Customer c = PrepareMixture.getCustomer();

        log.debug("Persisting Customer: " + c);
        cmgr.insert(c);
        assertNotNull(c.getId());

        // store history size
        int historySize = hmgr.getHistory(c).size();
        assertTrue(historySize > 0);

        // get persisted object id and drop it
        Long cId = c.getId();
        c = null;

        // retrieve persisted customer
        log.debug("Retrieving customer with id: " + cId);
        c = cmgr.get(cId);

        assertNotNull(c.getId());
        assertNotNull(c.getContact());
        assertTrue(c.getServices().size() == 2);
        assertNotNull(c.getServices().get(0).getName());
        assertNotNull(c.getServices().get(1).getName());
        assertNotNull(c.getBilling());

        log.debug("Retrieved customer: " + c);

        // update
        c.setName("Modified Customer Name");
        c.getContact().getAddress().setStreet("Modified Street String");
        c.getContact().setEmail("admin@gymopol.cz");
        // FIXME wrong pairing of services if remove(0)
        c.getServices().remove(1);
        c.getServices().get(0).setInfo("Modified Info String");
        c.getServices().get(0).setFrequency(Frequency.WEEKLY);
        c.getBilling().setFrequency(Frequency.DAILY);

        // persist
        log.debug("Persisting updated customer: " + c);
        cmgr.update(c);

        // see if history is growing
        assertTrue(hmgr.getHistory(c).size() > historySize);

        // reget
        c = null;

        log.debug("Retrieving updated customer with id: " + cId);
        c = cmgr.get(cId);
        assertNotNull(c.getId());
        assertNotNull(c.getContact());
        assertTrue(c.getServices().size() == 1);
        assertNotNull(c.getServices().get(0).getName());
        assertNotNull(c.getBilling());

        assertTrue("Modified Customer Name".equals(c.getName()));
        assertTrue("Modified Street String".equals(c.getContact().getAddress()
                .getStreet()));
        assertTrue("admin@gymopol.cz".equals(c.getContact().getEmail()));
        assertTrue("Modified Info String".equals(c.getServices().get(0)
                .getInfo()));

        log.debug("Retrieved updated customer: " + c);

        // try getting lists
        log.debug("Getting all customers.");
        List<Customer> customers = cmgr.getAll();
        assertTrue(customers.size() >= 1);

        // empty filter should return all
        log.debug("Getting customers by empty example.");
        Customer filterCustomer = new Customer();
        // FIXME should work with constructor pre filled enum fields
        filterCustomer.setContact(null);
        filterCustomer.setBilling(null);
        customers = cmgr.getByExample(filterCustomer);
        assertTrue(customers.size() >= 1);

        // with existing name should return it
        log.debug("Getting customers by existing example.");
        filterCustomer.setName("Modified Customer Name");
        customers = cmgr.getByExample(filterCustomer);
        assertTrue(customers.size() >= 1);

        // with non existing name should return empty list
        log.debug("Getting customers by non existing example.");
        filterCustomer.setName("xxx");
        customers = cmgr.getByExample(filterCustomer);
        assertTrue(customers.size() == 0);

        // remove
        log.debug("Removing customer: " + c);
        cmgr.delete(c);

        // try to reget
        log.debug("Trying to reget deleted customer.");
        try {
            c = cmgr.get(cId);
            fail();
        }
        catch (ObjectRetrievalFailureException e) {
            log.debug("Expected exception: " + e);
        }
    }

    @Test
    public void TmptestDeactivateCandidates() {
        CustomerManager cmgr = (CustomerManager) ctx.getBean("customerManager");
        assertNotNull(cmgr);

        cmgr.deactivateCandidates();
    }

}