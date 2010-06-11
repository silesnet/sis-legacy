package cz.silesnet.service;

import cz.silesnet.model.*;
import cz.silesnet.model.enums.Country;
import cz.silesnet.model.enums.Frequency;
import cz.silesnet.service.impl.BillingManagerImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.Test;

import java.util.*;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.*;

public class BillingManagerBillsTest {

    protected final Log log = LogFactory.getLog(getClass());

    @Test
    public void testPrepareBills() {
        // prepare customer
        Customer c = new Customer();
        c.setId(1L);
        c.setName("Test Billing Customer");
        c.getBilling().setFrequency(Frequency.MONTHLY);
        c.getBilling().setIsBilledAfter(false);
        Calendar lastlyBilled = new GregorianCalendar(2006, Calendar.FEBRUARY, 28);
        c.getBilling().setLastlyBilled(lastlyBilled.getTime());
        Service s1 = new Service();
        s1.setName("Testing service 1");
        s1.setFrequency(Frequency.MONTHLY);
        s1.setPrice(460);
        Calendar serviceFrom = new GregorianCalendar(2005, Calendar.JANUARY, 1);
        s1.setPeriod(new Period(serviceFrom.getTime(), null));
        c.getServices().add(s1);

        // prepare fake customer
        Customer c2 = PrepareMixture.getCustomerSimple();
        c2.getBilling().setLastlyBilled(null);
        c.setId(2L);

        List<Customer> cs = new ArrayList<Customer>();
        cs.add(c);
        cs.add(c2);

        // mock historyManager in BillingManager
        BillingManagerImpl bMgrI = new BillingManagerImpl();
        HistoryManager historyManager = mock(HistoryManager.class);
        bMgrI.setHistoryManager(historyManager);
        BillingManager bMgr = bMgrI;

        Calendar due = new GregorianCalendar(2006, Calendar.MARCH, 10);
        Invoicing invoicing = new Invoicing();
        invoicing.setName("Test invoicing");

        List<Bill> bills = bMgr.generateAll(invoicing, cs, due.getTime(), "0");
        assertNotNull(bills);
        assertTrue(bills.size() == 1);

        // modify customre so no bill will be created
        c.getBilling().setIsActive(false);
        bills = bMgr.generateAll(invoicing, cs, due.getTime(), "0");
        assertNotNull(bills);
        assertTrue(bills.size() == 0);

        c.getBilling().setIsActive(true);
        c.getContact().getAddress().setCountry(Country.PL);
        bills = bMgr.generateAll(invoicing, cs, due.getTime(), "0");
        assertNotNull(bills);
        assertTrue(bills.size() == 0);

        due.set(2000, Calendar.JANUARY, 1);
        c.getContact().getAddress().setCountry(Country.CZ);
        bills = bMgr.generateAll(invoicing, cs, due.getTime(), "0");
        assertNotNull(bills);
        assertTrue(bills.size() == 0);

    }

