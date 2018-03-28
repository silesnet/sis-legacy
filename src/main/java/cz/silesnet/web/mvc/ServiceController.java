package cz.silesnet.web.mvc;

import cz.silesnet.dao.ProductDAO;
import cz.silesnet.model.Customer;
import cz.silesnet.model.Label;
import cz.silesnet.model.Period;
import cz.silesnet.model.Product;
import cz.silesnet.model.Service;
import cz.silesnet.model.enums.Country;
import cz.silesnet.model.enums.Frequency;
import cz.silesnet.service.CustomerManager;
import cz.silesnet.service.LabelManager;
import cz.silesnet.util.CustomEnumEditor;
import cz.silesnet.util.MessagesUtils;
import cz.silesnet.util.NavigationUtils;
import org.apache.commons.beanutils.BeanComparator;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

import static cz.silesnet.model.enums.Frequency.ANNUAL;
import static cz.silesnet.model.enums.Frequency.MONTHLY;
import static cz.silesnet.model.enums.Frequency.ONE_TIME;
import static org.springframework.web.bind.ServletRequestUtils.getLongParameters;

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

    private ProductDAO productDao;

    // ~ Methods
    // ----------------------------------------------------------------

    // injected by Spring

    public void setLabelManager(LabelManager labelManager) {
        lmgr = labelManager;
    }

    public void setCustomerManager(CustomerManager customerManager) {
        cmgr = customerManager;
    }

    public void setProductDao(ProductDAO productDao) {
        this.productDao = productDao;
    }

    @Secured({"ROLE_ACCOUNTING"})
    public ModelAndView showForm(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {

        return super.showForm(request, response);
    }

    public ModelAndView cancel(HttpServletRequest request,
                               HttpServletResponse response) {
        log.debug("Canceling, going back.");

        return closeForm(request, response);
    }

    @Secured({"ROLE_ACCOUNTING"})
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
                "editService.deleteSuccess", new Object[]{command.getId(),
                command.getName()});

        // close form
        return closeForm(request, response);
    }

    @Secured({"ROLE_ACCOUNTING"})
    public ModelAndView insert(HttpServletRequest request,                               HttpServletResponse response) throws Exception {
        log.debug("Inserting service.");
        Service command = (Service) bindAndValidate(request);
        if (log.isDebugEnabled())
            log.debug("Obtained object: " + command);
        if (!ONE_TIME.equals(command.getFrequency()))
            throw new IllegalArgumentException("can't insert non one time service '" + command.getFrequency() + "'");
        final long serviceId = cmgr.nextOneTimeServiceId(command.getCustomerId());
        command.setId(serviceId);
        cmgr.insertService(command);
        MessagesUtils.setCodedSuccessMessage(request, "editService.insertSuccess", new Object[]{command.getId(), command.getName()});
        return closeForm(request, response);
    }

    public ModelAndView showList(HttpServletRequest request,
                                 HttpServletResponse response) {
        HashMap<String, Object> model = new HashMap<String, Object>();
        log.info("Listing all serivices.");
        // model.put("servicesList", smgr.getAllOrphans());

        return new ModelAndView("service/listServices", model);
    }

    @Secured({"ROLE_ACCOUNTING"})
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
                "editService.updateSuccess", new Object[]{command.getId(),
                command.getName()});

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
                s.setFrequency(ONE_TIME);
            } else {
                s.setFrequency(MONTHLY);
            }
        } else
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
                new CustomEnumEditor<Frequency>(MONTHLY));
    }

    protected Map<String, Object> referenceData(HttpServletRequest request) {
        HashMap<String, Object> model = new HashMap<String, Object>();
        final Country country = resolveRequestCountry(request);
        final List<Product> products = new ArrayList<>();
        model.put("country", country.getShortName().toUpperCase());
        if (isOneTimeService(request)) {
            products.addAll(oneTimeProducts(country));
            model.put("serviceFrequency", EnumSet.of(ONE_TIME));
        } else {
            products.addAll(productDao.getByCountry(country));
            model.put("serviceFrequency", EnumSet.of(MONTHLY, ANNUAL));
        }
        model.put("products", uniqueProductNamesOf(products));
        model.put("scripts", new String[]{
            "safeSubmit.js", "calendar.js", "comboBox.js"});

        return model;
    }

    private List<Product> uniqueProductNamesOf(List<Product> products) {
        final Map<String, Product> map = new LinkedHashMap<>();
        for (Product product : products) {
            if (!map.containsKey(product.getName())) {
                map.put(product.getName(), product);
            }
        }
        final ArrayList<Product> result = new ArrayList<>();
        result.addAll(map.values());
        return result;
    }

    private List<Product> oneTimeProducts(Country country) {
        if (Country.CZ.equals(country)) {
            return oneTimeProducts("Aktivace", "Odpočet", "Jiné");
        } else {
            return oneTimeProducts("Aktywacja", "Odliczenie", "Inne");
        }
    }

    private List<Product> oneTimeProducts(String ...names) {
        final List<Product> products = new ArrayList<>();
        for (String name : names) {
            final Product product = new Product();
            product.setName(name);
            product.setIsDedicated(false);
            products.add(product);
        }
        return products;
    }
    private Country resolveRequestCountry(HttpServletRequest request) {
        final long[] serviceIds = getLongParameters(request, "serviceId");
        final long[] customerIds = getLongParameters(request, "customerId");
        if (serviceIds.length == 1) {
            if ("1".equals(("" + serviceIds[0]).substring(0, 1))) {
                return Country.CZ;
            } else {
                return Country.PL;
            }
        } else if (customerIds.length == 1) {
            final Customer customer = cmgr.get(customerIds[0]);
            if (customer != null) {
                return customer.getContact().getAddress().getCountry();
            }
        }
        return Country.CZ;
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
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (o instanceof Service) {
                Service service = (Service) o;
                if (ONE_TIME.equals(service.getFrequency()))
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
            } else {
                fields.put("money_label", "money.label.cz");
            }
        }
        return fields;
    }

}