package cz.silesnet.web.mvc;

import cz.silesnet.model.Label;
import cz.silesnet.model.Node;
import cz.silesnet.model.Wireless;

import cz.silesnet.service.HistoryManager;
import cz.silesnet.service.LabelManager;
import cz.silesnet.service.NodeManager;

import cz.silesnet.utils.CustomLabelEditor;
import cz.silesnet.utils.MessagesUtils;
import cz.silesnet.utils.NavigationUtils;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.propertyeditors.CustomNumberEditor;

import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.WebUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller for editing nodes.
 *
 * @author Richard Sikora
 */
public class NodeWirelessEditController
    extends SimpleFormController {

    //~ Instance fields --------------------------------------------------------

    protected final Log    log  = LogFactory.getLog(getClass());
    private NodeManager    nmgr;
    private LabelManager   lmgr;
    private HistoryManager hmgr;

    //~ Methods ----------------------------------------------------------------

    // injected by Spring
    public void setHistoryManager(HistoryManager historyManager) {
        hmgr = historyManager;
    }

    // injected by Spring
    public void setLabelManager(LabelManager labelManager) {
        lmgr = labelManager;
    }

    // injected by Spring
    public void setNodeManager(NodeManager nodeManager) {
        nmgr = nodeManager;
    }

    public Node formBackingObject(HttpServletRequest request) {
        // determine request action
        String action = ServletRequestUtils.getStringParameter(request, "action",
                "actionUpdate");

        // get nodeId parameter
        long nodeId = ServletRequestUtils.getLongParameter(request, "nodeId", 0);
        log.debug("Want to " + action + " on node with id: " + nodeId);

        if (action.equals("actionAdd")) {
            // create new node with parent set
            Node node = new Wireless();
            node.setParentId(ServletRequestUtils.getLongParameter(request, "parentId",
                    0));

            return node;
        } else
            // return existing node
            return nmgr.getNodeById(nodeId);
    }

    // support binder with custor editors for some fields
    public void initBinder(HttpServletRequest request,
        ServletRequestDataBinder binder) {
        // Labels
        binder.registerCustomEditor(Label.class, new CustomLabelEditor(lmgr));
        // Integers
        binder.registerCustomEditor(Integer.class,
            new CustomNumberEditor(Integer.class, true));
    }

    public ModelAndView onSubmit(HttpServletRequest request,
        HttpServletResponse response, Object command, BindException errors)
        throws ServletException {
        Node node = (Node) command;

        if (request.getParameter("buttonSave") != null) {
            // save button pressed
            log.debug("Persisting node " + node);
            nmgr.updateNode(node);
            MessagesUtils.setCodedSuccessMessage(request,
                "editWireless.saveSuccess",
                new Object[] { node.getId(), node.getName() });
        }

        if (WebUtils.hasSubmitParameter(request, "buttonInsert")) {
            // insert button pressed
            log.debug("Inserting new node " + node);
            nmgr.insertNode(node);
            MessagesUtils.setCodedSuccessMessage(request,
                "editWireless.insertSuccess",
                new Object[] { node.getId(), node.getName() });
        }

        // figure out returnUrl after save or insert
        String returnUrl = NavigationUtils.getReturnUrl(request,
                "/net/wireless/view.html?view=viewDetail&nodeId="
                + node.getId());
        log.debug("Action successful, returning back to : " + returnUrl);

        // need redirect so we will go throught controller mechanism
        return new ModelAndView(new RedirectView(returnUrl));
    }

    // dispatch cancel and delete button before form validation
    public ModelAndView processFormSubmission(HttpServletRequest request,
        HttpServletResponse response, Object command, BindException errors)
        throws Exception {
        // get presented node
        Node node = (Node) command;

        if (request.getParameter("buttonCancel") != null) {
            // form cancelled get back without any changes

            // figure out returnUrl
            String returnUrl = NavigationUtils.getReturnUrl(request,
                    "/net/wireless/view.html?view=viewDetail&nodeId="
                    + node.getId());
            log.debug("Action canceled, returning back to : " + returnUrl
                + " without changes.");

            // need redirect so we will go throught controller mechanism
            return new ModelAndView(new RedirectView(returnUrl));
        }

        // delete given node
        if (request.getParameter("buttonDelete") != null) {
            // try to delete node here
            if (node.getSubNodesCount() > 0) {
                // node is not leaf can not delete
                log.debug(
                    "Trying to delete parent withou first deleting children. "
                    + node);
                MessagesUtils.setCodedSuccessMessage(request,
                    "editWireless.canNotDelete",
                    new Object[] { node.getId(), node.getName() });
            } else {
                log.debug("Deleting node: " + node);
                nmgr.deleteNode(node);
                MessagesUtils.setCodedSuccessMessage(request,
                    "editWireless.deleteSuccess",
                    new Object[] { node.getId(), node.getName() });
            }

            // figure out returnUrl after deletion
            // just pop from stack (koz there can be detail view of deleted
            // node)
            // so always return to siblings list
            String returnUrl = NavigationUtils.getReturnUrl(request, "");

            if (node.getParentId() != 0)
                returnUrl = request.getContextPath()
                    + "/net/wireless/view.html?view=viewSlaveList&parentId="
                    + node.getParentId();
            else
                returnUrl = request.getContextPath()
                    + "/net/wireless/view.html?view=viewMasterList";

            log.debug("Node deleted, returning back to: " + returnUrl);

            // need redirect so we will go throught controller mechanism
            return new ModelAndView(new RedirectView(returnUrl));
        }

        // if validation needed continue standard way
        return super.processFormSubmission(request, response, command, errors);
    }

    public Map referenceData(HttpServletRequest request, Object command,
        Errors errors)
        throws Exception {
        HashMap<String, Object> model = new HashMap<String, Object>();

        // support view with additional info

        // first let's generate labels lists

        // create N/A option label for not mandatory fields
        Label notAvailableLabel = new Label();
        notAvailableLabel.setId(Long.valueOf(0));
        notAvailableLabel.setName(MessagesUtils.getMessage(
                "app.label.notAvailable", request.getLocale()));

        // FIXME parent label values can not be hardcoded
        // get appropriage label lists
        ArrayList<Label> typeLabels = (ArrayList<Label>) lmgr.getSubLabels(lmgr
                    .getLabelById(Long.valueOf(10)));
        ArrayList<Label> domainLabels = (ArrayList<Label>) lmgr.getSubLabels(lmgr
                    .getLabelById(Long.valueOf(19)));
        ArrayList<Label> polarizationLabels = (ArrayList<Label>) lmgr
                    .getSubLabels(lmgr.getLabelById(Long.valueOf(30)));

            // sort domainLabel according 2 name 
            Collections.sort(domainLabels,
                (Comparator<? super Label>) (new BeanComparator("name")));

            // add N/A option to non mandatory fields
            domainLabels.add(0, notAvailableLabel);
            polarizationLabels.add(0, notAvailableLabel);

            // put it into model
            model.put("typeLabels", typeLabels);
            model.put("domainLabels", domainLabels);
            model.put("polarizationLabels", polarizationLabels);

            // support form with node history
            Node node = (Node) command;
            model.put("historyRecord", hmgr.getHistory(node));

            // set additional infos in model
            if (node.getParentId() != 0) {
                // we have slave node
                model.put("isMaster", false);
                model.put("isSlave", true);
                model.put("parentNode", nmgr.getNodeById(node.getParentId()));

                // kick from typeLabels list AP label
                // FIXME label id can not be so hardcoded here
                int indexOfAP = -1;

                for (Label label : typeLabels)
                    if (label.getId() == 11)
                        indexOfAP = typeLabels.indexOf(label);

                if (indexOfAP > -1)
                    typeLabels.remove(indexOfAP);
            } else {
                // we have master node
                model.put("isMaster", true);
                model.put("isSlave", false);

                // kick from typeLabels list SA label
                // FIXME label id can not be so hardcoded here
                int indexOfSA = -1;

                for (Label label : typeLabels)
                    if (label.getId() == 12)
                        indexOfSA = typeLabels.indexOf(label);

                if (indexOfSA > -1)
                    typeLabels.remove(indexOfSA);
            }

            // TODO actions should be done also via global const
            Boolean isUpdate        = Boolean.valueOf(false);
            Boolean isNew           = Boolean.valueOf(false);
            Boolean isDelete        = Boolean.valueOf(false);
            Boolean isDeleteConfirm = Boolean.valueOf(false);

            // get request action
            String action = ServletRequestUtils.getStringParameter(request, "action",
                    "actionUpdate");

            if (action.equals("actionUpdate"))
                isUpdate = Boolean.valueOf(true);
            else if (action.equals("actionAdd"))
                isNew = Boolean.valueOf(true);
            else if (action.equals("actionDelete"))
                isDelete = Boolean.valueOf(true);
            else if (action.equals("actionDeleteConfirm"))
                isDeleteConfirm = Boolean.valueOf(true);

            model.put("isUpdate", isUpdate);
            model.put("isNew", isNew);
            model.put("isDelete", isDelete);
            model.put("isDeleteConfirm", isDeleteConfirm);

            // include Javascripts
            model.put("scripts", new String[] { "safeSubmit.js" });

            return model;
        }
    }
