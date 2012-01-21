package cz.silesnet.service.mail.impl;

import cz.silesnet.service.invoice.Invoice;
import cz.silesnet.service.invoice.InvoiceWriter;
import cz.silesnet.service.invoice.InvoiceWriterFactory;
import cz.silesnet.service.mail.MimeMessagePreparatorFactory;
import cz.silesnet.service.mail.SignedEmailGenerator;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.util.Assert;

/**
 * User: der3k
 * Date: 26.10.2010
 * Time: 23:10:18
 */
public class SimpleMimeMessagePreparatorFactory implements MimeMessagePreparatorFactory, InitializingBean {

  private InvoiceWriterFactory writerFactory;
  private SignedEmailGenerator signer;

  public MimeMessagePreparator newInstance(final Invoice invoice) {
    InvoiceWriter writer = writerFactory.instanceOf(invoice);
    MimeMessagePreparator invoicePreparator = new InvoiceMimeMessagePreparator(invoice, writer);

    if (invoice.isSignedDelivery()) {
      return createSignedPreparatorFrom(invoicePreparator);
    } else {
      return invoicePreparator;
    }
  }

  private MimeMessagePreparator createSignedPreparatorFrom(final MimeMessagePreparator preparator) {
    DelegatingMimeMessagePreparator delegatingPreparator = new DelegatingMimeMessagePreparator();
    delegatingPreparator.addPreparator(preparator);
    delegatingPreparator.addPreparator(new SigningMimeMessagePreparator(signer));
    return delegatingPreparator;
  }

  public void setWriterFactory(final InvoiceWriterFactory writerFactory) {
    this.writerFactory = writerFactory;
  }

  public void setSigner(final SignedEmailGenerator signer) {
    this.signer = signer;
  }

  public void afterPropertiesSet() throws Exception {
    Assert.notNull(writerFactory);
    Assert.notNull(signer);
  }
}
