package cz.silesnet.service.impl;

import cz.silesnet.model.Invoicing;
import cz.silesnet.model.enums.Country;
import cz.silesnet.model.invoice.BillingContext;
import cz.silesnet.model.invoice.BillingContextFactory;
import cz.silesnet.service.BillingManager;
import org.testng.annotations.Test;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.spring.annotation.SpringBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * User: der3k
 * Date: 3.5.11
 * Time: 21:21
 */
@DataSet("/cz/silesnet/service/impl/BillingManagerIntegrationTest.xml")
public class BillingManagerIntegrationTest extends BaseServiceTestCase {

  @SpringBean("billingManager")
  private BillingManager manager;

  @SpringBean("billingContextFactory")
  private BillingContextFactory contextFactory;

  @Test
  public void billsForInvoicing() throws Exception {
    Invoicing invoicing = new Invoicing();
    invoicing.setId(1000L);
    invoicing.setHistoryId(5000L);
    invoicing.setCountry(Country.CZ);
    invoicing.setNumberingBase("2011000");
    invoicing.setInvoicingDate(date("2011-01-05"));
    manager.billCustomersIn(invoicing);
    assertThat(manager.getInvoices(invoicing).size(), is(2));
  }

  private static Date date(String date) {
    Date parsed;
    try {
      parsed = new SimpleDateFormat("yyyy-MM-dd").parse(date);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
    return parsed;
  }
}
