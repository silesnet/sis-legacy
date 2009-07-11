package cz.silesnet.model;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cz.silesnet.model.enums.Country;
import cz.silesnet.model.enums.Frequency;

import junit.framework.TestCase;

public class ServiceTest extends TestCase {
	protected final Log log = LogFactory.getLog(getClass());

	public void testEnums() {

		Service s = new Service();
		s.setPeriod(new Period(new Date(), null));
		s.setName("wirelessHOME");
		s.setPrice(Integer.valueOf(1278));
		s.setFrequency(Frequency.MONTHLY);
		s.setIsConnectivity(true);
		s.setConnectivity(new Connectivity(512, 256, false, 0));
		s.setInfo("Poznamky...");

		log.debug("Service: " + s);
		log.debug(s.getFrequency().getId());
		log.debug(s.getHistoricToString());
	}

	public void testEnumMapping() {
		Frequency p = Frequency.QQ;
		log.debug(p);
		int id = p.getId();
		log.debug("Id: " + id);
		Frequency pp = Frequency.ONE_TIME;
		log.debug(pp);
		log.debug("Reverse from id: " + pp.valueOf(id));
	}

	public void testBillText() {
		Service s = new Service();
		s.setPeriod(new Period(new Date(), null));
		s.setName("LANaccess");
		s.setPrice(Integer.valueOf(1278));
		s.setFrequency(Frequency.MONTHLY);
		s.setIsConnectivity(true);
		s.setConnectivity(new Connectivity(1024, 1024, true, 0));
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
}
