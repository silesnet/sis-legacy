package cz.silesnet.service.invoice;

import java.io.PrintWriter;

import javax.mail.internet.MimeBodyPart;

import cz.silesnet.model.Bill;

/**
 * Invoice writer generating invoice in PDF format.
 * 
 * @author Richard Sikora
 *
 */
public class PdfInvoiceWriter extends AbstractInvoiceWriter {

	public PdfInvoiceWriter(Bill bill) {
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
