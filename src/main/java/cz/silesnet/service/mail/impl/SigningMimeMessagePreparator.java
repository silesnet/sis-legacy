package cz.silesnet.service.mail.impl;

import cz.silesnet.service.SignedEmailGenerator;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.internet.MimeMessage;

/**
 * User: der3k
 * Date: 28.10.2010
 * Time: 15:28:37
 */
public class SigningMimeMessagePreparator implements MimeMessagePreparator {
  private SignedEmailGenerator signer;

  public SigningMimeMessagePreparator(final SignedEmailGenerator signer) {
    this.signer = signer;
  }

  public void prepare(final MimeMessage mimeMessage) throws Exception {
  }
}
