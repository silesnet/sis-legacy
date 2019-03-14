package cz.silesnet.service.impl;

import cz.silesnet.dao.BillDAO;
import cz.silesnet.dao.CustomerDAO;
import cz.silesnet.dao.LabelDAO;
import cz.silesnet.dao.ServiceDAO;
import cz.silesnet.event.EventBus;
import cz.silesnet.event.Key;
import cz.silesnet.event.support.JsonPayload;
import cz.silesnet.model.*;
import cz.silesnet.model.enums.BillingStatus;
import cz.silesnet.model.enums.Country;
import cz.silesnet.service.CustomerManager;
import cz.silesnet.service.HistoryManager;
import cz.silesnet.service.NetworkService;
import cz.silesnet.service.SettingManager;
import cz.silesnet.util.MessagesUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class CustomerManagerImpl implements CustomerManager {

    protected final Log log = LogFactory.getLog(getClass());

    private CustomerDAO dao;

    private BillDAO bDao;

    private ServiceDAO sDao;

    private HistoryManager hmgr;

    private SettingManager settingMgr;

    private LabelDAO labelDAO;

    private EventBus eventBus;

    private NetworkService networkService;

    public Customer get(Long customerId) {
        return dao.get(customerId);
    }

    @Override
    public String findAddressById(long addressId) {
        return sDao.findAddressById(addressId);
    }

    public ServiceBlueprint addService(final Integer blueprintId) {
        final ServiceBlueprint blueprint = sDao.findBlueprint(blueprintId);
        Customer customer;
        if (blueprint.shouldCreateNewCustomer()) {
            customer = blueprint.createNewCustomer();
            log.debug(customer);
            insert(customer);
        } else {
            customer = dao.get(blueprint.getCustomerId().longValue());
        }
        final Service service = blueprint.buildService(customer);
        customer.getServices().add(service);
        blueprint.imprintNewServiceOn(customer);
        update(customer);

        blueprint.setCustomerId(customer.getId().intValue());
        blueprint.setBillingOn(new Date());
        sDao.saveBlueprint(blueprint);
        
        return blueprint;
    }

    public void insert(Customer customer) {
        customer.setId(null);     // make sure we will have insert
        updateSymbol(customer);
        // assign new historyId
        customer.setHistoryId(hmgr.getNewHistoryId());
        // save history record
        hmgr.insertHistory(null, customer);
        // persist new customer
        dao.save(customer);
    }

    public void update(Customer customer) {
        // get former customer to checkperson orphans and generate history diff
        Customer formerCustomer = dao.get(customer.getId());
        // detatch former customer from hibernate session
        dao.evict(formerCustomer);
        // do some validity checks
        Assert.notNull(formerCustomer.getHistoryId(), "Persisted customer without historyId.");
        Assert.isTrue(formerCustomer.getHistoryId().equals(customer.getHistoryId()), "Outside (illegal) historyId change.");
        // update symbol if needed
        updateSymbol(customer);
        // ok, now persist history record
        hmgr.insertHistory(formerCustomer, customer);
        if (isSpsUpdated(formerCustomer, customer)) {
            customer.setUpdated(new Date());
        }
        // save updated customer
        dao.save(customer);
    }

    public List<Customer> getAll() {
        return dao.getAll();
    }

    public List<Customer> getByExample(Customer customer) {
        return dao.getByExample(customer);
    }

    public void delete(Customer customer) {
        // delete customers bills
        List<Bill> bills = fetchBills(customer);
        bDao.removeAll(bills);
        // delete customers history
        hmgr.deleteHistory(customer);
        // delete customer
        dao.remove(customer);
    }

    private boolean isSpsUpdated(Customer formerCustomer, Customer customer) {
        // general
        if (changedString(formerCustomer.getName(), customer.getName()))
            return true;
        if (changedString(formerCustomer.getSupplementaryName(), customer.getSupplementaryName()))
            return true;
        if (changedString(formerCustomer.getPublicId(), customer.getPublicId()))
            return true;
        if (changedString(formerCustomer.getDIC(), customer.getDIC()))
            return true;
        if (changedString(formerCustomer.getSymbol(), customer.getSymbol()))
            return true;
        if (changedString(formerCustomer.getBilling().getAccountNumber(), customer.getBilling().getAccountNumber()))
            return true;
        if (changedString(formerCustomer.getBilling().getBankCode(), customer.getBilling().getBankCode()))
            return true;
        // address
        Address formerAddres = formerCustomer.getContact().getAddress();
        Address addres = customer.getContact().getAddress();
        if (changedString(formerAddres.getStreet(), addres.getStreet()))
            return true;
        if (changedString(formerAddres.getCity(), addres.getCity()))
            return true;
        if (changedString(formerAddres.getPostalCode(), addres.getPostalCode()))
            return true;
        // contact
        Contact formerContact = formerCustomer.getContact();
        Contact contact = customer.getContact();
        if (changedString(formerContact.getEmail(), contact.getEmail()))
            return true;
        if (changedString(formerContact.getPhone(), contact.getPhone()))
            return true;
        if (changedString(formerContact.getName(), contact.getName()))
            return true;
        if (!formerCustomer.getBilling().getIsActive() && customer.getBilling().getIsActive())
            return true;
        return false;
    }

    private boolean changedString(String former, String current) {
        if (former == null && current == null)
            return false;
        if (former == null || current == null)
            return true;
        return !former.equals(current);
    }

    public Map<String, Long> getSummaryFor(Country c) {
        Map<String, Long> sumForCountry = sDao.calculateSummaryFor(c);
        if (Country.CZ.equals(c)) {
            long totalServicesCZK = sumForCountry.remove("overviewCustomers.totalServices");
            sumForCountry.put("overviewCustomers.totalServices.CZK", totalServicesCZK);

            long lastInvoicingCZK = sumForCountry.remove("overviewCustomers.totalLastInvoicing");
            sumForCountry.put("overviewCustomers.totalLastInvoicing.CZK", lastInvoicingCZK);

        } else if (Country.PL.equals(c)) {
            double ratePLNtoCZK = settingMgr.getDouble("exchangeRate.PLN_CZK", Double.valueOf(7.5));

            long totalServicesPLN = sumForCountry.remove("overviewCustomers.totalServices");
            long totalServicesCZK = (long) (totalServicesPLN * ratePLNtoCZK);
            sumForCountry.put("overviewCustomers.totalServices.CZK", totalServicesCZK);

            long lastInvoicingPLN = sumForCountry.remove("overviewCustomers.totalLastInvoicing");
            long lastInvoicingCZK = (long) (lastInvoicingPLN * ratePLNtoCZK);
            sumForCountry.put("overviewCustomers.totalLastInvoicing.CZK", lastInvoicingCZK);

            sumForCountry.put("overviewCustomers.totalServices.PLN", totalServicesPLN);
            sumForCountry.put("overviewCustomers.totalLastInvoicing.PLN", lastInvoicingPLN);

            sumForCountry.put("exchangeRate.PLN_CZK", (long) (ratePLNtoCZK * 100.0));
        }
        return sumForCountry;
    }

    public Service getService(Long serviceId) {
        return sDao.get(serviceId);
    }

    public void insertService(Service service) {
        if (service.getCustomerId() != null) {
            // insert service in customers context
            Customer c = get(service.getCustomerId());
            // need to evict koz customer is transient all changes are auto persisted without audit
            dao.evict(c);
            // add service and persist it
            c.getServices().add(service);
            update(c);
        } else
            // no customer context found, insert separately
            sDao.save(service);
    }

    public void updateService(Service service) {
        boolean found = false;
        // when saving service always reset its status
        service.setStatus("INHERIT_FROM_CUSTOMER");
        if (service.getCustomerId() != null) {
            // update service in customers context
            Customer c = get(service.getCustomerId());
            // need to evict koz customer is transient all changes are auto persisted without audit
            dao.evict(c);
            // locate service by id
            ListIterator<Service> sIt = c.getServices().listIterator();
            while (sIt.hasNext()) {
                Service formerService = sIt.next();
                if (formerService.getId().equals(service.getId())) {
                    // replace service
                    log.debug("Updating service in customers context.");
                    sDao.evict(formerService);
                    sIt.set(service);
                    // persist change
                    update(c);
                    eventBus.publish(JsonPayload.builder().add("id", service.getId()).build(), Key.of("sis.serviceUpdated"));
                    found = true;
                    break;
                }
            }
        }
        if (!found)
            // no customer context change save separately
            sDao.save(service);
      networkService.kickUser(service.getId());
    }

    public void deleteService(Service service) {
        boolean found = false;
        if (service.getCustomerId() != null) {
            // update service in customers context
            Customer c = get(service.getCustomerId());
            // need to evict koz customer is transient all changes are auto persisted without audit
            dao.evict(c);
            // locate service by id
            ListIterator<Service> sIt = c.getServices().listIterator();
            while (sIt.hasNext()) {
                Service formerService = sIt.next();
                if (formerService.getId().equals(service.getId())) {
                    // delete service
                    log.debug("Deleting service in customers context.");
                    sIt.remove();
                    // persist change
                    update(c);
                    found = true;
                    break;
                }
            }
        }
        if (!found)
            // no customer context change remove separatelly
            sDao.remove(service);
    }

    @SuppressWarnings("unchecked")
    public List<Customer> getByExample(Customer customer, Service service) {
        // get Customers by example, can be null
        List<Customer> customers = getByExample(customer);
        if (service == null) {
            // if no service given that's just it
            log.debug("Filtering Customers only by Customer.");
            return customers;
        }
        // get Services by example
        List<Service> services = sDao.getByExample(service);
        log.debug("Services found: " + services.size());
        if (customers == null) {
            // only by service
            log.debug("Filtering Customers only by Service.");
            customers = getCustomers(services);
        } else {
            // by service and by customer
            log.debug("Filtering Customers by Customer AND by Service.");
            customers = ListUtils.intersection(customers, getCustomers(services));
        }
        return customers;
    }

    private List<Customer> getCustomers(List<Service> services) {
        if (services == null)
            return null;
        List<Customer> customers = new ArrayList<Customer>();
        // iterate services and collect their customers
        for (Service s : services)
            customers.add(get(s.getCustomerId()));
        return customers;
    }

    public void deactivateCandidates() {
        // get current time
        Date due = new Date();
        log.info("Deactivating candidates! (" + due.toString() + ")");
        // get all active customers
        List<Customer> customers = getAll();
        for (Customer customer : customers) {
            if (customer.isDeactivateCandidate(due)) {
                // detach the customer from hibernate session before change, we
                // are in transaction
                // without it customer audit does not work much well
                dao.evict(customer);
                // we have customer to deactivate, do it now!
                customer.getBilling().setIsActive(false);
                customer.getBilling().setStatus(BillingStatus.EXPIRED);
                update(customer);
                log.info("Customer (" + customer.getName() + ") DEACTIVATED");
            }
        }
        log.info("Deactivating customers FINISHED!");
    }

    public List<Bill> fetchBills(Customer customer) {
        return bDao.getByCustomer(customer);
    }

    public void updateSymbol(Customer customer) {
        if (customer.getSymbol() == null || "".equals(customer.getSymbol())) {
            // symbol is not set, define it according to country
            if (Country.PL.equals(customer.getContact().getAddress().getCountry())) {
                customer.setSymbol("PL-" + customer.getBilling().getVariableSymbol());
                log.debug("New Symbol composed :" + customer.getSymbol());
            } else {
                // CZ & SK symbol leave it empty
            }
        }
    }

    public void exportCustomersToInsert(List<Customer> customers, PrintWriter writer) {
        if (customers == null)
            return;
        if (writer == null)
            throw new IllegalArgumentException("Writer not initialized.");
        DateFormat dFmt = new SimpleDateFormat("yyyyMMddHHmmss");
        Locale locale = new Locale("pl");
        // write header
        String timeStamp = dFmt.format(new Date());
        String exportBy = MessagesUtils.getMessage("billExport.by", locale);
        String infoLine = MessagesUtils.getMessage("billExport.invoice.infoLine",
                new Object[]{timeStamp, exportBy}, locale);
        // headers
        writer.printf("[INFO]\r\n%s\r\n\r\n[NAGLOWEK]\n\"KONTRAHENCI\"\n\n[ZAWARTOSC]\n", infoLine);
        String customerName;
        String customerLongName;
        for (Customer customer : customers) {
            // append customer to output writer
            // skip silently non polish
            if (!customer.getContact().getAddress().getCountry().equals(
                    Country.PL))
                continue;
            // compose names
            if (customer.getName().length() > 50) {
                customerName = customer.getName().substring(0, 47) + "...";
            } else {
                customerName = customer.getName();
            }
            if (customer.getSupplementaryName() != null && !"".equals(customer.getSupplementaryName())) {
                customerLongName = customer.getName() + " - " + customer.getSupplementaryName();
            } else {
                customerLongName = customer.getName();
            }
            // symbol, names
            writer.printf("2,\"%s\",\"%s\",\"%s\",", escapeQuotes(customer.getSymbol()), escapeQuotes(customerName),
                    escapeQuotes(customerLongName));
            // address, dic, ico
            Address a = customer.getContact().getAddress();
            writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",,,\"%s\",,,,,,,,,,,,,,,", a.getCity(),
                    a.getPostalCode(), a.getStreet(), customer.getDIC(), customer.getPublicId(),
                    customer.getContact().getPhone(), customer.getContact().getEmail());
            // trailer
            writer.printf("\"Polska\",\"PL\",0");
            writer.println();
        }
    }

    public long nextOneTimeServiceId(Long customerId) {
        final Customer customer = dao.get(customerId);
        dao.evict(customer);
        ContractNo contract = null;
        if (customer.getServices().size() > 0) {
            contract = ServiceId.serviceId(customer.getServices().get(0).getId().intValue()).contractNo();
        } else {
            log.debug("falling back to contract number from variable symbol");
            final Integer variableSymbol = customer.getBilling().getVariableSymbol();
            if (variableSymbol == null)
                throw new IllegalStateException("customer without variable symbol encountered '" + customer.getId() + "'");
            int variable = variableSymbol;
            while(contract == null && variable != 0) {
                try {
                    contract = ContractNo.contractNo(variable);
                } catch (IllegalArgumentException e) {
                    variable /= 10000; // cut off last 4 digits, legacy magic
                }
            }
            if (contract == null)
                throw new IllegalStateException("cannot figure out contract no from variable symbol");
        }
        final Country country = customer.getContact().getAddress().getCountry();
        final ServiceId fist = ServiceId.firstOnetimeServiceId(country, contract);
        final ServiceId last = ServiceId.lastOnetimeServiceId(country, contract);
        Long lastId = sDao.findMaxIdInRange(fist.id(), last.id());
        long nextId =  lastId == null ? fist.id() : ServiceId.serviceId(lastId.intValue()).next().id();
        log.debug("next onetime service id is '" + nextId + "'");
        return nextId;
    }

    // setters

    private String escapeQuotes(String name) {
        return StringUtils.replace(name, "\"", "\"\"");
    }

    public void setBillDAO(BillDAO dao) {
        bDao = dao;
    }

    public void setCustomerDAO(CustomerDAO customerDAO) {
        dao = customerDAO;
    }

    public void setServiceDAO(ServiceDAO serviceDAO) {
        this.sDao = serviceDAO;
    }

    public void setHistoryManager(HistoryManager historyManager) {
        hmgr = historyManager;
    }

    public void setSettingManager(SettingManager settingManager) {
        settingMgr = settingManager;
    }

    public void setLabelDAO(LabelDAO labelDAO) {
        this.labelDAO = labelDAO;
    }

    public void setEventBus(final EventBus eventBus) {
        this.eventBus = eventBus;
    }

  public void setNetworkService(NetworkService networkService) {
    this.networkService = networkService;
  }
}
