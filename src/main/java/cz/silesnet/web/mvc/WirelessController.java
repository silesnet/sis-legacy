package cz.silesnet.web.mvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import cz.silesnet.model.Label;
import cz.silesnet.model.Wireless;
import cz.silesnet.model.enums.Polarization;
import cz.silesnet.model.enums.WirelessEnum;
import cz.silesnet.model.enums.WirelessFrequency;
import cz.silesnet.service.LabelManager;
import cz.silesnet.utils.ControllerUtils;
import cz.silesnet.utils.CustomEnumEditor;
import cz.silesnet.utils.CustomLabelEditor;
import cz.silesnet.utils.MessagesUtils;
import cz.silesnet.utils.NavigationUtils;

/**
 * All in one controller for handling wireless related requests.
 * 
 * @author Richard Sikora
 */
public class WirelessController extends AbstractEntityController<Wireless> {

	protected final Log log = LogFactory.getLog(getClass());

	private LabelManager lmgr;

	// Specific action methods --------------------------------------

	@Override
	public ModelAndView showList(HttpServletRequest request,
			HttpServletResponse response) {
		log.debug("Showing wireless list.");
		Wireless filterObject = null;
		HashMap<String, Object> model = new HashMap<String, Object>();
		long parentId = ServletRequestUtils.getLongParameter(request,
				"parentId", 0);
		// supply model with commands list if needed
		if (!NavigationUtils.isTablePagination(request)) {
			// for master list, set filter on
			if (parentId == 0)
				filterObject = getFilterCommandObject(request);
			// filterObject can be null (slave list or no filtering data)
			if (filterObject == null)
				filterObject = newFilterCommandObject(request);
			// let's have only active nodes displayed
			filterObject.setActive(true);
			List<Wireless> commands = null;
			commands = persistenceManager.getByExample(filterObject);
			// reference command objects as session attribute
			request.getSession().setAttribute(getCommandsName(), commands);
			log.debug("Wireless list referenced as session attribute: "
					+ getCommandsName());
		}
		// reference common view data
		ControllerUtils
				.putAll(model, referenceViewData(request, getListView()));
		// clear navigation stack, because we are back on basic list
		NavigationUtils.clearNavigationStack(request);
		return new ModelAndView(getListView(), model);
	}

	public ModelAndView showDetail(HttpServletRequest request,
			HttpServletResponse response) {
		// reference wireless node
		Wireless node = formBackingObject(request);
		ServletRequestDataBinder binder = createBinder(node);
		// prepare model
		Map model = binder.getBindingResult().getModel();
		// refernece commands audit
		ControllerUtils.putAll(model, referenceCommandAudit(node));
		// common view data
		String detailView = "wireless/wirelessDetail";
		ControllerUtils.putAll(model, referenceViewData(request, detailView));
		return new ModelAndView(detailView, model);
	}

	@Override
	public ModelAndView delete(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		log.debug("Deleting Wireless.");
		// get wireless
		Wireless w = formBackingObject(request);
		// check for children, if not empty then fail
		if (w.getSubNodesCount() != 0) {
			MessagesUtils.setCodedFailureMessage(request,
					getFormMessage("deleteFailed"));
			return showForm(request, response);
		}
		// delete wireless
		persistenceManager.delete(w);
		// set success message
		MessagesUtils.setCodedSuccessMessage(request,
				getFormMessage("deleteSuccess"), w.getId());
		// force list refresh in case of showList()
		NavigationUtils.setForceListRefresh(request, true);
		return goBack(request, response);
	}

