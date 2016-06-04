package cz.silesnet.service.mail.impl;

import cz.silesnet.service.invoice.Invoice;
import cz.silesnet.service.invoice.InvoiceWriter;
import cz.silesnet.util.MessagesUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.internet.MimeMessage;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Locale;

/**
 * User: der3k
 * Date: 28.10.2010
 * Time: 15:23:10
 */
public class InvoiceMimeMessagePreparator implements MimeMessagePreparator {
  protected final Log LOG = LogFactory.getLog(getClass());

  private Invoice invoice;
  private InvoiceWriter writer;
  private Locale locale = Locale.getDefault();

  public InvoiceMimeMessagePreparator(final Invoice invoice, final InvoiceWriter writer) {
    this.invoice = invoice;
    this.writer = writer;
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
    final String body = renderText();
    LOG.info(body);
//    email.setText(body, "windows-1250");
    email.setText(body, false);
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

}
