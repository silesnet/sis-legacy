package cz.silesnet.model;

import junit.framework.TestCase;

public class BillTest extends TestCase {

	/*
	 * Test method for 'cz.silesnet.model.Bill.getNumber()'
	 */
	public void testGetNumber() {
		Bill bill = new Bill();
		bill.setNumber("200812");
		assertTrue("200812".equals(bill.getNumber()));
		assertTrue("12".equals(bill.getNumberShortPL()));
		assertTrue("12/2008".equals(bill.getNumberPL()));
		
		bill.setNumber("20080012");
		assertTrue("20080012".equals(bill.getNumber()));
		assertTrue("12".equals(bill.getNumberShortPL()));
		assertTrue("12/2008".equals(bill.getNumberPL()));
		
	}

}
