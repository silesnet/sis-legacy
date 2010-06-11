package cz.silesnet.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cz.silesnet.model.enums.BillingStatus;
import cz.silesnet.model.enums.Frequency;

public class CustomerDeactivateTest {

	protected final Log log = LogFactory.getLog(getClass());

    @Test
	public void testDeactivateCandidate() {
		Customer customer = new Customer();
		Billing billing = new Billing();
		customer.setBilling(billing);
		// if due is null throw NullPointerException
		try {
			customer.isDeactivateCandidate(null);
			fail();
		}
		catch (NullPointerException e) {
			assertNotNull(e);
		}
		Calendar calendar = new GregorianCalendar(2006, Calendar.JANUARY, 1);
		Date due = calendar.getTime();
		// skip suspended customers
		billing.setStatus(BillingStatus.CEASE);
		assertFalse(customer.isDeactivateCandidate(due));
		// skip customer without services
		billing.setStatus(BillingStatus.INVOICE);
		List<Service> services = new ArrayList<Service>();
		customer.setServices(services);
		assertFalse(customer.isDeactivateCandidate(due));
		// skip customers with one time service
		Service oneTime = new Service();
		oneTime.setFrequency(Frequency.ONE_TIME);
		Service activeService = new Service();
		activeService.setFrequency(Frequency.MONTHLY);
		calendar.set(2007, Calendar.JANUARY, 1);
		activeService.getPeriod().setFrom(calendar.getTime());
		services.clear();
		services.add(oneTime);
		services.add(activeService);
		assertFalse(customer.isDeactivateCandidate(due));
		// skip customers with open, active service
		services.clear();
		services.add(activeService);
		assertFalse(customer.isDeactivateCandidate(due));
		// check real services
		// skip one closed service, but still active
		calendar.set(2007, Calendar.MARCH, 12);
		due = calendar.getTime();
		Service closedService = new Service();
		closedService.setFrequency(Frequency.MONTHLY);
		calendar.set(2007, Calendar.FEBRUARY, 1);
		closedService.getPeriod().setFrom(calendar.getTime());
		calendar.set(2007, Calendar.MAY, 30);
		closedService.getPeriod().setTo(calendar.getTime());
		services.clear();
		services.add(closedService);
		assertFalse(customer.isDeactivateCandidate(due));
		// skip two closed services, but one still active
		Service closedService2 = new Service();
		closedService2.setFrequency(Frequency.MONTHLY);
		calendar.set(2007, Calendar.JANUARY, 1);
		closedService2.getPeriod().setFrom(calendar.getTime());
		calendar.set(2007, Calendar.JANUARY, 31);
		closedService2.getPeriod().setTo(calendar.getTime());
		services.clear();
		services.add(closedService2);
		services.add(closedService);
		assertFalse(customer.isDeactivateCandidate(due));
		// skip two closed, if max(services.to) == due
		calendar.set(2007, Calendar.MAY, 30); // closedService.to
		due = calendar.getTime();
		assertFalse(customer.isDeactivateCandidate(due));
		// skip two closed, if due OK, but lastlyBilled < max(services.to)
		calendar.set(2007, Calendar.JUNE, 1); // closedService.to + 1
		due = calendar.getTime();
		calendar.set(2007, Calendar.MAY, 29);
		billing.setLastlyBilled(calendar.getTime());
		assertFalse(customer.isDeactivateCandidate(due));
		// finally pass if due OK, lastly billed >= max(services.to)
		calendar.set(2007, Calendar.MAY, 30); // closedService.to
		billing.setLastlyBilled(calendar.getTime());
		assertTrue(customer.isDeactivateCandidate(due));
		calendar.set(2007, Calendar.JUNE, 1); // closedService.to + 1
		billing.setLastlyBilled(calendar.getTime());
		assertTrue(customer.isDeactivateCandidate(due));
		// skip if customer has lastly billed not set
		billing.setLastlyBilled(null);
		try {
			assertFalse(customer.isDeactivateCandidate(due));
		}
		catch (NullPointerException e) {
			fail();
		}
	}

    @Test
	public void testDeactivateDate() {
		Customer customer = new Customer();
		Billing billing = new Billing();
		customer.setBilling(billing);
		billing.setStatus(BillingStatus.INVOICE);
		List<Service> services = new ArrayList<Service>();
		customer.setServices(services);
		// configure service 2009-01-01 till 2009-04-30, billed
		Service service = new Service();
		service.setFrequency(Frequency.MONTHLY);
		Calendar calendar = new GregorianCalendar(2009, Calendar.JANUARY, 1);
		service.getPeriod().setFrom(calendar.getTime());
		calendar.set(2009, Calendar.APRIL, 30);
		service.getPeriod().setTo(calendar.getTime());
		services.add(service);
		calendar = new GregorianCalendar(2009, Calendar.APRIL, 30);
		customer.getBilling().setLastlyBilled(calendar.getTime());
		// due is the same day a bit later!
		Date due = new Date(calendar.getTime().getTime() + 1);
		System.out.println(service.getPeriod().getTo());
		System.out.println(due);
		assertTrue(service.getPeriod().getTo().before(due));
		assertFalse(customer.isDeactivateCandidate(due));
	}

    @Test
	public void testCutDayTimeMin() throws Exception {
		Calendar calendar = new GregorianCalendar(2009, Calendar.JANUARY, 1);
		long millis = calendar.getTimeInMillis();
		Date date = new Date(millis);
		Date dateWithTime = new Date(millis + 1);
		System.out.println(dateWithTime);
		assertFalse(date.equals(dateWithTime));
		Date cutDayTime = Customer.cutDayTime(dateWithTime);
		assertEquals(date, cutDayTime);
		assertFalse(date.before(cutDayTime));
	}

    @Test
	public void testCutDayTimeMax() throws Exception {
		Calendar calendar = new GregorianCalendar(2009, Calendar.JANUARY, 1);
		long millis = calendar.getTimeInMillis();
		Date date = new Date(millis);
		// maximum possible the same day with day time
		Date dateWithTime = new Date(millis + (1000 * 60 * 60 * 24) - 1);
		System.out.println(dateWithTime);
		assertFalse(date.equals(dateWithTime));
		Date cutDayTime = Customer.cutDayTime(dateWithTime);
		assertEquals(date, cutDayTime);
		assertFalse(date.before(cutDayTime));
	}
}