	@Override
	protected void validate(Wireless command, BindingResult bindingResult) {
		ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "name",
				"editForm.error.blank-or-null", "Value required.");
		ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "frequency",
				"editForm.error.blank-or-null", "Value required.");
	}

	public ModelAndView activate(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		log.debug("Activating Wireless.");
		// get wirless
		Wireless w = formBackingObject(request);
		// activate and persist change
		w.setActive(true);
		persistenceManager.update(w);
		// set success message
		MessagesUtils.setCodedSuccessMessage(request,
				getFormMessage("activateSuccess"), w.getId());
		return goBack(request, response);
	}

	public ModelAndView deactivate(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		log.debug("Deactivating Wireless.");
		// get wirless
		Wireless w = formBackingObject(request);
		// activate and persist change
		w.setActive(false);
		persistenceManager.update(w);
		// set success message
		MessagesUtils.setCodedSuccessMessage(request,
				getFormMessage("deactivateSuccess"), w.getId());
		return goBack(request, response);
	}

	public ModelAndView updateParent(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		log.debug("Updating master of slave wireless node.");
		long newParentId = ServletRequestUtils.getLongParameter(request,
				"newParentId", 0);
		if (newParentId == 0) {
			throw new IllegalStateException("Parent Wireless id not provided.");
		}

		// get parent
		Wireless newParent = persistenceManager.get(newParentId);

		// get slave
		Wireless w = formBackingObject(request);

		// update slaves parent
		w.setParentId(newParent.getId());
		persistenceManager.update(w);

		return showDetail(request, response);
	}

	// Overriden methods --------------------------------------------

	public ModelAndView showForm(HttpServletRequest request,
			HttpServletResponse response) {
		return super.showForm(request, response);
	}

	private Map referenceWirelessParent(HttpServletRequest request) {
		Wireless wParent = null;
		// first check request for special parameter
		long parentId = ServletRequestUtils.getLongParameter(request,
				"parentId", 0);
		if (parentId != 0) {
			wParent = persistenceManager.get(parentId);
		}
		else {
			// special parameter not present, try by backing object
			Wireless w = formBackingObject(request);
			// check for wireless parent
			if (w.getParentId() != null && w.getParentId() > 0)
				wParent = persistenceManager.get(w.getParentId());
		}
		return wParent != null ? referenceCommand(wParent, "wirelessParent")
				: null;
	}

	@SuppressWarnings("unchecked")
	protected Map referenceViewData(HttpServletRequest request, String viewLong) {
		Map model = super.referenceViewData(request, viewLong);
		// reference wireless parent if needed
		ControllerUtils.putAll(model, referenceWirelessParent(request));
		// reference domain labels
		// FIXME remove hardcoded constant
		ArrayList<Label> domains = (ArrayList<Label>) lmgr.getSubLabels(lmgr
				.getLabelById(19L));
		// sort domainLabel according 2 name
		Collections.sort(domains,
				(Comparator<? super Label>) (new BeanComparator("name")));
		// put it into model
		model.put("domains", domains);
		return model;
	}

	@Override
	protected Map referenceFormData(HttpServletRequest request, Wireless command) {
		HashMap<String, Object> model = new HashMap<String, Object>();
		model.put("wirelessType", EnumSet.allOf(WirelessEnum.class));
		model.put("wirelessFrequency", EnumSet.allOf(WirelessFrequency.class));
		model.put("wirelessPolarization", EnumSet.allOf(Polarization.class));

		// reference master nodes if wireless is slave
		if (command.getParentId() != null && command.getParentId() != 0) {
			Wireless filterObject = new Wireless();
			filterObject.setType(null);
			filterObject.setFrequency(null);
			filterObject.setPolarization(null);
			filterObject.setParentId(0L);
			model.put("mastersList", persistenceManager
					.getByExample(filterObject));
		}
		return model;
	}

	protected void initBinder(ServletRequestDataBinder binder) {
		super.initBinder(binder);
		binder.registerCustomEditor(WirelessEnum.class,
				new CustomEnumEditor<WirelessEnum>(WirelessEnum.AP));
		binder
				.registerCustomEditor(WirelessFrequency.class,
						new CustomEnumEditor<WirelessFrequency>(
								WirelessFrequency.F2412));
		binder.registerCustomEditor(Polarization.class,
				new CustomEnumEditor<Polarization>(Polarization.HORIZONTAL));
		binder.registerCustomEditor(Label.class, new CustomLabelEditor(lmgr));
	}

	public Wireless newCommandObject(HttpServletRequest request) {
		Wireless node = new Wireless();
		// set parent from request, default to 0
		node.setParentId(ServletRequestUtils.getLongParameter(request,
				"parentId", 0));
		return node;
	}

	public Wireless newFilterCommandObject(HttpServletRequest request) {
		Wireless node = super.newFilterCommandObject(request);
		node.setType(null);
		node.setFrequency(null);
		node.setPolarization(null);
		return node;
	}

	public void setLabelManager(LabelManager labelManager) {
		lmgr = labelManager;
	}

}