    @Test
    public void testPrepareBill() {

        int precision = BillingManagerImpl.sPrecison;
        float eAmount = 0;

        // prepare customer
        Customer c = new Customer();
        c.setId(1L);
        c.setName("Test Billing Customer");
        c.getBilling().setFrequency(Frequency.MONTHLY);
        c.getBilling().setIsBilledAfter(false);
        Calendar lastlyBilled = new GregorianCalendar(2006, Calendar.FEBRUARY, 28);
        c.getBilling().setLastlyBilled(lastlyBilled.getTime());
        Service s1 = new Service();
        s1.setName("Testing service 1");
        s1.setFrequency(Frequency.MONTHLY);
        s1.setPrice(460);
        Calendar serviceFrom = new GregorianCalendar(2005, Calendar.JANUARY, 1);
        Calendar serviceTo = new GregorianCalendar(2005, Calendar.JANUARY, 1);
        s1.setPeriod(new Period(serviceFrom.getTime(), null));
        c.getServices().add(s1);
        String hashCode = null;

        Calendar billFrom = new GregorianCalendar(2006, Calendar.JANUARY, 1);
        Calendar billTo = new GregorianCalendar(2006, Calendar.JANUARY, 1);

        // service is during whole period
        BillingManager bMgr = new BillingManagerImpl();
        Calendar due = new GregorianCalendar(2006, Calendar.MARCH, 10);
        Bill bill = bMgr.generate(c, due.getTime(), null);
        log.debug("Bill Period: " + bill.getPeriod().getPeriodString());
        log.debug("Bill totalPrice: " + bill.getTotalPrice());
        assertTrue(bill.getTotalPrice() == 460);
        assertTrue(bill.getItems().get(0).getLinePrice() == 460);
        assertTrue(bill.getItems().get(0).getPrice() == 460);
        eAmount = 1;
        assertTrue(bill.getItems().get(0).getAmount() == (float) (Math.round(eAmount * precision))
                / precision);
        hashCode = bill.getHashCode();
        assertNotNull(hashCode);
        log.debug("Bill hashCode: " + hashCode);
        billFrom.set(2006, Calendar.MARCH, 1);
        billTo.set(2006, Calendar.MARCH, 31);
        assertTrue(bill.getPeriod().getFrom().equals(billFrom.getTime()));
        assertTrue(bill.getPeriod().getTo().equals(billTo.getTime()));
        assertTrue(bill.getBillingDate().equals(due.getTime()));

        // service starts in period
        serviceFrom.set(2006, Calendar.MARCH, 15);
        s1.getPeriod().setFrom(serviceFrom.getTime());
        bill = bMgr.generate(c, due.getTime(), null);
        log.debug("Bill Period: " + bill.getPeriod().getPeriodString());
        log.debug("Bill totalPrice: " + bill.getTotalPrice());
        assertTrue(bill.getTotalPrice() == 252); // 17 days
        assertTrue(bill.getItems().get(0).getLinePrice() == 252);
        assertTrue(bill.getItems().get(0).getPrice() == 460);
        eAmount = (float) 0.5483871;
        assertTrue(bill.getItems().get(0).getAmount() == (float) (Math.round(eAmount * precision))
                / precision);
        // assertFalse(hashCode.equals(bill.getHashCode()));
        hashCode = bill.getHashCode();
        log.debug("Bill hashCode: " + hashCode);
        billFrom.set(2006, Calendar.MARCH, 15);
        billTo.set(2006, Calendar.MARCH, 31);
        assertTrue(bill.getPeriod().getFrom().equals(billFrom.getTime()));
        assertTrue(bill.getPeriod().getTo().equals(billTo.getTime()));
        billFrom.set(2006, Calendar.MARCH, 1);
        assertTrue(bill.getBillingDate().equals(due.getTime()));

        // service is in period
        serviceFrom.set(2006, Calendar.MARCH, 15);
        serviceTo.set(2006, Calendar.MARCH, 23);
        s1.getPeriod().setFrom(serviceFrom.getTime());
        s1.getPeriod().setTo(serviceTo.getTime());
        bill = bMgr.generate(c, due.getTime(), null);
        log.debug("Bill Period: " + bill.getPeriod().getPeriodString());
        log.debug("Bill totalPrice: " + bill.getTotalPrice());
        assertTrue(bill.getTotalPrice() == 134); // 9 days
        assertTrue(bill.getItems().get(0).getLinePrice() == 134);
        assertTrue(bill.getItems().get(0).getPrice() == 460);
        eAmount = (float) 0.29032257;
        assertTrue(bill.getItems().get(0).getAmount() == (float) (Math.round(eAmount * precision))
                / precision);
        // assertFalse(hashCode.equals(bill.getHashCode()));
        hashCode = bill.getHashCode();
        log.debug("Bill hashCode: " + hashCode);
        billFrom.set(2006, Calendar.MARCH, 15);
        billTo.set(2006, Calendar.MARCH, 23);
        assertTrue(bill.getPeriod().getFrom().equals(billFrom.getTime()));
        assertTrue(bill.getPeriod().getTo().equals(billTo.getTime()));
        billFrom.set(2006, Calendar.MARCH, 1);
        assertTrue(bill.getBillingDate().equals(due.getTime()));

        // service ends in period
        serviceFrom.set(2005, Calendar.JANUARY, 1);
        serviceTo.set(2006, Calendar.MARCH, 23);
        s1.getPeriod().setFrom(serviceFrom.getTime());
        s1.getPeriod().setTo(serviceTo.getTime());
        bill = bMgr.generate(c, due.getTime(), null);
        log.debug("Bill Period: " + bill.getPeriod().getPeriodString());
        log.debug("Bill totalPrice: " + bill.getTotalPrice());
        assertTrue(bill.getTotalPrice() == 341);
        assertTrue(bill.getItems().get(0).getLinePrice() == 341);
        assertTrue(bill.getItems().get(0).getPrice() == 460);
        eAmount = (float) 0.7419355;
        assertTrue(bill.getItems().get(0).getAmount() == (float) (Math.round(eAmount * precision))
                / precision);
        // assertFalse(hashCode.equals(bill.getHashCode()));
        hashCode = bill.getHashCode();
        log.debug("Bill hashCode: " + hashCode);
        billFrom.set(2006, Calendar.MARCH, 1);
        billTo.set(2006, Calendar.MARCH, 23);
        assertTrue(bill.getPeriod().getFrom().equals(billFrom.getTime()));
        assertTrue(bill.getPeriod().getTo().equals(billTo.getTime()));
        assertTrue(bill.getBillingDate().equals(due.getTime()));

        // more services in bill with different
        // periods and service.frequencies

        // annual service
        Service s2 = new Service();
        s2.setName("Testing service 2 web");
        s2.setPrice(1200);
        serviceFrom.set(2006, Calendar.JANUARY, 1);
        s2.getPeriod().setFrom(serviceFrom.getTime());
        s2.getPeriod().setTo(null);
        s2.setFrequency(Frequency.ANNUAL);
        c.getServices().add(s2);

        // one_time service
        Service s3 = new Service();
        s3.setName("Odpocet testing");
        s3.setPrice(-200);
        serviceFrom.set(2006, Calendar.MARCH, 25);
        s3.getPeriod().setFrom(serviceFrom.getTime());
        s3.getPeriod().setTo(null);
        s3.setFrequency(Frequency.ONE_TIME);
        c.getServices().add(s3);

        bill = bMgr.generate(c, due.getTime(), null);
        log.debug("Bill Period: " + bill.getPeriod().getPeriodString());
        log.debug("Bill totalPrice: " + bill.getTotalPrice());
        assertTrue(bill.getTotalPrice() == 241);
        log.debug(bill.getItems().get(0).getLinePrice());
        assertTrue(bill.getItems().get(0).getLinePrice() == 341);
        assertTrue(bill.getItems().get(0).getPrice() == 460);
        eAmount = (float) 0.7419355;
        assertTrue(bill.getItems().get(0).getAmount() == (float) (Math.round(eAmount * precision))
                / precision);
        log.debug(bill.getItems().get(1).getLinePrice());
        assertTrue(bill.getItems().get(1).getLinePrice() == 100);
        assertTrue(bill.getItems().get(1).getPrice() == 1200);
        eAmount = (float) 0.083333336;
        assertTrue(bill.getItems().get(1).getAmount() == (float) (Math.round(eAmount * precision))
                / precision);
        log.debug(bill.getItems().get(2).getLinePrice());
        assertTrue(bill.getItems().get(2).getLinePrice() == -200);
        assertTrue(bill.getItems().get(2).getPrice() == -200);
        eAmount = 1;
        assertTrue(bill.getItems().get(2).getAmount() == (float) (Math.round(eAmount * precision))
                / precision);
        // assertFalse(hashCode.equals(bill.getHashCode()));
        hashCode = bill.getHashCode();
        log.debug("Bill hashCode: " + hashCode);
        billFrom.set(2006, Calendar.MARCH, 1);
        billTo.set(2006, Calendar.MARCH, 31);
        assertTrue(bill.getPeriod().getFrom().equals(billFrom.getTime()));
        assertTrue(bill.getPeriod().getTo().equals(billTo.getTime()));
        assertTrue(bill.getBillingDate().equals(due.getTime()));

        // adjust a bit services so price will be -1 price bill
        s3.setPrice(-442);
        bill = bMgr.generate(c, due.getTime(), null);
        assertNotNull(bill);
        log.debug("Bill Period: " + bill.getPeriod().getPeriodString());
        log.debug("Bill totalPrice: " + bill.getTotalPrice());
        assertTrue(bill.getTotalPrice() == -1);

    }

