package cz.silesnet.service;

import java.io.InputStream;

public interface DocumentService {

  InputStream invoicePdfStream(String uuid);
}
