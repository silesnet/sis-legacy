package cz.silesnet.service.invoice;

import cz.silesnet.model.Bill;
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
public class FreeMarkerInvoiceWriter extends AbstractInvoiceWriter {

  protected Template template;
  private static final String INVOICE_MODEL_NAME = "invoice";

  protected FreeMarkerInvoiceWriter(Bill bill, Template template) {
    super(bill);
    this.template = template;
  }

  @Override
  public void writeTo(final PrintWriter writer) {
    Map<String, Object> model = new HashMap<String, Object>();
    model.put(INVOICE_MODEL_NAME, bill);
    try {
      template.process(model, writer);
    } catch (TemplateException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
