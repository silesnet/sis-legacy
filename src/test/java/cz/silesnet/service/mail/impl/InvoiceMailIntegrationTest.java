package cz.silesnet.service.mail.impl;

import cz.silesnet.model.enums.Country;
import cz.silesnet.service.invoice.Invoice;
import cz.silesnet.service.mail.MimeMessagePreparatorFactory;
import cz.silesnet.util.MessagesUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.subethamail.wiser.Wiser;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.unitils.UnitilsTestNG;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Mockito.*;

/**
 * User: der3k
 * Date: 28.10.2010
 * Time: 16:33:14
 */
@SpringApplicationContext({"context/sis-properties.xml", "context/sis-template.xml", "context/sis-email.xml", "context/sis-messages.xml"})
@Test(groups = "email")
public class InvoiceMailIntegrationTest extends UnitilsTestNG {

  @SpringBean("emailPreparatorFactory")
  private MimeMessagePreparatorFactory preparatorFactory;

  @SpringBean("mailSenderProduction")
  private JavaMailSender sender;

  private Wiser wiser;

  @BeforeMethod
  public void setUp() throws Exception {
    wiser = new Wiser();
    wiser.start();
  }

  @AfterMethod
  public void tearDown() throws Exception {
    wiser.stop();
  }

  @Test
  public void configuration() throws Exception {
    assertThat(preparatorFactory, is(not(nullValue())));
    assertThat(sender, is(not(nullValue())));
  }

  @Test
  public void emailCsInvoice() throws Exception {
    Invoice invoice = prepareInvoiceFixture(Country.CZ, false);
    MimeMessagePreparator preparator = preparatorFactory.newInstance(invoice, null);
    assertThat(preparator instanceof DelegatingMimeMessagePreparator, is(true));
//    sender.send(preparator);
  }

  @Test
  public void emailPlInvoice() throws Exception {
    Invoice invoice = prepareInvoiceFixture(Country.PL, false);
    MimeMessagePreparator preparator = preparatorFactory.newInstance(invoice, null);
    assertThat(preparator instanceof DelegatingMimeMessagePreparator, is(true));
//    sender.send(preparator);
  }

  @Test
  public void emailCsInvoiceSigned() throws Exception {
    Invoice invoice = prepareInvoiceFixture(Country.CZ, true);
    MimeMessagePreparator preparator = preparatorFactory.newInstance(invoice, null);
    assertThat(preparator instanceof DelegatingMimeMessagePreparator, is(true));
//    sender.send(preparator);
  }

  private Invoice prepareInvoiceFixture(Country country, boolean signed) {
    Invoice invoice = mock(Invoice.class);
    when(invoice.getShortFormatInLowerCase()).thenReturn("link");
    when(invoice.getCountry()).thenReturn(country);
    when(invoice.isSignedDelivery()).thenReturn(signed);
    when(invoice.getEmail()).thenReturn("nobody@localhost");
    when(invoice.getCopyToEmails()).thenReturn(new String[]{"nobody2@localhost", "nobody3@localhost"});
    when(invoice.getNumber()).thenReturn("1234567890");
    when(invoice.getPeriod()).thenReturn("01.01.2010 - 01.02.2010");
    when(invoice.getUuid()).thenReturn("aasdfjldsfsdf");
    return invoice;
  }
}
