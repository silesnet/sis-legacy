package cz.silesnet.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cz.silesnet.model.enums.WirelessFrequency;

import junit.framework.TestCase;

public class WirelessTest extends TestCase {
	protected final Log log = LogFactory.getLog(getClass());


	public void testFrequencyMap() {

		Wireless w = new Wireless();
		w.setFrequency(WirelessFrequency.F2412);
		log.debug(w.getFrequency());
		assertTrue(WirelessFrequency.F2412.equals(w.getFrequency()));
		assertTrue(w.getFrequency().getId() == 10);
		assertTrue(w.getFrequency().getFrequency() == 2412);
		assertTrue(w.getFrequency().getChannel() == 1);
		
	}
}
