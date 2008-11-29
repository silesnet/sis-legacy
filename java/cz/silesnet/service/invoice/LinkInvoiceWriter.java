package cz.silesnet.service.invoice;

import java.io.PrintWriter;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;

import cz.silesnet.model.Bill;

/**
 * Invoice writer producing text with link for onlive invoice delivery.
 * 
 * @author Richard Sikora
 *
 */
public class LinkInvoiceWriter extends AbstractInvoiceWriter {

	public LinkInvoiceWriter(Bill bill) {
		super(bill);
		// TODO Auto-generated constructor stub
	}

	public MimeBodyPart getMimeBodyPart() {
		MimeBodyPart mimeBodyPart = new MimeBodyPart();
		try {
			mimeBodyPart.setText("From Link writer getMimeBodyPart(): " + bill.getNumber());
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mimeBodyPart;
	}

	public void writeTo(PrintWriter writer) {
		writer.println("From Link writer writeTo(): " + bill.getNumber());
	}
}
