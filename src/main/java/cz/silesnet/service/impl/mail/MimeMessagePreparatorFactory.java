package cz.silesnet.service.impl.mail;

import cz.silesnet.model.Bill;
import org.springframework.mail.javamail.MimeMessagePreparator;

/**
 * User: der3k
 * Date: 19.10.2010
 * Time: 19:58:52
 */
public interface MimeMessagePreparatorFactory {
  MimeMessagePreparator getInstance(Bill bill);
}
