package cz.silesnet.service.invoice;

import java.io.PrintWriter;

import javax.mail.internet.MimeBodyPart;

/**
 * Interface for different invoice presentation formats. Writer can be used for
 * generating MimeBodyPart or to write to PrintWriter (eg. file)
 * 
 * @author Richard Sikora
 * 
 */
public interface InvoiceWriter {
	public MimeBodyPart getMimeBodyPart();

	public void writeTo(PrintWriter writer);
}