    @SuppressWarnings("static-access")
    @Test
    public void testGetBillItemAmount() {
        BillingManagerImpl bMgr = new BillingManagerImpl();

        int precision = bMgr.sPrecison;
        float eAmount = 0;

        Calendar from = new GregorianCalendar();
        Calendar to = new GregorianCalendar();
        Period b = new Period();
        Period s = new Period();
        Frequency f = Frequency.MONTHLY;
        float amount = 0;

        // one month, MONTHLY
        from.set(2006, Calendar.FEBRUARY, 1);
        to.set(2006, Calendar.FEBRUARY, 28);
        b.setFrom(from.getTime());
        b.setTo(to.getTime());
        f = Frequency.MONTHLY;

        // whole service
        from.set(2006, Calendar.JANUARY, 1);
        s.setFrom(from.getTime());
        s.setTo(null);
        amount = bMgr.getBillItemAmount(b, s, f);
        log.debug(amount);
        eAmount = 1;
        assertTrue(amount == (float) (Math.round(eAmount * precision)) / precision);

        // service is out
        from.set(2006, Calendar.MARCH, 1);
        s.setFrom(from.getTime());
        s.setTo(null);
        amount = bMgr.getBillItemAmount(b, s, f);
        log.debug(amount);
        eAmount = 0;
        assertTrue(amount == (float) (Math.round(eAmount * precision)) / precision);

        // partial service
        from.set(2006, Calendar.JANUARY, 1);
        to.set(2006, Calendar.FEBRUARY, 20);
        s.setFrom(from.getTime());
        s.setTo(to.getTime());
        amount = bMgr.getBillItemAmount(b, s, f);
        log.debug(amount);
        eAmount = (float) 0.71428573;
        assertTrue(amount == (float) (Math.round(eAmount * precision)) / precision);

        // one day service
        from.set(2006, Calendar.FEBRUARY, 1);
        to.set(2006, Calendar.FEBRUARY, 1);
        s.setFrom(from.getTime());
        s.setTo(to.getTime());
        amount = bMgr.getBillItemAmount(b, s, f);
        log.debug(amount);
        eAmount = (float) 0.035714287;
        assertTrue(amount == (float) (Math.round(eAmount * precision)) / precision);

        // 3 months == Q, MONTHLY
        from.set(2006, Calendar.JANUARY, 1);
        to.set(2006, Calendar.MARCH, 31);
        b.setFrom(from.getTime());
        b.setTo(to.getTime());
        f = Frequency.MONTHLY;

        // whole service
        from.set(2006, Calendar.JANUARY, 1);
        s.setFrom(from.getTime());
        s.setTo(null);
        amount = bMgr.getBillItemAmount(b, s, f);
        log.debug(amount);
        eAmount = 3;
        assertTrue(amount == (float) (Math.round(eAmount * precision)) / precision);

        // service is out
        from.set(2005, Calendar.JANUARY, 1);
        to.set(2005, Calendar.DECEMBER, 31);
        s.setFrom(from.getTime());
        s.setTo(to.getTime());
        amount = bMgr.getBillItemAmount(b, s, f);
        log.debug(amount);
        eAmount = 0;
        assertTrue(amount == (float) (Math.round(eAmount * precision)) / precision);

        // partial service
        from.set(2006, Calendar.FEBRUARY, 1);
        to.set(2006, Calendar.MARCH, 20);
        s.setFrom(from.getTime());
        s.setTo(to.getTime());
        amount = bMgr.getBillItemAmount(b, s, f);
        log.debug(amount);
        eAmount = (float) 1.6451613;
        assertTrue(amount == (float) (Math.round(eAmount * precision)) / precision);

        from.set(2006, Calendar.MARCH, 7);
        to.set(2006, Calendar.APRIL, 20);
        s.setFrom(from.getTime());
        s.setTo(to.getTime());
        amount = bMgr.getBillItemAmount(b, s, f);
        log.debug(amount);
        eAmount = (float) 0.8064516;
        assertTrue(amount == (float) (Math.round(eAmount * precision)) / precision);

        // 3 months == Q, ANNUAL
        from.set(2006, Calendar.JANUARY, 1);
        to.set(2006, Calendar.MARCH, 31);
        b.setFrom(from.getTime());
        b.setTo(to.getTime());
        f = Frequency.ANNUAL;

        // whole service
        from.set(2006, Calendar.JANUARY, 1);
        s.setFrom(from.getTime());
        s.setTo(null);
        amount = bMgr.getBillItemAmount(b, s, f);
        log.debug(amount);
        eAmount = (float) 0.25; // 3 months are 0.25 of annual price
        assertTrue(amount == (float) (Math.round(eAmount * precision)) / precision);

        // service is out
        from.set(2005, Calendar.JANUARY, 1);
        to.set(2005, Calendar.DECEMBER, 31);
        s.setFrom(from.getTime());
        s.setTo(to.getTime());
        amount = bMgr.getBillItemAmount(b, s, f);
        log.debug(amount);
        eAmount = 0;
        assertTrue(amount == (float) (Math.round(eAmount * precision)) / precision);

        // partial service
        from.set(2006, Calendar.FEBRUARY, 1);
        to.set(2006, Calendar.MARCH, 20);
        s.setFrom(from.getTime());
        s.setTo(to.getTime());
        amount = bMgr.getBillItemAmount(b, s, f);
        log.debug(amount);
        eAmount = (float) 0.13709678; // 1.65 monts are 0.14 of annual price
        assertTrue(amount == (float) (Math.round(eAmount * precision)) / precision);

        // one day service
        from.set(2006, Calendar.FEBRUARY, 1);
        to.set(2006, Calendar.FEBRUARY, 1);
        s.setFrom(from.getTime());
        s.setTo(to.getTime());
        amount = bMgr.getBillItemAmount(b, s, f);
        log.debug(amount);
        eAmount = (float) 0.0029761905;
        assertTrue(amount == (float) (Math.round(eAmount * precision)) / precision);

        // one month, ONE_TIME
        from.set(2006, Calendar.FEBRUARY, 1);
        to.set(2006, Calendar.FEBRUARY, 28);
        b.setFrom(from.getTime());
        b.setTo(to.getTime());
        f = Frequency.ONE_TIME;

        // all one time are auto included
        s.setFrom(null);
        s.setTo(null);
        amount = bMgr.getBillItemAmount(b, s, f);
        log.debug(amount);
        eAmount = 1;
        assertTrue(amount == (float) (Math.round(eAmount * precision)) / precision);

    }

