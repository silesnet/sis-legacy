package cz.silesnet.service.impl;

import cz.silesnet.dao.BillDAO;
import cz.silesnet.model.Bill;
import cz.silesnet.service.DocumentService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.util.FakeHttpServletRequest;
import org.directwebremoting.util.FakeHttpServletRequestFactory;
import org.directwebremoting.util.FakeHttpServletResponse;
import org.directwebremoting.util.SwallowingHttpServletResponse;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;

public class DefaultDocumentService implements DocumentService, ServletContextAware {
  protected final Log log = LogFactory.getLog(getClass());

  private BillDAO billDAO;
  private ServletContext servletContext;
  private ViewResolver viewResolver;

  public void setBillDAO(BillDAO billDAO) {
    this.billDAO = billDAO;
  }

  public void setViewResolver(ViewResolver viewResolver) {
    this.viewResolver = viewResolver;
  }

  public InputStream invoicePdfStream(String uuid) {
    Bill bill = billDAO.get(uuid);
    long billId = bill.getId();
    log.info("getting html of " + billId);
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    StringWriter writer = new StringWriter();
    StringBuffer buffer = writer.getBuffer();
    SwallowingHttpServletResponse swallowingResponse = new SwallowingHttpServletResponse(response, writer, "UTF-8");
    try {
//      View view = viewResolver.resolveViewName("billing/printBillTxt_cs", Locale.US);
//      view.render(new HashMap<String, Object>(), request, swallowingResponse);
      RequestDispatcher dispatcher = servletContext.getRequestDispatcher("/WEB-INF/jsp/billing/printBillTxt_cs.jsp");
      dispatcher.include(request, swallowingResponse);
      log.info(buffer.toString());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return new ByteArrayInputStream(uuid.getBytes());
  }

  public void setServletContext(ServletContext servletContext) {
    this.servletContext = servletContext;
  }
}
