package cz.silesnet.service.invoice.impl;

import cz.silesnet.model.enums.Country;
import cz.silesnet.service.invoice.Invoice;
import cz.silesnet.service.invoice.InvoiceWriter;
import org.testng.annotations.Test;
import org.unitils.UnitilsTestNG;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * User: der3k
 * Date: 21.10.2010
 * Time: 19:50:50
 */
@SpringApplicationContext({"context/sis-properties.xml", "context/sis-template.xml"})
@Test(groups = "integration")
public class FreeMarkerInvoiceWriterFactoryTest extends UnitilsTestNG {

  private static final String INVOICE_NUMBER = "12345";
  private static final String NUMBER = "Invoice number: " + INVOICE_NUMBER;
  private static final String SIGNATURE = "Sent by: accountant.cs@silesnet.cz";
  private static final String UTF8 = "UTF-8: ěščřžýáíéůŘŽČŮąężŻ";
  private static final String COUNTRY = "Country: pl";

  @SpringBean("invoiceWriterFactory")
  private FreeMarkerInvoiceWriterFactory factory;


  @Test
  public void testInstanceOf() throws Exception {
    Invoice invoice = preprateInvoiceMock(Country.CZ, "number");
    InvoiceWriter writer = factory.instanceOf(invoice);
    assertThat(writer, is(not(nullValue())));
  }

  @Test
  public void model() throws Exception {
    Invoice invoice = preprateInvoiceMock(Country.CZ, "number");
    when(invoice.getNumber()).thenReturn(INVOICE_NUMBER);
    assertThat(renderInvoice(invoice), is(NUMBER));
  }

  @Test
  public void systemVariables() throws Exception {
    Invoice invoice = preprateInvoiceMock(Country.CZ, "signature");
    assertThat(renderInvoice(invoice), is(SIGNATURE));
  }

  @Test
  public void utf8() throws Exception {
    Invoice invoice = preprateInvoiceMock(Country.CZ, "utf8");
    assertThat(renderInvoice(invoice), is(UTF8));
  }

  @Test
  public void country() throws Exception {
    Invoice invoice = preprateInvoiceMock(Country.PL, "country");
    assertThat(renderInvoice(invoice), is(COUNTRY));

  }

  private Invoice preprateInvoiceMock(Country country, String format) {
    Invoice invoice = mock(Invoice.class);
    when(invoice.getCountry()).thenReturn(country);
    when(invoice.getShortFormatInLowerCase()).thenReturn(format);
    return invoice;
  }

  private String renderInvoice(Invoice invoice) {
    InvoiceWriter writer = factory.instanceOf(invoice);
    StringWriter invoiceWriter = new StringWriter();
    writer.write(new PrintWriter(invoiceWriter));
    return invoiceWriter.toString();
  }

}