package cz.silesnet.web.mvc;

import cz.silesnet.model.Node;
import cz.silesnet.model.Wireless;
import cz.silesnet.service.LabelManager;
import cz.silesnet.service.NodeManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validator for network nodes.
 *
 * @author Richard Sikora
 */
public class NodeWirelessEditValidator implements Validator {

  // ~ Instance fields
  // --------------------------------------------------------

  protected final Log log = LogFactory.getLog(getClass());

  @SuppressWarnings("unused")
  private NodeManager nmgr;

  @SuppressWarnings("unused")
  private LabelManager lmgr;

  // ~ Methods
  // ----------------------------------------------------------------

  // injected by Spring

  public void setLabelManager(LabelManager labelManager) {
    lmgr = labelManager;
  }

  // injected by Spring

  public void setNodeManager(NodeManager nodeManager) {
    nmgr = nodeManager;
  }

  public boolean supports(Class clazz) {
    return clazz.equals(Node.class) || clazz.equals(Wireless.class);
  }

  public void validate(Object object, Errors errors) {
    Node node = (Node) object;
    log.debug("Validating node " + node);
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name",
        "nodeEdit.error.name.blank-or-null", "Value required.");
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "MAC",
        "nodeEdit.error.mac.blank-or-null", "Value required.");
  }
}
