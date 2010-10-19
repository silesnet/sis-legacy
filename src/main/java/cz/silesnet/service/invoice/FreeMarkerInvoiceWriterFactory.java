package cz.silesnet.service.invoice;

import cz.silesnet.model.Bill;
import org.springframework.beans.factory.InitializingBean;

/**
 * User: der3k
 * Date: 19.10.2010
 * Time: 20:56:39
 */
public class FreeMarkerInvoiceWriterFactory implements InvoiceWriterFactory, InitializingBean {

  public InvoiceWriter instanceOf(final Bill invoice, final InvoiceFormat format) {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public void afterPropertiesSet() throws Exception {
    //To change body of implemented methods use File | Settings | File Templates.
  }
}
