package cz.silesnet.web.mvc;

import cz.silesnet.model.*;
import cz.silesnet.model.enums.BillingStatus;
import cz.silesnet.model.enums.Country;
import cz.silesnet.model.enums.Frequency;
import cz.silesnet.model.enums.InvoiceFormat;
import cz.silesnet.service.*;
import cz.silesnet.util.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

import static cz.silesnet.util.MessagesUtils.setCodedFailureMessage;
import static cz.silesnet.util.MessagesUtils.setCodedSuccessMessage;

/**
 * Controller for Customer handling.
 *
 * @author Richard Sikora
 */
public class CustomerController extends AbstractCRUDController {

    private static final int DIVISION_LENGTH = 32;

    private static final int ACCOUNT_NUMBER_LENGTH = 26;

    private static final int BANK_CODE_LENGTH = 4;

    private HistoryManager hmgr;

    private CustomerManager cMgr;

    private LabelManager lmgr;

    private SettingManager settingMgr;

    private BillingManager bMgr;

    private EventManager eventManager;

    private CommandManager commandManager;

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

    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public void setCommandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public ModelAndView cancel(HttpServletRequest request, HttpServletResponse response) {
        log.debug("Canceling Customer form.");
        return goBack(request, response);
    }

    @Secured({"ROLE_ACCOUNTING"})
    public ModelAndView addService(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final int blueprintId = ServletRequestUtils.getRequiredIntParameter(request, "serviceId");
        log.debug("inserting new service from blueprint '" + blueprintId + "'");
        final ServiceBlueprint blueprint = cMgr.addService(blueprintId);
        log.debug("new service added from blueprint: " + blueprint);
        setCodedSuccessMessage(request, "editCustomer.addServiceSuccess", blueprint.getName());
        ModelAndView modelAndView;
        if (blueprint.isNewCustomerCreated())
            modelAndView = new ModelAndView(new RedirectView(request.getContextPath() + "/customer/edit.html?action=showForm&customerId=" + blueprint.getCustomerId()));
        else
            modelAndView = new ModelAndView(new RedirectView(request.getContextPath() + "/customer/view.html?action=showDetail&customerId=" + blueprint.getCustomerId()));
        return modelAndView;
    }

    @Override
    @Secured({"ROLE_ACCOUNTING"})
    public ModelAndView delete(HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.debug("Deleting Customer.");
        // get persisted Customer
        Customer c = (Customer) formBackingObject(request);
        if (log.isDebugEnabled())
            log.debug("Obtained Customer: " + c);
        // delete customer
        cMgr.delete(c);
        // set success message
        setCodedSuccessMessage(request, "editCustomer.deleteSuccess",
                new Object[]{c.getId(), c.getName()});
        // pop top url of deleted object
        NavigationUtils.getReturnUrl(request, "");
        return showList(request, response);
    }

    @Override
    @Secured({"ROLE_ACCOUNTING"})
    public ModelAndView insert(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
        setCodedSuccessMessage(request, "editCustomer.insertSuccess",
                new Object[]{c.getId(), c.getName()});
        return new ModelAndView(new RedirectView(request.getContextPath()
                + "/customer/view.html?action=showDetail&customerId=" + c.getId()));
    }

