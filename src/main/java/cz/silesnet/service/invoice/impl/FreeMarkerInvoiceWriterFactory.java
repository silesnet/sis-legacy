package cz.silesnet.service.invoice.impl;

import cz.silesnet.model.Bill;
import cz.silesnet.service.invoice.InvoiceFormat;
import cz.silesnet.service.invoice.InvoiceWriter;
import cz.silesnet.service.invoice.InvoiceWriterFactory;
import cz.silesnet.service.invoice.impl.FreeMarkerInvoiceWriter;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.io.IOException;

/**
 * User: der3k
 * Date: 19.10.2010
 * Time: 20:56:39
 */
public class FreeMarkerInvoiceWriterFactory implements InvoiceWriterFactory, InitializingBean {

  private Configuration configuration;
  private static final String INVOICE_TEMPLATE_PREFIX = "invoice-";
  private static final String INVOICE_TEMPLATE_SUFFIX = ".flt";

  public void setConfiguration(final Configuration configuration) {
    this.configuration = configuration;
  }

  public InvoiceWriter instanceOf(final Bill invoice, final InvoiceFormat format) {
    Template template;
    try {
      template = configuration.getTemplate(resolveTemplateName(format));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return new FreeMarkerInvoiceWriter(invoice, template);
  }

  private String resolveTemplateName(final InvoiceFormat format) {
    return INVOICE_TEMPLATE_PREFIX + format.shortName().toLowerCase() + INVOICE_TEMPLATE_SUFFIX;
  }

  public void afterPropertiesSet() throws Exception {
    Assert.notNull(configuration);
  }
}
