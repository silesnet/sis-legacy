package cz.silesnet.service.invoice;

import javax.mail.internet.MimeBodyPart;
import java.io.PrintWriter;

/**
 * Interface for different invoice presentation formats. Writer can be used for
 * generating MimeBodyPart or to write to PrintWriter (eg. file)
 *
 * @author Richard Sikora
 */
public interface InvoiceWriter {
  public void write(PrintWriter writer);
}
