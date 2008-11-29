package cz.silesnet.web.mvc;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.annotation.Secured;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.WebUtils;

import cz.silesnet.model.Address;
import cz.silesnet.model.Billing;
import cz.silesnet.model.Contact;
import cz.silesnet.model.Customer;
import cz.silesnet.model.Service;
import cz.silesnet.model.Historic;
import cz.silesnet.model.Label;
import cz.silesnet.model.Setting;
import cz.silesnet.model.enums.BillingStatus;
import cz.silesnet.model.enums.Country;
import cz.silesnet.model.enums.Frequency;
import cz.silesnet.service.BillingManager;
import cz.silesnet.service.CustomerManager;
import cz.silesnet.service.HistoryManager;
import cz.silesnet.service.LabelManager;
import cz.silesnet.service.SettingManager;
import cz.silesnet.service.invoice.InvoiceFormat;
import cz.silesnet.utils.CustomEnumEditor;
import cz.silesnet.utils.CustomLabelEditor;
import cz.silesnet.utils.FilterUtils;
import cz.silesnet.utils.MessagesUtils;
import cz.silesnet.utils.NavigationUtils;

/**
 * Controller for Customer handling.
 *
 * @author Richard Sikora
 */
public class CustomerController
    extends AbstractCRUDController {

    //~ Instance fields --------------------------------------------------------

    private HistoryManager  hmgr;
    private CustomerManager cMgr;
    private LabelManager lmgr;
    private SettingManager settingMgr;
	private BillingManager bMgr;
    
    //~ Methods ----------------------------------------------------------------

    public void setCustomerManager(CustomerManager customerManager) {
        cMgr = customerManager;
    }

    public void setHistoryManager(HistoryManager historyManager) {
        hmgr = historyManager;
    }

    public void setLabelManager(LabelManager labelManager) {
    	lmgr = labelManager;
    }
    
    public void setSettingManager(SettingManager settingManager) {
		settingMgr = settingManager;
	}
    
	public void setBillingManager(BillingManager billingManager) {
		bMgr = billingManager;
	}
    
    public ModelAndView cancel(HttpServletRequest request,
        HttpServletResponse response) {
        log.debug("Canceling Customer form.");
        return goBack(request, response);
    }

    @Secured({ "ROLE_ACCOUNTING" })
    public ModelAndView delete(HttpServletRequest request,
        HttpServletResponse response)
        throws Exception {
        log.debug("Deleting Customer.");
        // get persisted Customer
        Customer c = (Customer) formBackingObject(request);
        if (log.isDebugEnabled())
            log.debug("Obtained Customer: " + c);
        // delete customer
        cMgr.delete(c);
        // set success message
        MessagesUtils.setCodedSuccessMessage(request,
            "editCustomer.deleteSuccess",
            new Object[] { c.getId(), c.getName() });
        // pop top url of deleted object
        NavigationUtils.getReturnUrl(request, "");
        return showList(request, response);
    }

    @Secured({ "ROLE_ACCOUNTING" })
    public ModelAndView insert(HttpServletRequest request,
        HttpServletResponse response)
        throws Exception {
        log.debug("Inserting new Customer.");
        // get validated form Customer
        Customer c = (Customer) bindAndValidate(request);
        // set insertion date
        c.setInsertedOn(new Date());
        if (log.isDebugEnabled())
            log.debug("Obtained Customer: " + c);
        // persist changes
        cMgr.insert(c);
        // set success message
        MessagesUtils.setCodedSuccessMessage(request,
            "editCustomer.insertSuccess",
            new Object[] { c.getId(), c.getName() });
        return new ModelAndView(new RedirectView(
        		request.getContextPath() + "/customer/view.html?action=showDetail&customerId="+ c.getId()));
    }

    @Secured({ "ROLE_ACCOUNTING" })
    public ModelAndView showForm(HttpServletRequest request,
        HttpServletResponse response)
        throws Exception {
    	return super.showForm(request, response);
    }
    
    @SuppressWarnings("unchecked")
	public ModelAndView showDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.debug("Showing customer detail.");
        Customer c = (Customer) formBackingObject(request);
        // put binder into request, it contains command object
        // so no need to add it explicitly
        ServletRequestDataBinder binder = createBinder(c);
        // prepare model
        Map model = binder.getBindingResult().getModel();
        // add history is exists
        Map cHistory = referenceHistory(c);
        if (cHistory != null)
        	model.putAll(cHistory);
        model.putAll(referenceI18nFields(c));
        model.put("billsList", bMgr.getByCustomer(c));
        return new ModelAndView("customer/viewCustomer", model);
    }
    
    public ModelAndView showList(HttpServletRequest request,
        HttpServletResponse response) {
        // list all customers at once
        log.debug("Listing filtered customers");
        HashMap<String, Object> model = new HashMap<String, Object>();
        // get Customer and Service filter params from session
        Map<String, String> customerFilter = FilterUtils.getFilterMap(request, "customer.");
        Map<String, String> serviceFilter = FilterUtils.getFilterMap(request, "service.");
    	if (!AbstractCRUDController.isTablePagination(request)) {
            // support view with customers
        	Customer c = null;
        	Service s = null;
        	// get Customers example
        	if ( !((customerFilter.size() == 0) || (customerFilter.size() == 1 && "true".equals(customerFilter.get("billing.isActive"))))) {
	        	c = prepareExampleCustomer();
	        	ServletRequestDataBinder binder = new ServletRequestDataBinder(c);
	        	// register custom editors
	        	initBinder(binder);
	        	binder.bind(new MutablePropertyValues(customerFilter));
	        	if (log.isDebugEnabled())
	        		log.debug("Customer findByExample object : " + c);
        	}
        	// get Service example
        	if (serviceFilter.size() != 0) {
        		s = new Service();
	        	ServletRequestDataBinder serviceBinder = new ServletRequestDataBinder(s);
	        	serviceBinder.registerCustomEditor(Integer.class, new CustomNumberEditor(Integer.class, true));
	        	serviceBinder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("dd.MM.yyyy"), true));
	        	serviceBinder.registerCustomEditor(Frequency.class, new CustomEnumEditor<Frequency>(Frequency.MONTHLY));
	        	serviceBinder.bind(new MutablePropertyValues(serviceFilter));
	        	if (log.isDebugEnabled())
	        		log.debug("Service findByExample object : " + s);
        	}
        	if (c == null && s == null) {
	        	log.debug("Considered empty filter.");
	        	model.put("emptyFilter", true);
        	} else {
        		request.getSession().setAttribute("customersList", cMgr.getByExample(c, s));
        	}
        }
        // Frequency enum for filtering
        model.put("billingFrequency", EnumSet.of(Frequency.MONTHLY, Frequency.Q, Frequency.QQ, Frequency.ANNUAL));
        // Country enum for filtering
        model.put("addressCountry", EnumSet.allOf(Country.class));
        // BillingStatus enum
        model.put("billingStatus", EnumSet.allOf(BillingStatus.class));
        // popup calendar script
        model.put("scripts", new String[] { "calendar.js" });
        // clear navigation stack, because we are back on basic list
        NavigationUtils.clearNavigationStack(request);
        return new ModelAndView("customer/listCustomers", model);
    }

    @Secured({ "ROLE_ACCOUNTING" })
    public ModelAndView showOverview(HttpServletRequest request,
            HttpServletResponse response) {
            // list all customers at once
            log.debug("Showing customers overview.");
            HashMap<String, Object> model = new HashMap<String, Object>();
            model.put("overviewCustomers", cMgr.getOverview(null).entrySet());
            model.put("overviewCustomersCZ", cMgr.getOverview(Country.CZ).entrySet());
            model.put("overviewCustomersPL", cMgr.getOverview(Country.PL).entrySet());
            model.put("exchangeRate", settingMgr.get("exchangeRate.PLN_CZK"));
            return new ModelAndView("customer/overviewCustomers", model);
        }

    @Secured({ "ROLE_ACCOUNTING" })
    public ModelAndView update(HttpServletRequest request,
        HttpServletResponse response)
        throws Exception {
        log.debug("Updating Customer.");
        // get updated and validated Customer
        Customer c = (Customer) bindAndValidate(request);
        if (log.isDebugEnabled())
            log.debug("Obtained Customer: " + c);
        // persist changes
        cMgr.update(c);
        // set success message
        MessagesUtils.setCodedSuccessMessage(request,
            "editCustomer.updateSuccess",
            new Object[] { c.getId(), c.getName() });
        return goBack(request, response);
    }

    @Secured({ "ROLE_ACCOUNTING" })
    public ModelAndView activate(HttpServletRequest request,
            HttpServletResponse response)
            throws Exception {
            log.debug("Activating Customer.");
            // get customer
            Customer c = (Customer) formBackingObject(request);
            // activate and persist change
            c.getBilling().setIsActive(true);
            // set new status from parameter in the request
            c.getBilling().setStatus(BillingStatus.INVOICE.valueOf(ServletRequestUtils.getRequiredIntParameter(request, "newStatusId")));
            cMgr.update(c);
            // set success message
            MessagesUtils.setCodedSuccessMessage(request,
                "editCustomer.activateSuccess",
                new Object[] { c.getId(), c.getName() });
            return goBack(request, response);
        }

    @Secured({ "ROLE_ACCOUNTING" })
    public ModelAndView deactivate(HttpServletRequest request,
            HttpServletResponse response)
            throws Exception {
            log.debug("Deactivating Customer.");
            // get customer
            Customer c = (Customer) formBackingObject(request);
            // deactivate and persist change
            c.getBilling().setIsActive(false);
            // set new status from parameter in the request
            c.getBilling().setStatus(BillingStatus.INVOICE.valueOf(ServletRequestUtils.getRequiredIntParameter(request, "newStatusId")));
            cMgr.update(c);
            // set success message
            MessagesUtils.setCodedSuccessMessage(request,
                "editCustomer.deactivateSuccess",
                new Object[] { c.getId(), c.getName() });
            return goBack(request, response);
        }

    @Secured({ "ROLE_ACCOUNTING" })
    public ModelAndView updateExchangeRate(HttpServletRequest request, HttpServletResponse response) throws Exception {
		log.debug("Updating Exchange rate system setting");
		Double rate = ServletRequestUtils.getDoubleParameter(request, "exchangeRate", 0);
		if (rate != 0) {
			// persist change
			Setting s = settingMgr.get("exchangeRate.PLN_CZK");
			if (s == null) {
				s = new Setting("exchangeRate.PLN_CZK", rate.toString());
				settingMgr.insert(s);
			} else {
				s.setValue(rate.toString());
				settingMgr.update(s);
			}
			// set success message
	        MessagesUtils.setCodedSuccessMessage(request,
	                "overviewCustomers.updateSuccess", rate);
		} else {
	        MessagesUtils.setCodedFailureMessage(request,
	                "overviewCustomers.updateFailure");
		}
		// go back to customers overview
		return showOverview(request, response);
	}

    @Secured({ "ROLE_ACCOUNTING" })
    public ModelAndView exportToWinduo(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug("Exporting customers in Winduo import format.");
		// set response encoding properly
		response.setCharacterEncoding("Cp1250");
		// use text/plain to display in browser
		response.setContentType("text/csv;charset=windows-1250");
		// dump customers to response writer
		// get selected customers
		List<Customer> cList = new ArrayList<Customer>();
		for (String cIdStr : getSelectedCustomersIdSet(request)) {
			log.debug(cIdStr);
			cList.add(cMgr.get(Long.valueOf(cIdStr)));
		}
		cMgr.exportCusotmersToWinDuo(cList, response.getWriter());
		return null;
	}

    @Secured({ "ROLE_ACCOUNTING" })
    public ModelAndView exportToInsert(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug("Exporting customers to Insert in XML.");
		// set response encoding properly
		response.setCharacterEncoding("Cp1250");
		// use text/plain to display in browser
		response.setContentType("text/csv;charset=windows-1250");
		// dump customers to response writer
		// get selected customers
		List<Customer> cList = new ArrayList<Customer>();
		for (String cIdStr : getSelectedCustomersIdSet(request)) {
			log.debug(cIdStr);
			cList.add(cMgr.get(Long.valueOf(cIdStr)));
		}
		cMgr.exportCusotmersToInsert(cList, response.getWriter());
		return null;
	}

    @Secured({ "ROLE_ACCOUNTING" })
	public ModelAndView exportAllToWinduo(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug("Exporting all customers in Winduo import format.");
		// set response encoding properly
		response.setCharacterEncoding("Cp1250");
		// use text/plain to display in browser
		response.setContentType("text/csv;charset=windows-1250");
		// dump all customers to response writer
		cMgr.exportCusotmersToWinDuo(cMgr.getAll(), response.getWriter());
		return null;
	}
	
    protected Boolean isFormBackingObjectNew(HttpServletRequest request) {
        return (ServletRequestUtils.getLongParameter(request, "customerId", 0) == 0)
        ? true : false;
    }

    public ModelAndView goBack(HttpServletRequest request,
        HttpServletResponse response) {
        String returnUrl = NavigationUtils.getReturnUrl(request,
                "/customer/view.html?action=showList");
        // need redirect so we will go throught controller mechanism
        return new ModelAndView(new RedirectView(returnUrl));
    }

    protected Object formBackingObject(HttpServletRequest request)
        throws Exception {
        Customer c = null;
        if (isFormBackingObjectNew(request)) {
            c = new Customer();
            // set country according to country filter, default cz
            // get filter map
            Map<String, String> filterMap = FilterUtils.getFilterMap(request, "customer.");
            // if country filter set to pl change customers country
            log.debug(filterMap.get("contact.address.country"));
            if (filterMap.get("contact.address.country") != null
            	&& filterMap.get("contact.address.country").equals(Integer.valueOf(Country.PL.getId()).toString()))
            	c.getContact().getAddress().setCountry(Country.PL);
        }
        else
            c = cMgr.get(ServletRequestUtils.getRequiredLongParameter(request,
                        "customerId"));
        return c;
    }

    protected void initBinder(ServletRequestDataBinder binder) {
        // Integers
        binder.registerCustomEditor(Integer.class,
            new CustomNumberEditor(Integer.class, true));
        // Long numbers
        binder.registerCustomEditor(Long.class,
            new CustomNumberEditor(Long.class, true));
        // Dates
        binder.registerCustomEditor(Date.class,
            new CustomDateEditor(new SimpleDateFormat("dd.MM.yyyy"), true));
        // Frequencies
        binder.registerCustomEditor(Frequency.class,
            new CustomEnumEditor<Frequency>(Frequency.MONTHLY));
        // Countries
        binder.registerCustomEditor(Country.class,
            new CustomEnumEditor<Country>(Country.CZ));
        // Status
        binder.registerCustomEditor(BillingStatus.class,
            new CustomEnumEditor<BillingStatus>(BillingStatus.INVOICE));
        // InvoiceFormat
        binder.registerCustomEditor(InvoiceFormat.class,
            new CustomEnumEditor<InvoiceFormat>(InvoiceFormat.LINK));
        // Labels
        binder.registerCustomEditor(Label.class, new CustomLabelEditor(lmgr));
    }

    protected Map referenceData(HttpServletRequest request) {
        HashMap<String, Object> model = new HashMap<String, Object>();

        // Frequency enum
        model.put("billingFrequency", EnumSet.of(Frequency.MONTHLY, Frequency.Q, Frequency.QQ, Frequency.ANNUAL));
        // Country enum
        model.put("addressCountry", EnumSet.allOf(Country.class));
        // BillingStatus enum
//        model.put("billingStatus", EnumSet.allOf(BillingStatus.class));
//        logic moved to referenceI18nFields because we have the customer there available
        // Shire labels
        model.put("shires", lmgr.getSubLabels(lmgr.get(Label.SHIRES)));
        // Responsible labels
        model.put("responsibles", lmgr.getSubLabels(lmgr.get(Label.RESPONSIBLES)));
        // InvoiceFormat enum
        model.put("invoiceFormats", EnumSet.allOf(InvoiceFormat.class));
        // include Javascripts for safe submitting and calendar
        model.put("scripts", new String[] { "safeSubmit.js", "calendar.js" });
        return model;
    }

    protected Map referenceHistory(Historic historic) {
        HashMap<String, Object> model = new HashMap<String, Object>();
        if (historic.getHistoryId() != null)
            model.put("historyRecord", hmgr.getHistory(historic));
        return model;
    }

    private Customer prepareExampleCustomer() {
    	Customer c = new Customer();
    	c.setBilling(new Billing());
    	c.getBilling().setFrequency(null);
    	c.getBilling().setIsBilledAfter(null);
    	c.getBilling().setIsActive(null);
    	c.getBilling().setStatus(null);
    	c.setContact(new Contact());
    	c.getContact().setAddress(new Address());
    	c.getContact().getAddress().setCountry(null);
    	return c;
    }
    
    protected void validate(Object command, BindingResult bindingResult) {
    	Customer customer = (Customer) command;
    	ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "name",
            "editForm.error.blank-or-null", "Value required.");
        ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "billing.lastlyBilled",
        		"editForm.error.blank-or-null", "Value required.");
        ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "publicId",
        		"editForm.error.blank-or-null", "Value required.");
        // validate publicId uniqueness, use exportPublicId
        log.debug("Checking uniqueness for publicId: " + customer.getPublicId());
        Customer exampleCustomer = prepareExampleCustomer();
        exampleCustomer.setPublicId(customer.getPublicId());
        List<Customer> customers = cMgr.getByExample(exampleCustomer);
        for (Customer c: customers) {
        	if (!c.getId().equals(customer.getId())) {
	        	// there is existing customer with given exportPublicId, so reject it
        		bindingResult.rejectValue("publicId", "editCustomer.not-unique.publicId", new Object[] {c.getName()}, "Unique value required.");
            	break;
        	}
        }
        // validate Symbol uniqueness if not empty
        if (!(customer.getSymbol() == null || "".equals(customer.getSymbol()))) {
	        log.debug("Checking uniqueness for Symbol: " + customer.getSymbol());
	        exampleCustomer = prepareExampleCustomer();
	        exampleCustomer.setSymbol(customer.getSymbol());
	        customers = cMgr.getByExample(exampleCustomer);
	        for (Customer c: customers) {
	        	if (!c.getId().equals(customer.getId())) {
		        	// there is existing customer with given Symbol, so reject it
	        		bindingResult.rejectValue("symbol", "editCustomer.not-unique.symbol", new Object[] {c.getName()}, "Unique value required.");
	            	break;
	        	}
	        }
        }
        // validate contract number uniqueness for each contrac no entered
        // contractNo is multivalue field separated by ","
        String[] contractNumbers = customer.getContractNo().split(",");
        exampleCustomer = prepareExampleCustomer();
		// check each contract number
