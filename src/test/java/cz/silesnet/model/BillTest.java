package cz.silesnet.model;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class BillTest {

  @Test
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

  @Test
  public void testNet() throws Exception {
    Bill bill = prepareMixture();
    assertEquals(19.5F, bill.getNet());
  }

  @Test
  public void testNetRounded() throws Exception {
    Bill bill = prepareMixture();
    assertEquals(20, bill.getNetRounded());
  }

  @Test
  public void testVatRounded() throws Exception {
    Bill bill = prepareMixture();
    assertEquals(4, bill.getVatRounded());
  }

  @Test
  public void testBrt() throws Exception {
    Bill bill = prepareMixture();
    assertEquals(24, bill.getBrt());
  }

  @Test
  public void testItemBrt() throws Exception {
    Bill bill = prepareMixture();
    assertEquals(8.806F, bill.getItems().get(0).getBrt());
  }

  private Bill prepareMixture() {
    Bill bill = new Bill();
    BillItem item1 = new BillItem("Item 1", 0.74F, 10);
    BillItem item2 = new BillItem("Item 2", 1.21F, 10);
    bill.getItems().add(item1);
    item1.setBill(bill);
    bill.getItems().add(item2);
    item2.setBill(bill);
    bill.setVat(19);
    return bill;
  }
}
