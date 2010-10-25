package cz.silesnet.service.invoice.impl;

import cz.silesnet.service.invoice.Invoice;
import cz.silesnet.service.invoice.InvoiceWriter;
import cz.silesnet.service.invoice.InvoiceWriterFactory;
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
  private static final String INVOICE_TEMPLATE_I18N_SEPARATOR = "_";
  private static final String INVOICE_TEMPLATE_SUFFIX = ".flt";

  public void setConfiguration(final Configuration configuration) {
    this.configuration = configuration;
  }

  public InvoiceWriter instanceOf(final Invoice invoice) {
    Template template;
    try {
      template = configuration.getTemplate(resolveTemplateName(invoice));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return new FreeMarkerInvoiceWriter(invoice, template);
  }

  private String resolveTemplateName(final Invoice invoice) {
    StringBuilder template = new StringBuilder(INVOICE_TEMPLATE_PREFIX);
    template.append(invoice.getShortFormatInLowerCase());
    template.append(INVOICE_TEMPLATE_I18N_SEPARATOR)
        .append(invoice.getCountry().getLocale().toString());
    template.append(INVOICE_TEMPLATE_SUFFIX);
    return template.toString();
  }

  public void afterPropertiesSet() throws Exception {
    Assert.notNull(configuration);
  }
}
