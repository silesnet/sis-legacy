package cz.silesnet.service.mail;

import cz.silesnet.model.Invoicing;
import cz.silesnet.service.invoice.Invoice;
import org.springframework.mail.javamail.MimeMessagePreparator;

/**
 * User: der3k
 * Date: 19.10.2010
 * Time: 19:58:52
 */
public interface MimeMessagePreparatorFactory {
  MimeMessagePreparator newInstance(Invoice invoice, String bcc);
}