    @Override
    @Secured({"ROLE_ACCOUNTING"})
    public ModelAndView showForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
        model.put("isSynchronized", c.isSpsSynchronized());
        return new ModelAndView("customer/viewCustomer", model);
    }

    @Override
    public ModelAndView showList(HttpServletRequest request, HttpServletResponse response) {
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
            if (!((customerFilter.size() == 0) || (customerFilter.size() == 1
                    && "true".equals(customerFilter.get("billing.isActive"))))) {
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
        model.put("scripts", new String[]{"calendar.js"});
        // clear navigation stack, because we are back on basic list
        NavigationUtils.clearNavigationStack(request);
        return new ModelAndView("customer/listCustomers", model);
    }

    @Secured({"ROLE_ACCOUNTING"})
    public ModelAndView showOverview(HttpServletRequest request, HttpServletResponse response) {
        log.debug("Showing customers overview.");
        Map<String, Long> summaryCZ = cMgr.getSummaryFor(Country.CZ);
        Map<String, Long> summaryPL = cMgr.getSummaryFor(Country.PL);
        Map<String, Long> summary = new LinkedHashMap<String, Long>();
        addSummaryKeys("overviewCustomers.totalCustomers", summary, summaryCZ, summaryPL);
        addSummaryKeys("overviewCustomers.totalServices.CZK", summary, summaryCZ, summaryPL);
        addSummaryKeys("overviewCustomers.totalLastInvoicing.CZK", summary, summaryCZ, summaryPL);

        DecimalFormat format = new DecimalFormat();
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols();
        formatSymbols.setDecimalSeparator('.');
        formatSymbols.setGroupingSeparator(' ');
        format.setDecimalFormatSymbols(formatSymbols);
        HashMap<String, Object> model = new HashMap<String, Object>();
        model.put("overviewCustomers", formatSummaryKeys(summary, format).entrySet());
        model.put("overviewCustomersCZ", formatSummaryKeys(summaryCZ, format).entrySet());
        model.put("overviewCustomersPL", formatSummaryKeys(summaryPL, format).entrySet());
        model.put("exchangeRate", settingMgr.get("exchangeRate.PLN_CZK"));
        return new ModelAndView("customer/overviewCustomers", model);
    }

    private void addSummaryKeys(String key, Map<String, Long> result, Map<String, Long> a, Map<String, Long> b) {
        result.put(key, a.get(key) + b.get(key));
    }

    private Map<String, String> formatSummaryKeys(Map<String, Long> summary, DecimalFormat format) {
        Map<String, String> formattedSummary = new LinkedHashMap<String, String>();
        for (Map.Entry<String, Long> entry : summary.entrySet()) {
            String value = null;
            if (entry.getKey().endsWith(".PLN_CZK"))
                value = "" + ((double) entry.getValue()) / 100.0;
            else
                value = format.format(entry.getValue());

            if (entry.getKey().endsWith("Download") || entry.getKey().endsWith("Upload")) {
                formattedSummary.put(entry.getKey(), value + " Mbps");
            } else if (entry.getKey().endsWith(".CZK")) {
                formattedSummary.put(entry.getKey(), value + " "
                        + MessagesUtils.getMessage("money.label.cz"));
            } else if (entry.getKey().endsWith(".PLN")) {
                formattedSummary.put(entry.getKey(), value + " "
                        + MessagesUtils.getMessage("money.label.pl"));
            } else {
                formattedSummary.put(entry.getKey(), value);
            }
        }
        return formattedSummary;
    }

    @Override
    @Secured({"ROLE_ACCOUNTING"})
    public ModelAndView update(HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.debug("Updating Customer.");
        // get updated and validated Customer
        Customer c = (Customer) bindAndValidate(request);
        if (log.isDebugEnabled())
            log.debug("Obtained Customer: " + c);
        // persist changes
        cMgr.update(c);
        // set success message
        setCodedSuccessMessage(request, "editCustomer.updateSuccess",
                new Object[]{c.getId(), c.getName()});
        return goBack(request, response);
    }

    @Secured({"ROLE_ACCOUNTING"})
    public ModelAndView reconnect(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Customer c = (Customer) formBackingObject(request);
        log.debug("Enabling customers connectivity for " + c.getName());
        final Command reconnect = new Command();
        reconnect.setCommand("reconnect");
        reconnect.setEntity("customers");
        reconnect.setEntityId(c.getId());

        final long reconnectCommandId = commandManager.submit(reconnect);

        final CommandExecution execution = new CommandExecution(commandManager, reconnectCommandId);
        execution.waitUntilFinished(2000);
        if (execution.hasCompleted()) {
            setCodedSuccessMessage(request, "editCustomer.reconnectSuccess", new Object[]{c.getId(), c.getName()});
        } else {
            setCodedFailureMessage(request, "editCustomer.reconnectFail", new Object[]{c.getId(), c.getName()});
        }
        return goBack(request, response);
    }

    @Secured({"ROLE_ACCOUNTING"})
    public ModelAndView activate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.debug("Activating Customer.");
        // get customer
        Customer c = (Customer) formBackingObject(request);
        // activate and persist change
        c.getBilling().setIsActive(true);
        // set new status from parameter in the request
        c.getBilling().setStatus(BillingStatus.INVOICE.valueOf(
                ServletRequestUtils.getRequiredIntParameter(request, "newStatusId")));
        cMgr.update(c);
        // set success message
        setCodedSuccessMessage(request, "editCustomer.activateSuccess",
                new Object[]{c.getId(), c.getName()});
        return goBack(request, response);
    }

    @Secured({"ROLE_ACCOUNTING"})
    public ModelAndView deactivate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.debug("Deactivating Customer.");
        // get customer
        Customer c = (Customer) formBackingObject(request);
        // deactivate and persist change
        c.getBilling().setIsActive(false);
        // set new status from parameter in the request
        c.getBilling().setStatus(BillingStatus.INVOICE.valueOf(
                ServletRequestUtils.getRequiredIntParameter(request, "newStatusId")));
        cMgr.update(c);
        // set success message
        setCodedSuccessMessage(request, "editCustomer.deactivateSuccess",
                new Object[]{c.getId(), c.getName()});
        return goBack(request, response);
    }

    @Secured({"ROLE_ACCOUNTING"})
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
            setCodedSuccessMessage(request, "overviewCustomers.updateSuccess", rate);
        } else {
            MessagesUtils.setCodedFailureMessage(request, "overviewCustomers.updateFailure");
        }
        // go back to customers overview
        return showOverview(request, response);
    }

    @Secured({"ROLE_ACCOUNTING"})
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
        cMgr.exportCustomersToInsert(cList, response.getWriter());
        return null;
    }

    @Override
    protected Boolean isFormBackingObjectNew(HttpServletRequest request) {
        return (ServletRequestUtils.getLongParameter(request, "customerId", 0) == 0);
    }

    public ModelAndView goBack(HttpServletRequest request, HttpServletResponse response) {
        String returnUrl = NavigationUtils.getReturnUrl(request, "/customer/view.html?action=showList");
        // need redirect so we will go throught controller mechanism
        return new ModelAndView(new RedirectView(returnUrl));
    }

    @Override
    protected Object formBackingObject(HttpServletRequest request) throws Exception {
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
        } else
            c = cMgr.get(ServletRequestUtils.getRequiredLongParameter(request, "customerId"));
        return c;
    }

    @Override
    protected void initBinder(ServletRequestDataBinder binder) {
        // Integers
        binder.registerCustomEditor(Integer.class, new CustomNumberEditor(Integer.class, true));
        // Long numbers
        binder.registerCustomEditor(Long.class, new CustomNumberEditor(Long.class, true));
        // Dates
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("dd.MM.yyyy"), true));
        // Frequencies
        binder.registerCustomEditor(Frequency.class, new CustomEnumEditor<Frequency>(Frequency.MONTHLY));
        // Countries
        binder.registerCustomEditor(Country.class, new CustomEnumEditor<Country>(Country.CZ));
        // Status
        binder.registerCustomEditor(BillingStatus.class, new CustomEnumEditor<BillingStatus>(BillingStatus.INVOICE));
        // InvoiceFormat
        binder.registerCustomEditor(InvoiceFormat.class, new CustomEnumEditor<InvoiceFormat>(InvoiceFormat.LINK));
        // Labels
        binder.registerCustomEditor(Label.class, new CustomLabelEditor(lmgr));
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Map referenceData(HttpServletRequest request) {
        HashMap<String, Object> model = new HashMap<String, Object>();
        // Frequency enum
        model.put("billingFrequency", EnumSet.of(Frequency.MONTHLY, Frequency.Q, Frequency.QQ, Frequency.ANNUAL));
        // Country enum
        model.put("addressCountry", EnumSet.allOf(Country.class));
        // BillingStatus enum
        // model.put("billingStatus", EnumSet.allOf(BillingStatus.class));
        // logic moved to referenceI18nFields because we have the customer there
        // available
        // InvoiceFormat enum
        model.put("invoiceFormats", EnumSet.allOf(InvoiceFormat.class));
        // include Javascripts for safe submitting and calendar
        // when customer is active, and is disconnected (replay, customer events)
        model.put("scripts", new String[]{"safeSubmit.js", "calendar.js"});
        return model;
    }

    @SuppressWarnings("unchecked")
    @Override
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

    @Override
    protected void validate(Object command, BindingResult bindingResult) {
        Customer customer = (Customer) command;
        ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "name", "editForm.error.blank-or-null", "Value required.");
        ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "billing.lastlyBilled", "editForm.error.blank-or-null", "Value required.");
        ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "publicId", "editForm.error.blank-or-null", "Value required.");
        // check account number
        String accNo = customer.getBilling().getAccountNumber();
        if (StringUtils.isNotBlank(accNo)) {
            if (accNo.length() > ACCOUNT_NUMBER_LENGTH) {
                bindingResult.rejectValue("billing.accountNumber", "editCustomer.too-long",
                        new Object[]{ACCOUNT_NUMBER_LENGTH}, "Field is too long (max  " + ACCOUNT_NUMBER_LENGTH + " characters).");
            }
            if (!StringUtils.isNumeric(accNo.replace("-", ""))) {
                bindingResult.rejectValue("billing.accountNumber", "editCustomer.numeric", "Numeric value expected.");
            }
        }
        // check bank code
        String bankCo = customer.getBilling().getBankCode();
        if (StringUtils.isNotBlank(bankCo)) {
            if (bankCo.length() > BANK_CODE_LENGTH) {
                bindingResult.rejectValue("billing.bankCode", "editCustomer.too-long",
                        new Object[]{BANK_CODE_LENGTH}, "Field is too long (max " + BANK_CODE_LENGTH + " characters).");
            }
            if (!StringUtils.isNumeric(bankCo.replace("-", ""))) {
                bindingResult.rejectValue("billing.bankCode", "editCustomer.numeric", "Numeric value expected.");
            }
        }
        // validate supplementaryName for 32 characters
        String division = customer.getSupplementaryName();
        if (StringUtils.isNotBlank(division) && division.length() > DIVISION_LENGTH) {
            bindingResult.rejectValue("supplementaryName", "editCustomer.too-long",
                    new Object[]{DIVISION_LENGTH}, "Field is too long (max  " + DIVISION_LENGTH + " characters).");
        }
        // validate publicId uniqueness, use exportPublicId
        log.debug("Checking uniqueness for publicId: " + customer.getPublicId());
        Customer exampleCustomer = prepareExampleCustomer();
        exampleCustomer.setPublicId(customer.getPublicId());
        List<Customer> customers = cMgr.getByExample(exampleCustomer);
        for (Customer c : customers) {
            if (!c.getId().equals(customer.getId())) {
                // there is existing customer with given exportPublicId, so
                // reject it
                /*
                * DISABLED: 2009-05-06 bindingResult.rejectValue("publicId",
                * "editCustomer.not-unique.publicId", new Object[] { c.getName() },
                * "Unique value required.");
                */
                break;
            }
        }
        // validate Symbol uniqueness if not empty
        if (!(customer.getSymbol() == null || "".equals(customer.getSymbol()))) {
            log.debug("Checking uniqueness for Symbol: " + customer.getSymbol());
            exampleCustomer = prepareExampleCustomer();
            exampleCustomer.setSymbol(customer.getSymbol());
            exampleCustomer.getContact().getAddress().setCountry(customer.getContact().getAddress().getCountry());
            customers = cMgr.getByExample(exampleCustomer);
            for (Customer c : customers) {
                if (!c.getId().equals(customer.getId())) {
                    // there is existing customer with given Symbol, so reject
                    // it
                    bindingResult.rejectValue("symbol", "editCustomer.not-unique.symbol",
                            new Object[]{c.getName()}, "Unique value required.");
                    break;
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
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
            fields.put("isDisconnected", isDisconnected(c.getId()));
        }
        return fields;
    }

    private boolean isDisconnected(long customerId) {
        final Set<String> connectionEvents = new HashSet<>();
        connectionEvents.add("disconnected");
        connectionEvents.add("reconnected");
        connectionEvents.add("connected");
        String lastEvent = null;
        for (Event event : eventManager.events("customers", customerId)) {
            if (connectionEvents.contains(event.getEvent())) {
                lastEvent = event.getEvent();
            }
        }
        return "disconnected".equals(lastEvent);
    }

    @SuppressWarnings("unchecked")
    private Iterable<String> getSelectedCustomersIdSet(
            HttpServletRequest request) {
        Map<String, Object> selectedCustomersMap = WebUtils.getParametersStartingWith(request, "selectedCustomers_");
        return selectedCustomersMap.keySet();
    }
}
