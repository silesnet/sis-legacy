package cz.silesnet.service.mail.impl;

import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.internet.MimeMessage;
import java.util.LinkedList;
import java.util.List;

/**
 * User: der3k
 * Date: 28.10.2010
 * Time: 15:31:30
 */
public class DelegatingMimeMessagePreparator implements MimeMessagePreparator{

  private List<MimeMessagePreparator> preparators = new LinkedList<MimeMessagePreparator>();

  public void addPreparator(MimeMessagePreparator preparator) {
    preparators.add(preparator);
  }
  public void prepare(final MimeMessage mimeMessage) throws Exception {
    for (MimeMessagePreparator preparator : preparators) {
      preparator.prepare(mimeMessage);
    }
  }
}
