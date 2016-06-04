package cz.silesnet.service.mail.impl;

import cz.silesnet.service.DocumentService;
import cz.silesnet.service.invoice.Invoice;
import cz.silesnet.service.invoice.InvoiceWriter;
import cz.silesnet.util.MessagesUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.internet.MimeMessage;
import java.io.*;
import java.util.Date;
import java.util.Locale;

/**
 * User: der3k
 * Date: 28.10.2010
 * Time: 15:23:10
 */
public class InvoiceMimeMessagePreparator implements MimeMessagePreparator {
  public static final String PDF_MIME = "application/pdf";

  protected final Log LOG = LogFactory.getLog(getClass());

  private Invoice invoice;
  private InvoiceWriter writer;
  private Locale locale = Locale.getDefault();
  private DocumentService documentService;

  public InvoiceMimeMessagePreparator(final Invoice invoice, final InvoiceWriter writer, DocumentService documentService) {
    this.invoice = invoice;
    this.writer = writer;
    this.documentService = documentService;
  }

  public void prepare(final MimeMessage mimeMessage) throws Exception {
    LOG.info("creating invoice email...");
    MimeMessageHelper email = new MimeMessageHelper(mimeMessage, true);
    locale = invoice.getCountry().getLocale();

    email.setSentDate(new Date());
    email.setFrom(message("billEmail.from"));
    email.setTo(invoice.getEmail());
    email.setCc(invoice.getCopyToEmails());
    email.setSubject(message("billEmail.subject", invoice.getNumber(), invoice.getPeriod()));
    email.getMimeMessage().addHeader("X-Priority", "1");
    email.getMimeMessage().addHeader("X-MSMail-Priority", "High");
//    email.setText(body, "windows-1250");
    email.setText(renderText(), false);
    LOG.info("adding PDF attachment to email...");
    email.addAttachment(pdfFilename(), pdfResource(), PDF_MIME);
  }

  private String message(String key) {
    return MessagesUtils.getMessage(key, locale);
  }

  private String message(String key, Object... params) {
    return MessagesUtils.getMessage(key, params, locale);
  }

  private String renderText() {
    StringWriter textWriter = new StringWriter();
    writer.write(new PrintWriter(textWriter));
    return textWriter.toString();
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
