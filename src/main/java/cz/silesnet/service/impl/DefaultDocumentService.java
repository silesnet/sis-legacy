package cz.silesnet.service.impl;

import cz.silesnet.service.DocumentService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.io.*;

public class DefaultDocumentService implements DocumentService, InitializingBean {
  protected final Log LOG = LogFactory.getLog(getClass());

  private File tmpFolder;
  private File pdfCommand;

  public void setTmpFolder(File tmpFolder) {
    this.tmpFolder = tmpFolder;
  }

  public void setPdfCommand(File pdfCommand) {
    this.pdfCommand = pdfCommand;
  }

  public InputStream invoicePdfStream(final String uuid) {
    LOG.info("getting html of " + uuid);
    final File pdfFile = pdfFile(uuid);
    if (!pdfFile.exists() || pdfFile.length() == 0) {
      if (!tmpFolder.exists()) {
        tmpFolder.mkdirs();
      }
      LOG.info("creating invoice PDF '" + createPdfCommand(uuid, pdfFile) + "'");
      try {
        final Process process = new ProcessBuilder(this.pdfCommand.getPath(), uuid, pdfFile.getPath())
            .redirectErrorStream(true)
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .start();
        final int error = process.waitFor();
        if (error != 0) {
          throw new RuntimeException("error code: " + error);
        }
        Assert.isTrue(pdfFile.exists(), pdfFile.getAbsolutePath() + " exists");
        Assert.isTrue(pdfFile.length() > 0, pdfFile.getAbsolutePath() + " has non zero size");
      } catch (Exception e) {
        LOG.error("PDF creation command failed for '" + uuid + "'", e);
        throw new RuntimeException(e);
      }
    }
    final FileInputStream pdfInput;
    try {
      pdfInput = new FileInputStream(pdfFile);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
    return new BufferedInputStream(pdfInput);
  }

  protected String createPdfCommand(String uuid, File pdfFile) {
    return pdfCommand.getPath() + " " + uuid + " \"" + pdfFile.getPath() + "\"";
  }

  protected File pdfFile(String uuid) {
    return new File(tmpFolder, uuid + ".pdf");
  }

  public void afterPropertiesSet() throws Exception {
    Assert.notNull(tmpFolder);
    Assert.notNull(pdfCommand);
//    Assert.isTrue(pdfCommand.exists());
  }
}
