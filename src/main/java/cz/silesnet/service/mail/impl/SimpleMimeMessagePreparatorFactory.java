package cz.silesnet.service.mail.impl;

import cz.silesnet.service.DocumentService;
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
  private DocumentService documentService;

  public MimeMessagePreparator newInstance(final Invoice invoice) {
    DelegatingMimeMessagePreparator messagePreparator = new DelegatingMimeMessagePreparator();
    InvoiceWriter writer = writerFactory.instanceOf(invoice);
    messagePreparator.addPreparator(new InvoiceMimeMessagePreparator(invoice, writer));
//    messagePreparator.addPreparator(new PdfInvoiceMimeMessagePreparator(invoice, documentService));
    if (invoice.isSignedDelivery()) {
      messagePreparator.addPreparator(new SigningMimeMessagePreparator(signer));
    }
    return  messagePreparator;
  }

  public void setWriterFactory(final InvoiceWriterFactory writerFactory) {
    this.writerFactory = writerFactory;
  }

  public void setSigner(final SignedEmailGenerator signer) {
    this.signer = signer;
  }

  public void setDocumentService(DocumentService documentService) {
    this.documentService = documentService;
  }

  public void afterPropertiesSet() throws Exception {
    Assert.notNull(writerFactory);
    Assert.notNull(signer);
//    Assert.notNull(documentService);
  }
}
