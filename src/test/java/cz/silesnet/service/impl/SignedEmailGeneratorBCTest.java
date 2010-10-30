package cz.silesnet.service.impl;

import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.mail.smime.SMIMESignedGenerator;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.bouncycastle.x509.extension.AuthorityKeyIdentifierStructure;
import org.bouncycastle.x509.extension.SubjectKeyIdentifierStructure;
import org.springframework.core.io.ClassPathResource;
import org.testng.annotations.Test;

import javax.security.auth.x500.X500Principal;
import java.io.FileWriter;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * User: der3k
 * Date: 30.10.2010
 * Time: 9:48:07
 */
public class SignedEmailGeneratorBCTest {

  @Test(groups = "email")
  public void configure() throws Exception {
    Security.addProvider(new BouncyCastleProvider());

    KeyStore sisStore = loadKeyStore("crypto/sis.pfx", "sis");
    KeyPair sisPair = getKeyPairFromKeyStore(sisStore, "sis", "sis");

    CertificateFactory cf = CertificateFactory.getInstance("X.509", "BC");
    X509Certificate certificate = (X509Certificate) cf.generateCertificate(new ClassPathResource("crypto/sis-ca.pem").getInputStream());

    SMIMESignedGenerator smimeSignedGenerator = new SMIMESignedGenerator();
    smimeSignedGenerator.addSigner(sisPair.getPrivate(), certificate, SMIMESignedGenerator.DIGEST_SHA1);

  }

  private void createCertificate() throws Exception {
    Security.addProvider(new BouncyCastleProvider());

    KeyStore sisStore = loadKeyStore("crypto/sis.pfx", "sis");
    KeyPair sisPair = getKeyPairFromKeyStore(sisStore, "sis", "sis");

    KeyStore caStore = loadKeyStore("crypto/ca.pfx", "ca");
    KeyPair caPair = getKeyPairFromKeyStore(caStore, "ca", "ca");
    X509Certificate caCert = (X509Certificate) caStore.getCertificate("ca");


    X509V3CertificateGenerator generator = new X509V3CertificateGenerator();
    generator.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
    generator.setIssuerDN(caCert.getSubjectX500Principal());
    generator.setNotBefore(new Date(System.currentTimeMillis() - 50000));
    generator.setNotAfter(new Date(System.currentTimeMillis() + 3153600000000L));
    generator.setSubjectDN(new X500Principal("CN=SIS"));
    generator.setPublicKey(sisPair.getPublic());
    generator.setSignatureAlgorithm("SHA256WithRSAEncryption");

    generator.addExtension(X509Extensions.AuthorityKeyIdentifier, false, new AuthorityKeyIdentifierStructure(caCert));
    generator.addExtension(X509Extensions.SubjectKeyIdentifier, false, new SubjectKeyIdentifierStructure(sisPair.getPublic()));

    generator.addExtension(X509Extensions.BasicConstraints, true, new BasicConstraints(false));
    generator.addExtension(X509Extensions.KeyUsage, true, new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));
    generator.addExtension(X509Extensions.ExtendedKeyUsage, true, new ExtendedKeyUsage(KeyPurposeId.id_kp_serverAuth));
    generator.addExtension(X509Extensions.SubjectAlternativeName, false, new GeneralNames(new GeneralName(GeneralName.rfc822Name, "info@localhost")));

    X509Certificate certificate = generator.generateX509Certificate(sisPair.getPrivate(), "BC");
    X509Certificate[] certificateChain = {certificate, caCert};

    PEMWriter writer = new PEMWriter(new FileWriter("src/test/resources/crypto/sis-ca.pem"));
    writer.writeObject(certificate);
    writer.writeObject(caCert);
    writer.close();
  }

  private KeyStore loadKeyStore(String path, String password) throws Exception {
    KeyStore ks = KeyStore.getInstance("PKCS12", "SunJSSE");
    ks.load(new ClassPathResource(path).getInputStream(), password.toCharArray());
    return ks;
  }

  private KeyPair getKeyPairFromKeyStore(KeyStore store, String alias, String password) throws Exception {
    PrivateKey privateKey = (PrivateKey) store.getKey(alias, password.toCharArray());
    Certificate certificate = store.getCertificate(alias);
    PublicKey publicKey = certificate.getPublicKey();
    return new KeyPair(publicKey, privateKey);
  }

}
