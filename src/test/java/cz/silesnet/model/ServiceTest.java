package cz.silesnet.model;

import cz.silesnet.model.enums.Country;
import cz.silesnet.model.enums.Frequency;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.Test;

import java.util.Date;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ServiceTest {
    protected final Log log = LogFactory.getLog(getClass());

    @Test
    public void testCsContracNo() {
        Service s = new Service();
        s.setId(10002301L);
        assertEquals(s.getContractNo(), "23");
    }

    @Test
    public void testCsContracNoSmall() {
        Service s = new Service();
        s.setId(10000101L);
        assertEquals(s.getContractNo(), "1");
    }

    @Test
    public void testCsContracNoOnetime() {
        Service s = new Service();
        s.setId(110002301L);
        assertEquals(s.getContractNo(), "23");
    }

    @Test
    public void testPlContracNoOnetime() {
        Service s = new Service();
        s.setId(120002301L);
        assertEquals(s.getContractNo(), "23");
    }
    
    @Test
    public void testPlContracNo() {
        Service s = new Service();
        s.setId(20002301L);
        assertEquals(s.getContractNo(), "23");
    }

    @Test
    public void testEnums() {

        Service s = new Service();
        s.setPeriod(new Period(new Date(), null));
        s.setName("wirelessHOME");
        s.setPrice(Integer.valueOf(1278));
        s.setFrequency(Frequency.MONTHLY);
        s.setInfo("Poznamky...");

        log.debug("Service: " + s);
        log.debug(s.getFrequency().getId());
        log.debug(s.getHistoricToString());
    }

    @Test
    public void testEnumMapping() {
        Frequency p = Frequency.QQ;
        log.debug(p);
        int id = p.getId();
        log.debug("Id: " + id);
        Frequency pp = Frequency.ONE_TIME;
        log.debug(pp);
        log.debug("Reverse from id: " + pp.valueOf(id));
    }

    @Test
    public void testBillText() {
        Service s = new Service();
        s.setPeriod(new Period(new Date(), null));
        s.setName("LANaccess");
        s.setPrice(Integer.valueOf(1278));
        s.setFrequency(Frequency.MONTHLY);
        s.setInfo("Poznamky...");

        // log.debug("Service: " + s);
        log.info("Invoice line text: " + s.getBillItemText(Country.CZ));
        log.info("Invoice line text: " + s.getBillItemText(Country.PL));
        // log.debug("Invoice line text: " + s.getShortInfo());
        log.info("Invoice line text: " + s.getBillItemText(Country.CZ));
        log.info("Invoice line text: " + s.getBillItemText(Country.PL));
        log.info("Invoice line text: " + s.getBillItemText(Country.CZ));
        log.info("Invoice line text: " + s.getBillItemText(Country.PL));
        log.info("Invoice line text: " + s.getBillItemText(Country.CZ));
        log.info("Invoice line text: " + s.getBillItemText(Country.PL));
    }

    @Test
    public void testShortInfo() throws Exception {
        Service service = new Service();
        service.setName("Wireless");
        assertEquals("Wireless", service.getShortInfo());
    }
}
