package cz.silesnet.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.util.Properties;

/**
 * Just for testing purposes of mail sending. This sender only logs events.
 *
 * @author Richard Sikora
 */
public class TestMailSender implements JavaMailSender {

  protected final Log log = LogFactory.getLog(getClass());

  private String smtpHost;

  public void setHost(String host) {
    this.smtpHost = host;
  }

  public MimeMessage createMimeMessage() {
    return new MimeMessage(Session.getDefaultInstance(new Properties()));
  }

  public MimeMessage createMimeMessage(InputStream arg0) throws MailException {
    return new MimeMessage(Session.getDefaultInstance(new Properties()));
  }

  public void send(MimeMessage email) throws MailException {
    log.debug("Sending email (" + smtpHost + ")");
  }

  public void send(MimeMessage[] emails) throws MailException {
    log.debug("Sending multiple emails " + emails.length + " (" + smtpHost
        + ")");
  }

  public void send(MimeMessagePreparator preparator) throws MailException {
    log.debug("Sending email (" + smtpHost + ")");
  }

  public void send(MimeMessagePreparator[] emails) throws MailException {
    log.debug("Sending multiple emails " + emails.length + " (" + smtpHost
        + ")");
  }

  public void send(SimpleMailMessage arg0) throws MailException {
    log.debug("Sending email (" + smtpHost + ")");
  }

  public void send(SimpleMailMessage[] emails) throws MailException {
    log.debug("Sending multiple emails " + emails.length + " (" + smtpHost
        + ")");
  }

}
