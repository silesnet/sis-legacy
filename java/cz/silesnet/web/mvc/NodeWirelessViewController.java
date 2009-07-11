package cz.silesnet.web.mvc;

import cz.silesnet.model.Label;
import cz.silesnet.model.Node;
import cz.silesnet.model.Wireless;
import cz.silesnet.model.enums.WirelessEnum;

import cz.silesnet.service.HistoryManager;
import cz.silesnet.service.LabelManager;
import cz.silesnet.service.NodeManager;

import cz.silesnet.utils.FilterUtils;
import cz.silesnet.utils.MessagesUtils;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.util.Assert;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Network node viewing controller.
 * 
 * @author Richard Sikora
 */
public class NodeWirelessViewController extends MultiActionController {

	// ~ Instance fields
	// --------------------------------------------------------

	protected final Log log = LogFactory.getLog(getClass());

	private NodeManager nmgr;

	private LabelManager lmgr;

	private HistoryManager hmgr;

	// ~ Methods
	// ----------------------------------------------------------------

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

	public ModelAndView viewDetail(HttpServletRequest request,
			HttpServletResponse response) {
		HashMap<String, Object> model = new HashMap<String, Object>();

		long nodeId = ServletRequestUtils
				.getLongParameter(request, "nodeId", 0);
		log.debug("View detail of node whit id: " + nodeId);
		Assert.isTrue(nodeId != 0, "No nodeId given, can not display details!");

		// retrieve node and put it into model
		Node node = nmgr.getNodeById(nodeId);
		model.put("node", node);

		// retrieve node's history and support view with it
		model.put("historyRecord", hmgr.getHistory(node));

		// support view with additional info
		if (node.getParentId() != 0) {
			// we have slave node
			model.put("isMaster", false);
			model.put("isSlave", true);
			model.put("parentNode", nmgr.getNodeById(node.getParentId()));

			// FIXME this can not be so hardcoded here, use application global
			// const
			// which should be automatically set on application deploy
			if (node instanceof Wireless
					&& WirelessEnum.SA.equals(((Wireless) node).getType()))
				model.put("isSA", true);
			else
				model.put("isSA", false);
		}
		else {
			// we have master node
			model.put("isMaster", true);
			model.put("isSlave", false);
			model.put("isSA", false);
		}

		return new ModelAndView("wireless/detailWireless", model);
	}

	public ModelAndView viewMasterList(HttpServletRequest request,
			HttpServletResponse response) {
		HashMap<String, Object> model = new HashMap<String, Object>();

		log.debug("View master wireless list.");

		// get DomainLabelId if present
		String domainLabelIdString = FilterUtils.getFilterParameter(request,
				"DomainLabelId");

		// support view with nodes
		if (!"".equals(domainLabelIdString)) {
			// get filtered nodes by domainLabelId
			log.debug("Getting filtered nodes by DomainLabelId: "
					+ domainLabelIdString);
			model.put("nodes", nmgr.getLeveOfNodesByDomain(0, Long
					.valueOf(domainLabelIdString)));
		}
		else {
			// get unfiltered nodes
			log.debug("Getting unfiltered nodes.");
			model.put("nodes", nmgr.getLevelOfNodes(0));
		}

		// support view wiht additional info
		model.put("isMaster", true);
		model.put("isSlave", false);

		// support view with domain labels list for filtering

		// create empty option label if no filtering needed
		// id has to be 0, ViewFilterFilter utilizes this value
		// to remove filterAttribute from session sisFilterMap
		Label anyDomainLabel = new Label();
		anyDomainLabel.setId(Long.valueOf(0));

		anyDomainLabel.setName(MessagesUtils.getMessage(
				"listWireless.label.filter.anyDomain", request.getLocale()));

		// FIXME parent label values can not be hardcoded
		ArrayList<Label> domainLabels = (ArrayList<Label>) lmgr
				.getSubLabels(lmgr.getLabelById(Long.valueOf(19)));

		// sort domainLabel according 2 name
		Collections.sort(domainLabels,
				(Comparator<? super Label>) (new BeanComparator("name")));

		domainLabels.add(0, anyDomainLabel);
		model.put("domainLabels", domainLabels);

		return new ModelAndView("wireless/listWireless", model);
	}

	public ModelAndView viewSlaveList(HttpServletRequest request,
			HttpServletResponse response) {
		HashMap<String, Object> model = new HashMap<String, Object>();

		log.debug("View slave wireless list.");

		// get parentId for list
		long parentId = ServletRequestUtils.getLongParameter(request,
				"parentId", 0);
		Assert.isTrue(parentId != 0,
				"No parentId given, can not display any slaves!");

		// support view with slave nodes list (parentId=0)
		model.put("nodes", nmgr.getLevelOfNodes(parentId));

		// support view wiht additional info
		model.put("isMaster", false);
		model.put("isSlave", true);
		model.put("parentNode", nmgr.getNodeById(parentId));

		return new ModelAndView("wireless/listWireless", model);
	}
}