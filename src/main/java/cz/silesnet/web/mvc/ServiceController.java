package cz.silesnet.web.mvc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.annotation.Secured;
import org.apache.commons.beanutils.BeanComparator;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import cz.silesnet.model.Customer;
import cz.silesnet.model.Label;
import cz.silesnet.model.Period;
import cz.silesnet.model.Service;
import cz.silesnet.model.enums.Country;
import cz.silesnet.model.enums.Frequency;
import cz.silesnet.service.CustomerManager;
import cz.silesnet.service.LabelManager;
import cz.silesnet.utils.CustomEnumEditor;
import cz.silesnet.utils.MessagesUtils;
import cz.silesnet.utils.NavigationUtils;

/**
 * CRUD Controller for service entity objects.
 * 
 * @author Richard Sikora
 */
public class ServiceController extends AbstractCRUDController {

	// ~ Instance fields
	// --------------------------------------------------------

	private LabelManager lmgr;

	private CustomerManager cmgr;

	// ~ Methods
	// ----------------------------------------------------------------

	// injected by Spring
	public void setLabelManager(LabelManager labelManager) {
		lmgr = labelManager;
	}

	public void setCustomerManager(CustomerManager customerManager) {
		cmgr = customerManager;
	}

	@Secured( { "ROLE_ACCOUNTING" })
	public ModelAndView showForm(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		return super.showForm(request, response);
	}

	public ModelAndView cancel(HttpServletRequest request,
			HttpServletResponse response) {
		log.debug("Canceling, going back.");

		return closeForm(request, response);
	}

	@Secured( { "ROLE_ACCOUNTING" })
	public ModelAndView delete(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		log.debug("Deleting service.");

		// get validated command object
		Service command = (Service) formBackingObject(request);

		if (log.isDebugEnabled())
			log.debug("Obtained object: " + command);
		// delete service
		cmgr.deleteService(command);
		// display success message
		MessagesUtils.setCodedSuccessMessage(request,
				"editService.deleteSuccess", new Object[] { command.getId(),
						command.getName() });

		// close form
		return closeForm(request, response);
	}

	@Secured( { "ROLE_ACCOUNTING" })
	public ModelAndView insert(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		log.debug("Inserting service.");

		// get validated command object
		Service command = (Service) bindAndValidate(request);

		if (log.isDebugEnabled())
			log.debug("Obtained object: " + command);

		// persist new service
		cmgr.insertService(command);
		// display success message
		MessagesUtils.setCodedSuccessMessage(request,
				"editService.insertSuccess", new Object[] { command.getId(),
						command.getName() });

		// close form
		return closeForm(request, response);
	}

	public ModelAndView showList(HttpServletRequest request,
			HttpServletResponse response) {
		HashMap<String, Object> model = new HashMap<String, Object>();
		log.info("Listing all serivices.");
		// model.put("servicesList", smgr.getAllOrphans());

		return new ModelAndView("service/listServices", model);
	}

	@Secured( { "ROLE_ACCOUNTING" })
	public ModelAndView update(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		log.debug("Updating service.");

		// get validated command object
		Service command = (Service) bindAndValidate(request);

		if (log.isDebugEnabled())
			log.debug("Obtained object: " + command);

		// persist change
		cmgr.updateService(command);
		// set success message
		MessagesUtils.setCodedSuccessMessage(request,
				"editService.updateSuccess", new Object[] { command.getId(),
						command.getName() });

		// close form
		return closeForm(request, response);
	}

	protected Boolean isFormBackingObjectNew(HttpServletRequest request) {
		return (ServletRequestUtils.getLongParameter(request, "serviceId", 0) == 0) ? true
				: false;
	}

	protected ModelAndView closeForm(HttpServletRequest request,
			HttpServletResponse response) {
		String returnUrl = NavigationUtils.getReturnUrl(request,
				"/customer/view.html?action=showList");

		// need redirect so we will go throught controller mechanism
		return new ModelAndView(new RedirectView(returnUrl));
	}

