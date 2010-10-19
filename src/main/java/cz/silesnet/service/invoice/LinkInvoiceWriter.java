package cz.silesnet.service.invoice;

import cz.silesnet.model.Bill;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import java.io.PrintWriter;

/**
 * Invoice writer producing text with link for onlive invoice delivery.
 *
 * @author Richard Sikora
 */
public class LinkInvoiceWriter extends AbstractInvoiceWriter {

  public LinkInvoiceWriter(Bill bill) {
    super(bill);
    // TODO Auto-generated constructor stub
  }

  public void writeTo(PrintWriter writer) {
    // TODO
  }
}
