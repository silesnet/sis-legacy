package cz.silesnet.service.impl;

import cz.silesnet.service.DocumentService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class DefaultDocumentService implements DocumentService {
  protected final Log log = LogFactory.getLog(getClass());

  public InputStream invoicePdfStream(String uuid) {
    log.info("getting html of " + uuid);

    return new ByteArrayInputStream(uuid.getBytes());
  }
}