    @Test
    public void testBillingPeriods() {
        // set some vars
        BillingManagerImpl bMgr = new BillingManagerImpl();
        Calendar lastlyBilled = new GregorianCalendar();
        Calendar due = new GregorianCalendar();
        Calendar from = new GregorianCalendar();
        Calendar to = new GregorianCalendar();
        Period p = null;

        // / prepare customer billing component
        Billing b = new Billing();

        // billing forward, MONTHLY
        b.setFrequency(Frequency.MONTHLY);
        b.setIsBilledAfter(false);
        log.debug("Billing forward, MONTHLY");

        lastlyBilled.set(2006, Calendar.MARCH, 31);
        due.set(2006, Calendar.APRIL, 10);
        b.setLastlyBilled(lastlyBilled.getTime());
        p = bMgr.getIvoicePeriod(b, due.getTime());
        log.debug("Billing period : " + p.getPeriodString());
        from.set(2006, Calendar.APRIL, 1);
        to.set(2006, Calendar.APRIL, 30);
        assertTrue(p.getFrom().equals(from.getTime()));
        assertTrue(p.getTo().equals(to.getTime()));

        lastlyBilled.set(2006, Calendar.APRIL, 15);
        due.set(2006, Calendar.APRIL, 10);
        b.setLastlyBilled(lastlyBilled.getTime());
        p = bMgr.getIvoicePeriod(b, due.getTime());
        log.debug("Billing period : " + p.getPeriodString());
        from.set(2006, Calendar.APRIL, 16);
        to.set(2006, Calendar.APRIL, 30);
        assertTrue(p.getFrom().equals(from.getTime()));
        assertTrue(p.getTo().equals(to.getTime()));

        // billing forward, Q
        b.setFrequency(Frequency.Q);
        b.setIsBilledAfter(false);
        log.debug("Billing forward, Q");

        lastlyBilled.set(2006, Calendar.MARCH, 31);
        due.set(2006, Calendar.APRIL, 10);
        b.setLastlyBilled(lastlyBilled.getTime());
        p = bMgr.getIvoicePeriod(b, due.getTime());
        log.debug("Billing period : " + p.getPeriodString());
        from.set(2006, Calendar.APRIL, 1);
        to.set(2006, Calendar.JUNE, 30);
        assertTrue(p.getFrom().equals(from.getTime()));
        assertTrue(p.getTo().equals(to.getTime()));

        // lastlyBilled not last of month, Q
        lastlyBilled.set(2006, Calendar.FEBRUARY, 15);
        due.set(2006, Calendar.APRIL, 10);
        b.setLastlyBilled(lastlyBilled.getTime());
        p = bMgr.getIvoicePeriod(b, due.getTime());
        log.debug("Billing period : " + p.getPeriodString());
        from.set(2006, Calendar.FEBRUARY, 16);
        to.set(2006, Calendar.JUNE, 30);
        assertTrue(p.getFrom().equals(from.getTime()));
        assertTrue(p.getTo().equals(to.getTime()));

        // odd due date
        lastlyBilled.set(2006, Calendar.FEBRUARY, 15);
        due.set(2006, Calendar.MAY, 10);
        b.setLastlyBilled(lastlyBilled.getTime());
        p = bMgr.getIvoicePeriod(b, due.getTime());
        log.debug("Billing period : " + p.getPeriodString());
        from.set(2006, Calendar.FEBRUARY, 16);
        to.set(2006, Calendar.JUNE, 30);
        assertTrue(p.getFrom().equals(from.getTime()));
        assertTrue(p.getTo().equals(to.getTime()));

        lastlyBilled.set(2006, Calendar.MAY, 15);
        due.set(2006, Calendar.MAY, 10);
        b.setLastlyBilled(lastlyBilled.getTime());
        p = bMgr.getIvoicePeriod(b, due.getTime());
        log.debug("Billing period : " + p.getPeriodString());
        from.set(2006, Calendar.MAY, 16);
        to.set(2006, Calendar.JUNE, 30);
        assertTrue(p.getFrom().equals(from.getTime()));
        assertTrue(p.getTo().equals(to.getTime()));

        lastlyBilled.set(2006, Calendar.MARCH, 31);
        due.set(2006, Calendar.MARCH, 10);
        b.setLastlyBilled(lastlyBilled.getTime());
        p = bMgr.getIvoicePeriod(b, due.getTime());
        log.debug("Billing period : " + p);
        assertNull(p);

        lastlyBilled.set(2005, Calendar.DECEMBER, 31);
        due.set(2006, Calendar.APRIL, 2);
        b.setLastlyBilled(lastlyBilled.getTime());
        p = bMgr.getIvoicePeriod(b, due.getTime());
        log.debug("Billing period : " + p.getPeriodString());
        from.set(2006, Calendar.JANUARY, 1);
        to.set(2006, Calendar.JUNE, 30);
        assertTrue(p.getFrom().equals(from.getTime()));
        assertTrue(p.getTo().equals(to.getTime()));

        // billing backward, MONTHLY
        b.setFrequency(Frequency.MONTHLY);
        b.setIsBilledAfter(true);
        log.debug("Billing backward, MONTHLY");

        lastlyBilled.set(2006, Calendar.FEBRUARY, 28);
        due.set(2006, Calendar.APRIL, 10);
        b.setLastlyBilled(lastlyBilled.getTime());
        p = bMgr.getIvoicePeriod(b, due.getTime());
        log.debug("Billing period : " + p.getPeriodString());
        from.set(2006, Calendar.MARCH, 1);
        to.set(2006, Calendar.MARCH, 31);
        assertTrue(p.getFrom().equals(from.getTime()));
        assertTrue(p.getTo().equals(to.getTime()));

        lastlyBilled.set(2006, Calendar.FEBRUARY, 15);
        due.set(2006, Calendar.APRIL, 10);
        b.setLastlyBilled(lastlyBilled.getTime());
        p = bMgr.getIvoicePeriod(b, due.getTime());
        log.debug("Billing period : " + p.getPeriodString());
        from.set(2006, Calendar.FEBRUARY, 16);
        to.set(2006, Calendar.MARCH, 31);
        assertTrue(p.getFrom().equals(from.getTime()));
        assertTrue(p.getTo().equals(to.getTime()));

        lastlyBilled.set(2006, Calendar.MARCH, 15);
        due.set(2006, Calendar.APRIL, 10);
        b.setLastlyBilled(lastlyBilled.getTime());
        p = bMgr.getIvoicePeriod(b, due.getTime());
        log.debug("Billing period : " + p.getPeriodString());
        from.set(2006, Calendar.MARCH, 16);
        to.set(2006, Calendar.MARCH, 31);
        assertTrue(p.getFrom().equals(from.getTime()));
        assertTrue(p.getTo().equals(to.getTime()));

        lastlyBilled.set(2006, Calendar.MARCH, 30);
        due.set(2006, Calendar.APRIL, 10);
        b.setLastlyBilled(lastlyBilled.getTime());
        p = bMgr.getIvoicePeriod(b, due.getTime());
        log.debug("Billing period : " + p.getPeriodString());
        from.set(2006, Calendar.MARCH, 31);
        to.set(2006, Calendar.MARCH, 31);
        assertTrue(p.getFrom().equals(from.getTime()));
        assertTrue(p.getTo().equals(to.getTime()));

        // billing backward, Q
        b.setFrequency(Frequency.Q);
        b.setIsBilledAfter(true);
        log.debug("Billing backward, Q");

        lastlyBilled.set(2006, Calendar.MARCH, 15);
        due.set(2006, Calendar.APRIL, 10);
        b.setLastlyBilled(lastlyBilled.getTime());
        p = bMgr.getIvoicePeriod(b, due.getTime());
        log.debug("Billing period : " + p.getPeriodString());
        from.set(2006, Calendar.MARCH, 16);
        to.set(2006, Calendar.MARCH, 31);
        assertTrue(p.getFrom().equals(from.getTime()));
        assertTrue(p.getTo().equals(to.getTime()));

        // if due date is odd do not generate bill
        lastlyBilled.set(2006, Calendar.MARCH, 15);
        due.set(2006, Calendar.MAY, 10);
        b.setLastlyBilled(lastlyBilled.getTime());
        p = bMgr.getIvoicePeriod(b, due.getTime());
        log.debug("Billing period : " + p);
        assertNull(p);

    }

