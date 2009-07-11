package cz.silesnet.model;

import java.util.Date;

import junit.framework.TestCase;

public class CustomerTest extends TestCase {

	public void testIsSpsSynchronizedTrue() {
		Customer customer = new Customer();
		long now = System.currentTimeMillis();
		customer.setUpdated(new Date(now));
		customer.setSynchronized(new Date(now + 1));
		assertTrue(customer.isSpsSynchronized());
	}

	public void testIsSpsSynchronizedGreater() {
		Customer customer = new Customer();
		long now = System.currentTimeMillis();
		customer.setUpdated(new Date(now));
		customer.setSynchronized(new Date(now - 1));
		assertFalse(customer.isSpsSynchronized());
	}

	public void testIsSpsSynchronizedEqual() {
		Customer customer = new Customer();
		Date now = new Date();
		customer.setUpdated(now);
		customer.setSynchronized(now);
		assertFalse(customer.isSpsSynchronized());
	}

	public void testIsSpsSynchronizedNull() {
		Customer customer = new Customer();
		assertFalse(customer.isSpsSynchronized());
	}

}
