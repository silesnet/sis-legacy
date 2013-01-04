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

    @Test
    public void testBrtWithRounding() throws Exception {
        Bill bill = prepareMixture();
        bill.getItems().clear();
        bill.getItems().add(new BillItem("Item 1", 292F, 1));
        bill.setVat(20);

        assertEquals(350, bill.getBrt());
    }

    @Test
    public void test21VatRounding() throws Exception {
        final Bill bill = prepareMixture();
        bill.setVat(21);
        bill.getItems().clear();
        final BillItem item = new BillItem("Item", 0F, 1);
        bill.getItems().add(item);

        item.setAmount(260F);
        assertEquals(315, bill.getBrt());

        item.setAmount(310F);
        assertEquals(375, bill.getBrt());

        item.setAmount(360F);
        assertEquals(436, bill.getBrt());

        item.setAmount(410F);
        assertEquals(496, bill.getBrt());
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
