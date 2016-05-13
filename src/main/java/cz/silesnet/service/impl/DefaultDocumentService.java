package cz.silesnet.service.impl;

import cz.silesnet.service.DocumentService;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class DefaultDocumentService implements DocumentService {

  public InputStream invoicePdfStream(String uuid) {
    return new ByteArrayInputStream(uuid.getBytes());
  }
}
