package cz.silesnet.service.impl;

import org.testng.annotations.Test;

import java.io.File;

import static org.testng.Assert.*;

public class DefaultDocumentServiceTest {
  @Test
  public void testCommandIsConfigured() throws Exception {
    DefaultDocumentService documentService = new DefaultDocumentService();
    documentService.setPdfCommand(new File("bin/get-invoice-pdf"));
    documentService.setTmpFolder(new File("tmp/pdf"));
    assertEquals(documentService.pdfFile("abcd"), new File("tmp/pdf/abcd.pdf"));
    assertEquals(documentService.createPdfCommand("1234", new File("abc.pdf")).replaceAll("\\\\", "/"),
        "bin/get-invoice-pdf 1234 \"abc.pdf\"");

  }
}