package cz.silesnet.web.mvc;

import cz.silesnet.model.Customer;
import cz.silesnet.model.Label;
import cz.silesnet.model.PrepareMixture;
import cz.silesnet.service.CustomerManager;
import cz.silesnet.service.HistoryManager;
import cz.silesnet.service.LabelManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static org.mockito.Mockito.*;

/**
 * User: der3k
 * Date: 2.10.2010
 * Time: 13:13:29
 */
public class CustomerControllerTest {

  protected static final Log log = LogFactory.getLog(CustomerControllerTest.class);

  @Test
  public void form() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();

    Customer customer = PrepareMixture.getCustomer();
    customer.setId(1L);
    Long id = customer.getId();
    CustomerController controller = new CustomerController();

    CustomerManager cMgr = mock(CustomerManager.class);
    when(cMgr.get(id)).thenReturn(customer);
    controller.setCustomerManager(cMgr);

    LabelManager lMgr = mock(LabelManager.class);
    Label label = new Label();
    label.setId(301L);
    label.setParentId(300L);
    label.setName("Shire&Responsible");
    ArrayList<Label> labels = new ArrayList<Label>();
    labels.add(label);
    when(lMgr.getSubLabels(null)).thenReturn(labels);
    controller.setLabelManager(lMgr);

    HistoryManager hMgr = mock(HistoryManager.class);
    controller.setHistoryManager(hMgr);

    request.setParameter("customerId", "" + id);
    ModelAndView result = controller.showForm(request, null);
    System.out.println(result);
    assert true;
  }

  @Test
  public void billingMonth() throws Exception {
    String format = "%02d";
    Assert.assertEquals(String.format(format, 1), "01");
    Assert.assertEquals(String.format(format, 11), "11");
  }
}
