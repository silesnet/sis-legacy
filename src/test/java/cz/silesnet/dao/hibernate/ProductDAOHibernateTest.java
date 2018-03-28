package cz.silesnet.dao.hibernate;

import cz.silesnet.model.enums.Country;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class ProductDAOHibernateTest {
  @Test
  public void testRenderCountry() throws Exception {
    final Country country = Country.CZ;
    assertEquals(country.getShortName().toUpperCase(), "CZ");
  }
}