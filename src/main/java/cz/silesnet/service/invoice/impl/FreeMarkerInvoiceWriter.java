package cz.silesnet.service.invoice.impl;

import cz.silesnet.model.Bill;
import cz.silesnet.service.invoice.Invoice;
import cz.silesnet.service.invoice.InvoiceWriter;
import cz.silesnet.service.invoice.impl.AbstractInvoiceWriter;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * User: der3k
 * Date: 19.10.2010
 * Time: 20:36:24
 */
public class FreeMarkerInvoiceWriter implements InvoiceWriter {

  private Invoice invoice;
  private Template template;
  private static final String INVOICE_MODEL_NAME = "invoice";

  protected FreeMarkerInvoiceWriter(Invoice invoice, Template template) {
    this.invoice = invoice;
    this.template = template;
  }

  public void write(final PrintWriter writer) {
    Map<String, Object> model = new HashMap<String, Object>();
    model.put(INVOICE_MODEL_NAME, invoice);
    try {
      template.process(model, writer);
    } catch (TemplateException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