    @Test
    public void testMailSender() {
        String[] paths = {"context/sis-properties.xml", "context/sis-db.xml", "context/sis-hibernate.xml",
                "context/sis-dao.xml", "context/sis-transaction.xml", "context/sis-service.xml", "context/sis-email.xml"};
        ApplicationContext ctx = new ClassPathXmlApplicationContext(paths);
        assertNotNull(ctx);

        Bill bill = PrepareMixture.getBill();
        bill.getInvoicedCustomer().getContact().setEmail("rsi.news@quick.cz");
        bill.getInvoicedCustomer().getBilling().setDeliverCopyEmail(
                "sikora@silesnet.cz,sikora@gympol.cz");
        Calendar cFrom = new GregorianCalendar(2006, Calendar.MARCH, 1);
        Calendar cTo = new GregorianCalendar(2006, Calendar.MARCH, 31);
        bill.setPeriod(new Period(cFrom.getTime(), cTo.getTime()));
        bill.setHashCode("2853a22d-b4b9-11da-9de4-958326744870");

        @SuppressWarnings("unused")
        BillingManager bMgr = (BillingManager) ctx.getBean("billingManager");
        // bMgr.email(bill);
    }

    @Test
    public void testVAT() {
        BillItem bi = new BillItem("Text 1", 1.0F, 838);
        Bill b = PrepareMixture.getBillSimple();
        b.setItems(new ArrayList<BillItem>());
        b.setVat(19);
        b.getItems().add(bi);

        float itemVat = 0;
        try {
            itemVat = bi.getLinePriceVat();
            fail();
        } catch (IllegalStateException e) {
            log.debug("Got expected exception: " + e);
        }
        bi.setBill(b);

        assertTrue(bi.getLinePrice() == 838);
        itemVat = bi.getLinePriceVat();
        log.debug("Item VAT: " + itemVat);
        assertTrue(itemVat == 997.22F);

        float billVat = b.getBillVat();
        log.debug("Bill VAT: " + billVat);
        assertTrue(billVat == 159.22F);
        float billRoundedVat = b.getBillRoundedVat();
        log.debug("Bill rounder VAT: " + billRoundedVat);
        assertTrue(billRoundedVat == 159.0F);

        bi.setPrice(839);
        billVat = b.getBillVat();
        log.debug("Bill VAT: " + billVat);
        assertTrue(billVat == 159.41F);
        billRoundedVat = b.getBillRoundedVat();
        log.debug("Bill rounder VAT: " + billRoundedVat);
        assertTrue(billRoundedVat == 159.5F);

        bi.setPrice(840);
        billVat = b.getBillVat();
        log.debug("Bill VAT: " + billVat);
        assertTrue(billVat == 159.6F);
        billRoundedVat = b.getBillRoundedVat();
        log.debug("Bill rounder VAT: " + billRoundedVat);
        assertTrue(billRoundedVat == 159.5F);

        bi.setPrice(841);
        billVat = b.getBillVat();
        log.debug("Bill VAT: " + billVat);
        assertTrue(billVat == 159.79F);
        billRoundedVat = b.getBillRoundedVat();
        log.debug("Bill rounder VAT: " + billRoundedVat);
        assertTrue(billRoundedVat == 160.0F);
    }

