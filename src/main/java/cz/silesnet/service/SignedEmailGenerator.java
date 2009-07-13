package cz.silesnet.service;

import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;

/**
 * Interface definig functionality of signed email generator.
 * 
 * @author Richard Sikora
 */
public interface SignedEmailGenerator {
	public Multipart generate(MimeBodyPart bodyPart);
}