contractNumbersLabel:
		for (String contractNo: contractNumbers) {
	        exampleCustomer.setContractNo(contractNo.trim());
	        // get possible customers with such contractNo
	        customers = cMgr.getByExample(exampleCustomer);
	        // iterate over selected customers, exept edited one
	        for (Customer c: customers) {
	        	if (!c.getId().equals(customer.getId())) {
	        		// get all existing cotrac numbers and compare each
	        		String[] contracNumbersExisting = c.getContractNo().split(",");
	        		for (String contractNoExisting: contracNumbersExisting) {
	        			// if match found reject the value
	        			if (contractNo.equals(contractNoExisting)) {
			        		bindingResult.rejectValue("contractNo", "editForm.error.not-unique", "Unique value required.");
				        	break contractNumbersLabel;
	        			}
	        		}
	        	}
	        }
        }
    }
    
    protected Map referenceI18nFields(Object o) {
    	HashMap<String, Object> fields = new HashMap<String, Object>();
    	if (o instanceof Customer) {
    		Customer c = (Customer) o;
    		if (c.getContact().getAddress().getCountry() == Country.PL) {
	    		fields.put("publicId_label", "publicId.label.pl");
	    		fields.put("dic_label", "dic.label.pl");
	    	} else {
	    		fields.put("publicId_label", "publicId.label.cz");
	    		fields.put("dic_label", "dic.label.cz");
	    	}
        	// reference statuses
    		if (c.getBilling().getIsActive()) {
	          fields.put("billingStatus", BillingStatus.getActiveStatuses());
	          fields.put("billingStatusComplementary", BillingStatus.getInactiveStatuses());
    		} else {
  	          fields.put("billingStatus", BillingStatus.getInactiveStatuses());
	          fields.put("billingStatusComplementary", BillingStatus.getActiveStatuses());
    		}
    	}
    	return fields;
    }
    
	@SuppressWarnings("unchecked")
	private Iterable<String> getSelectedCustomersIdSet(HttpServletRequest request) {
		Map<String, Object> selectedCustomersMap = WebUtils.getParametersStartingWith(request, "selectedCustomers_");
		return selectedCustomersMap.keySet();
	}
}