	protected Object formBackingObject(HttpServletRequest request)
			throws Exception {
		Service s = null;
		if (isFormBackingObjectNew(request)) {
			s = new Service();
			// set customerId
			Long customerId = ServletRequestUtils.getLongParameter(request,
					"customerId", 0);
			s.setCustomerId(customerId);
			// set period starting from now
			s.setPeriod(new Period(new Date(), null));
			// set frequency to monthly or one time
			if ("oneTime".equals(ServletRequestUtils.getStringParameter(
					request, "formType", ""))) {
				s.setFrequency(Frequency.ONE_TIME);
			}
			else {
				s.setFrequency(Frequency.MONTHLY);
			}
		}
		else
			s = cmgr.getService(ServletRequestUtils.getRequiredLongParameter(
					request, "serviceId"));
		return s;
	}

	protected void initBinder(ServletRequestDataBinder binder) {
		log.debug("Registering custom editors...");
		binder.registerCustomEditor(Integer.class, new CustomNumberEditor(
				Integer.class, true));
		// Date
		binder.registerCustomEditor(Date.class, new CustomDateEditor(
				new SimpleDateFormat("dd.MM.yyyy"), true));
		// Frequency
		binder.registerCustomEditor(Frequency.class,
				new CustomEnumEditor<Frequency>(Frequency.MONTHLY));
	}

	protected Map<String, Object> referenceData(HttpServletRequest request) {
		HashMap<String, Object> model = new HashMap<String, Object>();
		// support view with types labels
		ArrayList<Label> serviceNames;
		if (isOneTimeService(request)) {
			// one time service names
			// FIXME can not be so hardcoded here
			serviceNames = (ArrayList<Label>) lmgr.getSubLabels(lmgr
					.getLabelById(Long.valueOf(201)));
			// one time Frequency enums
			model.put("serviceFrequency", EnumSet.of(Frequency.ONE_TIME));
		}
		else {
			// regular service names
			// FIXME can not be so hardcoded here
			serviceNames = (ArrayList<Label>) lmgr.getSubLabels(lmgr
					.getLabelById(Long.valueOf(200)));
			// regular service Frequency enums
			model.put("serviceFrequency", EnumSet.of(Frequency.MONTHLY,
					Frequency.ANNUAL));
		}
		// sort service names by name
		Collections.sort(serviceNames,
				(Comparator<? super Label>) (new BeanComparator("name")));
		model.put("serviceNames", serviceNames);

		// include Javascripts for safe submitting
		model.put("scripts", new String[] { "safeSubmit.js", "calendar.js",
				"comboBox.js" });

		return model;
	}

	private boolean isOneTimeService(HttpServletRequest request) {
		String formType = ServletRequestUtils.getStringParameter(request,
				"formType", "");
		if ("oneTime".equals(formType))
			return true;
		if (!isFormBackingObjectNew(request)) {
			Object o = null;
			try {
				// it is not nice to call formBackingObject() the second time
				// but it's already probably in the persistence cache so it
				// should
				// not take too long, and it is only in a editing case
				o = formBackingObject(request);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			if (o instanceof Service) {
				Service service = (Service) o;
				if (Frequency.ONE_TIME.equals(service.getFrequency()))
					return true;
			}
		}
		return false;
	}

	protected void validate(Object command, BindingResult bindingResult) {
		Service service = (Service) command;
		if (service.getAdditionalName() == null
				|| "".equals(service.getAdditionalName())) {
			ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "name",
					"editForm.error.blank-or-null", "Value required.");
		}
		ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "price",
				"editForm.error.blank-or-null", "Value required.");
		ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "period.from",
				"editForm.error.blank-or-null", "Value required.");
		if (!service.getPeriod().isValid())
			bindingResult.rejectValue("period.to",
					"editService.periodMishmash", "Period mishmash.");
	}

	protected Map referenceI18nFields(Object o) {
		HashMap<String, String> fields = new HashMap<String, String>();
		if (o instanceof Service) {
			Service s = (Service) o;
			Customer c = cmgr.get(s.getCustomerId());
			if (c != null
					&& c.getContact().getAddress().getCountry() == Country.PL) {
				fields.put("money_label", "money.label.pl");
			}
			else {
				fields.put("money_label", "money.label.cz");
			}
		}
		return fields;
	}

}