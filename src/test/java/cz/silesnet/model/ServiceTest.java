package cz.silesnet.model;

import cz.silesnet.model.enums.Country;
import cz.silesnet.model.enums.Frequency;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.Test;

import java.util.Date;

import static org.testng.Assert.*;

public class ServiceTest {
  protected final Log log = LogFactory.getLog(getClass());

  @Test
  public void testEnums() {

    Service s = new Service();
    s.setPeriod(new Period(new Date(), null));
    s.setName("wirelessHOME");
    s.setPrice(Integer.valueOf(1278));
    s.setFrequency(Frequency.MONTHLY);
    s.setConnectivity(new Connectivity(512, 256, false, 0));
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
    s.setConnectivity(new Connectivity(4, 2, true, 0));
    s.getConnectivity().setBps("M");
    s.setInfo("Poznamky...");

    // log.debug("Service: " + s);
    log.info("Invoice line text: " + s.getBillItemText(Country.CZ));
    log.info("Invoice line text: " + s.getBillItemText(Country.PL));
    // log.debug("Invoice line text: " + s.getShortInfo());
    s.getConnectivity().setIsAggregated(false);
    log.info("Invoice line text: " + s.getBillItemText(Country.CZ));
    log.info("Invoice line text: " + s.getBillItemText(Country.PL));
    s.getConnectivity().setUpload(null);
    log.info("Invoice line text: " + s.getBillItemText(Country.CZ));
    log.info("Invoice line text: " + s.getBillItemText(Country.PL));
    s.setConnectivity(null);
    log.info("Invoice line text: " + s.getBillItemText(Country.CZ));
    log.info("Invoice line text: " + s.getBillItemText(Country.PL));
  }

  @Test
  public void testIsNotConnectivity() throws Exception {
    Service service = new Service();
    service.getConnectivity().setDownload(1);
    assertTrue(service.getIsConnectivity());
  }

  @Test
  public void testIsConnectivity() throws Exception {
    Service service = new Service();
    service.getConnectivity().setDownload(1);
    service.getConnectivity().setUpload(1);
    assertTrue(service.getIsConnectivity());
  }

  @Test
  public void testShortInfo() throws Exception {
    Service service = new Service();
    service.setName("Wireless");
    service.getConnectivity().setDownload(4);
    service.getConnectivity().setUpload(2);
    service.getConnectivity().setBps("M");
    service.getConnectivity().setIsAggregated(true);
    assertEquals("Wireless 4/2 Mbps (&)", service.getShortInfo());
  }
}
