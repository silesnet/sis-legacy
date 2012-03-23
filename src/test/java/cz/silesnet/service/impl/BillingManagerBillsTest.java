package cz.silesnet.service.impl;

import cz.silesnet.model.*;
import cz.silesnet.service.BillingManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Ignore;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.testng.Assert.*;

public class BillingManagerBillsTest {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    protected final Log log = LogFactory.getLog(getClass());

    @Test(groups = "integration")
    public void testMailSender() {
        String[] paths = {"context/sis-properties.xml", "context/sis-db.xml", "context/sis-hibernate.xml",
                "context/sis-dao.xml", "context/sis-bus.xml", "context/sis-transaction.xml", "context/sis-service.xml", "context/sis-email.xml", "context/sis-template.xml", "context/sis-billing.xml"};
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
        // bMgr.email(billFor);
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

    @Test(groups = "integration")
    public void testGetByStatus() {
        String[] paths = {"context/sis-properties.xml", "context/sis-db.xml", "context/sis-hibernate.xml",
                "context/sis-dao.xml", "context/sis-bus.xml", "context/sis-transaction.xml", "context/sis-service.xml", "context/sis-email.xml", "context/sis-template.xml", "context/sis-billing.xml"};
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