    @Test
    public void testHashCode2() {
        // compose new invoice uuid from customer db id and invoice db id
        // "1" 5 digit customer id 8 digits of invoice id -> hex
        long id = new Long(String.format("1%05d", 9999) + String.format("%08d", 999999));
        long id2 = new Long(String.format("1%05d", new Long("9999"))
                + String.format("%08d", new Long("999999")));
        log.info(id);
        log.info(String.format("%d", id));
        log.info(String.format("%H", id));
        log.info(Long.toHexString(id));
        log.info(Long.toHexString(id).toUpperCase());
        log.info(Long.toHexString(id2));
    }

    @Test
    public void testHashCode3() {
        Long customerId = new Long("14") + 1000000;
        Long customerId2 = new Long("14");
        Long timeStamp = new Long((new Date()).getTime());
        String hashCode = Long.toHexString(customerId) + Long.toHexString(timeStamp);
        String hashCode2 = Long.toHexString(customerId2 + 1000000)
                + Long.toHexString((new Date()).getTime());
        log.info(hashCode);
        log.info(Long.toHexString(customerId) + "z" + Long.toHexString(timeStamp));
        log.info(hashCode2);
    }

    @Test
    public void testGetByStatus() {
        String[] paths = {"context/sis-properties.xml", "context/sis-db.xml", "context/sis-hibernate.xml",
                "context/sis-dao.xml", "context/sis-transaction.xml", "context/sis-service.xml", "context/sis-email.xml"};
        ApplicationContext ctx = new ClassPathXmlApplicationContext(paths);
        assertNotNull(ctx);
        @SuppressWarnings("unused")
        BillingManager bMgr = (BillingManager) ctx.getBean("billingManager");

        // get for winDuo
        List<Bill> bills = bMgr.getByStatus(null, true, null, null, false);
        log.info("winDuo export");
        log.info(bills.size());
        for (Bill bill : bills) {
            Customer c = bMgr.fetchCustomer(bill);
            log.info(c.getName());
        }

        // get for undelivered
        bills = bMgr.getByStatus(null, true, true, false, false);
        log.info("Undelivered bills");
        log.info(bills.size());
        for (Bill bill : bills) {
            Customer c = bMgr.fetchCustomer(bill);
            log.info(c.getName());
        }

        // get for delivered
        bills = bMgr.getByStatus(null, true, true, true, false);
        log.info("Delivered bills");
        log.info(bills.size());
        for (Bill bill : bills) {
            Customer c = bMgr.fetchCustomer(bill);
            log.info(c.getName());
        }

    }

}
