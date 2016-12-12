package cz.silesnet.web.mvc;

import cz.silesnet.model.Command;
import cz.silesnet.model.Customer;
import cz.silesnet.model.Label;
import cz.silesnet.model.PrepareMixture;
import cz.silesnet.service.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

/**
 * User: der3k
 * Date: 2.10.2010
 * Time: 13:13:29
 */
public class CustomerControllerTest {

  protected static final Log log = LogFactory.getLog(CustomerControllerTest.class);

  @Test
  public void testWaitForCommandToTimeout() throws Exception {
    final CommandManager commandManager = commandManager("completed", 10);
    final long commandId = commandManager.submit(newCommand());
    final CommandExecution execution = new CommandExecution(commandManager, commandId);

    execution.waitUntilFinished(10L);
    assertFalse(execution.hasFinished());
    assertFalse(execution.hasCompleted());
    assertFalse(execution.hasFailed());
    assertEquals(execution.currentStatus(), "started");
  }

  @Test
  public void testWaitForCommandToComplete() throws Exception {
    final CommandManager commandManager = commandManager("completed", 5);
    final long commandId = commandManager.submit(newCommand());
    final CommandExecution execution = new CommandExecution(commandManager, commandId);

    execution.waitUntilFinished(1000L);
    assertTrue(execution.hasFinished());
    assertTrue(execution.hasCompleted());
    assertFalse(execution.hasFailed());
    assertEquals(execution.currentStatus(), "completed");
  }

  @Test
  public void testWaitForCommandToFail() throws Exception {
    final CommandManager commandManager = commandManager("failed", 5);
    final long commandId = commandManager.submit(newCommand());
    final CommandExecution execution = new CommandExecution(commandManager, commandId);

    execution.waitUntilFinished(1000L);
    assertTrue(execution.hasFinished());
    assertTrue(execution.hasFailed());
    assertFalse(execution.hasCompleted());
    assertEquals(execution.currentStatus(), "failed");
  }

  private CommandManager commandManager(final String finalStatus, final long statusCallCounterParam) {
    return new CommandManager() {
        private final long commandId = 1;
        private Command command;
        private long statusCallCounter = statusCallCounterParam;

        @Override
        public long submit(Command command) {
          this.command = command;
          this.command.setStatus("started");
          return commandId;
        }

        @Override
        public String status(long commandId) {
          if (--statusCallCounter == 0) {
            this.command.setStatus(finalStatus);
          }
          return command != null ? command.getStatus() : null;
        }
      };
  }

  private Command newCommand() {
    final Command command = new Command();
    command.setCommand("disconnect");
    command.setEntity("customers");
    command.setEntityId(1);
    return command;
  }

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
    controller.setEventManager(mock(EventManager.class));

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
