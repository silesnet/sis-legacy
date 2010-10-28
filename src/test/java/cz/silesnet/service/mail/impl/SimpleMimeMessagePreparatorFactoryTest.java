package cz.silesnet.service.mail.impl;

import cz.silesnet.model.enums.Country;
import cz.silesnet.service.invoice.Invoice;
import cz.silesnet.service.invoice.InvoiceWriterFactory;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Mockito.*;

/**
 * User: der3k
 * Date: 28.10.2010
 * Time: 12:10:26
 */
public class SimpleMimeMessagePreparatorFactoryTest {

  private SimpleMimeMessagePreparatorFactory preparatorFactory;

  @BeforeMethod
  private void setUp() {
    preparatorFactory = new SimpleMimeMessagePreparatorFactory();
    InvoiceWriterFactory writerFactory = mock(InvoiceWriterFactory.class);
    preparatorFactory.setWriterFactory(writerFactory);
  }

  @Test
  public void newInstance() throws Exception {
    Invoice invoice = mockInvoiceFixture(false);
    MimeMessagePreparator preparator = preparatorFactory.newInstance(invoice);
    assertThat(preparator, is(instanceOf(InvoiceMimeMessagePreparator.class)));
  }

  @Test
  public void newInstanceWhenSigned() throws Exception {
    Invoice invoice = mockInvoiceFixture(true);
    MimeMessagePreparator preparator = preparatorFactory.newInstance(invoice);
    assertThat(preparator, is(instanceOf(DelegatingMimeMessagePreparator.class)));
  }

  private Invoice mockInvoiceFixture(boolean signed) {
    Invoice invoice = mock(Invoice.class);
    when(invoice.isSignedDelivery()).thenReturn(signed);
    return invoice;
  }
}
