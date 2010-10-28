package cz.silesnet.service.mail.impl;

import cz.silesnet.service.SignedEmailGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;

/**
 * User: der3k
 * Date: 28.10.2010
 * Time: 15:28:37
 */
public class SigningMimeMessagePreparator implements MimeMessagePreparator {
  private final Log log = LogFactory.getLog(getClass());
  private final SignedEmailGenerator signer;

  public SigningMimeMessagePreparator(final SignedEmailGenerator signer) {
    this.signer = signer;
  }

  public void prepare(final MimeMessage mimeMessage) throws Exception {
    MimeBodyPart bodyPart = new MimeBodyPart();
    bodyPart.setText(mimeMessage.getContent().toString());
    try {
      Multipart multipart = signer.generate(bodyPart);
      mimeMessage.setContent(multipart, multipart.getContentType());
      log.debug("Email successfuly SIGNED.");
    } catch (RuntimeException e) {
      log.info("Email SIGNING FAILED for: " + mimeMessage.getFrom()[0].toString());
    }
  }
}
