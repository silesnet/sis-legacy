package cz.silesnet.service.mail.impl;

import cz.silesnet.service.mail.SignedEmailGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.mail.smime.SMIMEException;
import org.bouncycastle.mail.smime.SMIMESignedGenerator;

import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * Concrete implementation of SignedEmailGenerator utlizing Bouncy Castle
 * SMIMESignedGenerator.
 *
 * @author Richard Sikora
 */
public class SignedEmailGeneratorBC implements SignedEmailGenerator {
  protected final Log log = LogFactory.getLog(getClass());

  private String certificatePath;

  private String privateKeyPath;

  private String privateKeyAlias;

  private String privateKeyPassword;

  private SMIMESignedGenerator smimeSignedGenerator;

  private boolean isConfigured = false;

  public Multipart generate(MimeBodyPart bodyPart) {
    if (!isConfigured)
      throw new IllegalStateException(
          "SignedEmailGenerator is not configured!");
    Multipart multiPart = null;
    try {
      multiPart = smimeSignedGenerator.generate(bodyPart, "BC");
      log.debug("MimeBodyPart SIGNED.");
    }
    catch (NoSuchAlgorithmException e) {
      throw new SignedEmailGenerateException(e);
    }
    catch (NoSuchProviderException e) {
      throw new SignedEmailGenerateException(e);
    }
    catch (SMIMEException e) {
      throw new SignedEmailGenerateException(e);
    }
    return multiPart;
  }

  public void configure() {
    isConfigured = false;
    try {
      configureInternal();
      log.info("SignedEmailGenerator CONFIGURED!");
      isConfigured = true;
    }
    catch (Exception e) {
//			e.printStackTrace();
      log.warn("SignedEmailGenerator NOT CONFIGURED: " + e.getMessage());
    }
  }

  private void configureInternal() throws CertificateException,
      NoSuchProviderException, KeyStoreException,
      NoSuchAlgorithmException, UnrecoverableKeyException, IOException {
    // add BouncyCastle JCE provider
    Security.addProvider(new BouncyCastleProvider());
    BufferedInputStream bis;
    // get certificate from file
    CertificateFactory cf = CertificateFactory.getInstance("X.509", "BC");
    bis = new BufferedInputStream(new FileInputStream(certificatePath));
    X509Certificate certificate = (X509Certificate) cf
        .generateCertificate(bis);
    // get private of from file
    KeyStore ks = KeyStore.getInstance("PKCS12", "SunJSSE");
    bis = new BufferedInputStream(new FileInputStream(privateKeyPath));
    ks.load(bis, privateKeyPassword.toCharArray());
    PrivateKey privateKey = (PrivateKey) ks.getKey(privateKeyAlias,
        privateKeyPassword.toCharArray());
    smimeSignedGenerator = new SMIMESignedGenerator();
    smimeSignedGenerator.addSigner(privateKey, certificate,
        SMIMESignedGenerator.DIGEST_SHA1);
  }

  public void setPrivateKeyAlias(String privateKeyAlias) {
    this.privateKeyAlias = privateKeyAlias;
  }

  public void setPrivateKeyPath(String privateKeyPath) {
    this.privateKeyPath = privateKeyPath;
  }

  public void setPrivateKeyPassword(String privateKeyPassword) {
    this.privateKeyPassword = privateKeyPassword;
  }

  public void setCertificatePath(String certificatePath) {
    this.certificatePath = certificatePath;
  }

}
