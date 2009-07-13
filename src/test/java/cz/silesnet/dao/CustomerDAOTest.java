package cz.silesnet.dao;

import cz.silesnet.model.Customer;
import cz.silesnet.model.PrepareMixture;
import cz.silesnet.model.enums.Frequency;

import org.springframework.orm.ObjectRetrievalFailureException;

import java.util.List;

public class CustomerDAOTest extends BaseDAOTestCase {

	// ~ Methods
	// ----------------------------------------------------------------

	public void testCRUD() {
		CustomerDAO dao = (CustomerDAO) ctx.getBean("customerDAO");

		Customer c = PrepareMixture.getCustomer();
		int servicesSize = c.getServices().size();
		String customerName = c.getName();
		Frequency billingFrequency = c.getBilling().getFrequency();

		log.debug("Persisting Customer: " + c);
		dao.save(c);
		assertNotNull(c.getId());

		// get persisted object id and drop it
		Long cId = c.getId();
		c = null;

		// retrieve persisted customer
		log.debug("Retrieving customer with id: " + cId);
		c = dao.get(cId);
		assertNotNull(c.getId());
		assertNotNull(c.getContact());
		assertTrue(c.getServices().size() == servicesSize);
		assertNotNull(c.getServices().get(0).getName());
		assertNotNull(c.getServices().get(1).getName());
		assertNotNull(c.getBilling());
		assertTrue(customerName.equals(c.getName()));
		assertTrue(c.getBilling().getFrequency().equals(billingFrequency));

		log.debug("Retrieved customer: " + c);

		// update
		c.setName("Modified Customer Name");
		c.getContact().getAddress().setStreet("Modified Street");
		c.getContact().setEmail("admin@gymopol.cz");
		c.getServices().remove(0);
		c.getServices().get(0).setInfo("Modified Service Info String");
		c.getServices().get(0).setFrequency(Frequency.WEEKLY);
		c.getBilling().setFrequency(Frequency.DAILY);

		// persist
		log.debug("Persisting updated customer: " + c);
		dao.save(c);

		// reget
		c = null;

		log.debug("Retrieving updated customer with id: " + cId);
		c = dao.get(cId);
		assertNotNull(c.getId());
		assertNotNull(c.getContact());
		assertTrue(c.getServices().size() == servicesSize - 1);
		assertNotNull(c.getServices().get(0).getName());
		assertNotNull(c.getBilling());
		assertTrue("Modified Customer Name".equals(c.getName()));
		assertTrue("Modified Street".equals(c.getContact().getAddress()
				.getStreet()));
		assertTrue("admin@gymopol.cz".equals(c.getContact().getEmail()));
		assertTrue("Modified Service Info String".equals(c.getServices().get(0)
				.getInfo()));
		assertTrue(c.getBilling().getFrequency().equals(Frequency.DAILY));

		log.debug("Retrieved updated customer: " + c);

		// does not work with non transactional session, see
		// HibernateTemplate.enableFilter()
		// List<Customer> customers = dao.getAll();
		List<Customer> customers = null;

		// empty filter should return all
		log.debug("Getting customers by empty example.");
		Customer filterCustomer = new Customer();
		// FIXME should work with constructor pre filled enum fields
		filterCustomer.setContact(null);
		filterCustomer.setBilling(null);
		customers = dao.getByExample(filterCustomer);
		assertTrue(customers.size() >= 1);

		// with existing name should return it
		log.debug("Getting customers by existing example.");
		filterCustomer.setName("Modified Customer Name");
		customers = dao.getByExample(filterCustomer);
		assertTrue(customers.size() >= 1);

		// with non existing name should return empty list
		log.debug("Getting customers by non existing example.");
		filterCustomer.setName("xxx");
		customers = dao.getByExample(filterCustomer);
		assertTrue(customers.size() == 0);

		// remove
		log.debug("Removing customer: " + c);
		dao.remove(c);

		// try to reget
		log.debug("Trying to reget deleted customer.");
		try {
			c = dao.get(cId);
			fail();
		}
		catch (ObjectRetrievalFailureException e) {
			log.debug("Expected exception: " + e);
		}
	}
}