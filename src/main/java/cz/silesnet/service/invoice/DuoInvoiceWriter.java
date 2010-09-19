package cz.silesnet.service.invoice;

import cz.silesnet.model.Bill;

import javax.mail.internet.MimeBodyPart;
import java.io.PrintWriter;

/**
 * Invoice writer producing invoice in WinDuo application format.
 *
 * @author Richard Sikora
 */
public class DuoInvoiceWriter extends AbstractInvoiceWriter {

  public DuoInvoiceWriter(Bill bill) {
    super(bill);
    // TODO Auto-generated constructor stub
  }

  public MimeBodyPart getMimeBodyPart() {
    // TODO Auto-generated method stub
    return null;
  }

  public void writeTo(PrintWriter writer) {
    // TODO Auto-generated method stub
  }
}
