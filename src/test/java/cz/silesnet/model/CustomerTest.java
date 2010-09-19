package cz.silesnet.model;

import org.testng.annotations.Test;

import java.util.Date;

import static org.testng.Assert.*;

public class CustomerTest {

  @Test
  public void testIsSpsSynchronizedTrue() {
    Customer customer = new Customer();
    long now = System.currentTimeMillis();
    customer.setUpdated(new Date(now));
    customer.setSynchronized(new Date(now + 1));
    assertTrue(customer.isSpsSynchronized());
  }

  @Test
  public void testIsSpsSynchronizedGreater() {
    Customer customer = new Customer();
    long now = System.currentTimeMillis();
    customer.setUpdated(new Date(now));
    customer.setSynchronized(new Date(now - 1));
    assertFalse(customer.isSpsSynchronized());
  }

  @Test
  public void testIsSpsSynchronizedEqual() {
    Customer customer = new Customer();
    Date now = new Date();
    customer.setUpdated(now);
    customer.setSynchronized(now);
    assertFalse(customer.isSpsSynchronized());
  }

  @Test
  public void testIsSpsSynchronizedNull() {
    Customer customer = new Customer();
    assertFalse(customer.isSpsSynchronized());
  }

}
