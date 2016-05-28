package cz.silesnet.service.mail.impl;

import cz.silesnet.service.DocumentService;
import cz.silesnet.service.invoice.Invoice;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PdfInvoiceMimeMessagePreparator implements MimeMessagePreparator {
  public static final String PDF_MIME = "application/pdf";
  private final DocumentService documentService;
  private final Invoice invoice;

  public PdfInvoiceMimeMessagePreparator(final Invoice invoice, final DocumentService documentService) {
    this.invoice = invoice;
    this.documentService = documentService;
  }

  public void prepare(MimeMessage mimeMessage) throws Exception {
    final MimeMessageHelper email = new MimeMessageHelper(mimeMessage, true);
    // disabled
//    email.addAttachment(pdfFilename(), pdfResource(), PDF_MIME);
  }

  private ByteArrayResource pdfResource() {
    final InputStream pdfStream = documentService.invoicePdfStream(invoice.getUuid());
    return new ByteArrayResource(streamBytes(pdfStream));
  }

  private byte[] streamBytes(final InputStream pdfStream) {
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    final byte[] buffer = new byte[1024];
    int bytesRead;
    try {
      while ((bytesRead = pdfStream.read(buffer, 0, buffer.length)) != -1) {
        outputStream.write(buffer, 0, bytesRead);
      }
      outputStream.flush();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return outputStream.toByteArray();
  }

  private String pdfFilename() {
    return "" + invoice.getNumber() + ".pdf";
  }
}
