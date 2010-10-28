package cz.silesnet.service.mail.impl;

import cz.silesnet.service.invoice.Invoice;
import cz.silesnet.service.invoice.InvoiceWriter;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.internet.MimeMessage;

/**
 * User: der3k
 * Date: 28.10.2010
 * Time: 15:23:10
 */
public class InvoiceMimeMessagePreparator implements MimeMessagePreparator {
  private Invoice invoice;
  private InvoiceWriter writer;

  public InvoiceMimeMessagePreparator(final Invoice invoice, final InvoiceWriter writer) {
    this.invoice = invoice;
    this.writer = writer;
  }

  public void prepare(final MimeMessage mimeMessage) throws Exception {
  }
}
