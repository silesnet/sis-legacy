package cz.silesnet.web.mvc;

import cz.silesnet.model.Historic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.util.WebUtils;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Abstract controller for entity objects CRUD operations.
 * 
 * @author Richard Sikora
 */
public abstract class AbstractCRUDController extends MultiActionController {

	// ~ Instance fields
	// --------------------------------------------------------

	protected final Log log = LogFactory.getLog(getClass());

	private String fFormView;

	private String fCommandName = "command";

	private String fCommandNewName = "isNew";

	// ~ Methods
	// ----------------------------------------------------------------

	public void setCommandName(String commandName) {
		fCommandName = commandName;
	}

	public String getCommandName() {
		return fCommandName;
	}

	public void setCommandNewName(String commandNewName) {
		fCommandNewName = commandNewName;
	}

	public String getCommandNewName() {
		return fCommandNewName;
	}

	public void setFormView(String formView) {
		fFormView = formView;
	}

	public String getFormView() {
		return fFormView;
	}

	public final ModelAndView showErrorForm(HttpServletRequest request,
			HttpServletResponse response,
			ServletRequestBindingException bindingException) throws Exception {
		log.debug("Showing error form.");
		// retrieve BindException
		BindException errors = (BindException) bindingException.getRootCause();

		// prepare model
		Map model = prepareModel(request, errors);
		return new ModelAndView(getFormView(), model);
	}

	public ModelAndView showForm(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		log.debug("Showing form.");
		// get fresh command object
		Object command = formBackingObject(request);

		// put binder into request, it contains command object
		// so no need to add it explicitly
		ServletRequestDataBinder binder = createBinder(command);

		// prepare model
		Map model = prepareModel(request, binder.getBindingResult());
		return new ModelAndView(getFormView(), model);
	}

	public abstract ModelAndView cancel(HttpServletRequest request,
			HttpServletResponse response);

	public abstract ModelAndView delete(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public abstract ModelAndView insert(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public abstract ModelAndView showList(HttpServletRequest request,
			HttpServletResponse response);

	public abstract ModelAndView update(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	protected abstract Boolean isFormBackingObjectNew(HttpServletRequest request);

	protected ModelAndView closeForm(HttpServletRequest request,
			HttpServletResponse response) {
		log.debug("Closing form.");

		return showList(request, response);
	}

	protected abstract Object formBackingObject(HttpServletRequest request)
			throws Exception;

	protected abstract Map referenceData(HttpServletRequest request);

	protected Object bind(HttpServletRequest request) throws Exception {
		return bind(request, false);
	}

	protected Object bindAndValidate(HttpServletRequest request)
			throws Exception {
		return bind(request, true);
	}

	protected final ServletRequestDataBinder createBinder(Object command) {
		// have new binder on command object
		ServletRequestDataBinder binder = new ServletRequestDataBinder(command,
				getCommandName());
		// init binder with custom editors
		initBinder(binder);
		return binder;
	}

	protected void initBinder(ServletRequestDataBinder binder) {
		log.debug("Default initing binder with custom editors (none).");
	}

	protected Map referenceHistory(Historic historic) {
		if (log.isDebugEnabled())
			log.debug("Referencing command object's history for: " + historic);
		return null;
	}

	protected void validate(Object command, BindingResult bindingResult) {
		log.debug("Default validating command object (none).");
	}

	private final Object bind(HttpServletRequest request, Boolean validate)
			throws Exception {
		log.debug("Binding request parameters to command object.");
		// retrieve command object
		Object command = formBackingObject(request);

		// have new binder for command object and bind it
		ServletRequestDataBinder binder = createBinder(command);
		binder.bind(request);
		// validate if needed
		if (validate)
			validate(command, binder.getBindingResult());
		// raise ServletRequestBindingException on errors
		binder.closeNoCatch();
		// return command
		return command;
	}

	@SuppressWarnings("unchecked")
	private final Map prepareModel(HttpServletRequest request,
			BindingResult bindingResult) {
		// create model from errors that include command object
		Map model = bindingResult.getModel();
		// set commnadNew flag
		model.put(getCommandNewName(), isFormBackingObjectNew(request));
		// add other reference data
		Map referenceData = referenceData(request);
		if (referenceData != null)
			model.putAll(referenceData);

		// add history of command object if needed
		Object command = model.get(getCommandName());
		if (command instanceof Historic) {
			Historic historic = (Historic) command;
			Map commandHistory = referenceHistory(historic);
			if (commandHistory != null)
				model.putAll(commandHistory);
		}

		// add i18n fields labels
		Map i18nFields = referenceI18nFields(command);
		if (i18nFields != null)
			model.putAll(i18nFields);
		return model;
	}

	// add i18n field label to model
	protected Map referenceI18nFields(Object o) {
		return null;
	}

	protected static boolean isTablePagination(HttpServletRequest request) {
		return WebUtils.getParametersStartingWith(request, "d-").size() == 0 ? false
				: true;
	}

}