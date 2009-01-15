package cz.silesnet.service.impl;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.GenericValidator;
import org.springframework.util.Assert;

import cz.silesnet.dao.BillDAO;
import cz.silesnet.dao.CustomerDAO;
import cz.silesnet.dao.ServiceDAO;
import cz.silesnet.model.Address;
import cz.silesnet.model.Bill;
import cz.silesnet.model.Contact;
import cz.silesnet.model.Customer;
import cz.silesnet.model.Service;
import cz.silesnet.model.enums.BillingStatus;
import cz.silesnet.model.enums.Country;
import cz.silesnet.service.CustomerManager;
import cz.silesnet.service.HistoryManager;
import cz.silesnet.service.SettingManager;
import cz.silesnet.utils.MessagesUtils;
import cz.silesnet.utils.SearchUtils;

/**
 * Concrete implementation of CustomerManager usig CustomerDAO for CRUD
 * operations.
 * 
 * @author Richard Sikora
 */
public class CustomerManagerImpl implements CustomerManager {

    // ~ Instance fields
    // --------------------------------------------------------

    protected final Log log = LogFactory.getLog(getClass());
    private CustomerDAO dao;
    private BillDAO bDao;
    private ServiceDAO sDao;
    private HistoryManager hmgr;
    private SettingManager settingMgr;

    // ~ Methods
    // ----------------------------------------------------------------

    public void setBillDAO(BillDAO dao) {
        bDao = dao;
    }

    public List<Customer> getAll() {
        return dao.getAll();
    }

    public List<Customer> getByExample(Customer customer) {
        return dao.getByExample(customer);
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

    public void delete(Customer customer) {
        // delete customers bills
        List<Bill> bills = fetchBills(customer);
        bDao.removeAll(bills);
        // delete customers history
        hmgr.deleteHistory(customer);
        // delete customer
        dao.remove(customer);
    }

    public Customer get(Long customerId) {
        return dao.get(customerId);
    }

    public void insert(Customer customer) {
        // make sure we will have insert
        customer.setId(null);
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
        // Customer formerCustomer = dao.load(customer.getId());
        // detatch former customer from hibernate session
        dao.evict(formerCustomer);

        // do some validity checks
        Assert.notNull(formerCustomer.getHistoryId(), "Persisted customer without historyId.");
        Assert.isTrue(formerCustomer.getHistoryId().equals(customer.getHistoryId()),
                "Outside (illegal) historyId change.");

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

    private boolean isSpsUpdated(Customer formerCustomer, Customer customer) {
        // general
        if (changedString(formerCustomer.getContractNo(), customer.getContractNo()))
            return true;
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

    public Map<String, String> getOverview(Country c) {
        Map<String, String> overview = new LinkedHashMap<String, String>();
        // get decimal formmater
        DecimalFormat f = new DecimalFormat();
        // total customers count
        overview.put("overviewCustomers.totalCustomers", f.format(dao.getTotalCustomers(c)));
        // total download
        overview.put("overviewCustomers.totalDownload", f.format(sDao.getTotalDownload(c)) + " "
                + MessagesUtils.getMessage("netSpeed.label"));
        // total upload
        overview.put("overviewCustomers.totalUpload", f.format(sDao.getTotalUpload(c)) + " "
                + MessagesUtils.getMessage("netSpeed.label"));

        // total price
        // get country id
        int cId = c != null ? c.getId() : 0;
        // get exchange rate
        double ratePLN_CZK = settingMgr.getDouble("exchangeRate.PLN_CZK", Double.valueOf(7.5));
        // count totals
        int totalCZ = sDao.getTotalPrice(Country.CZ);
        int totalPL = sDao.getTotalPrice(Country.PL);
        int totalPL_CZK = (int) (totalPL * ratePLN_CZK);
        int total = totalPL_CZK + totalCZ;

        switch (cId) {
        case 0:
            // overall, no country
            overview.put("overviewCustomers.totalPrice.CZK", f.format(total) + " "
                    + MessagesUtils.getMessage("money.label.cz"));
            break;
        case 10:
            // CZ
            overview.put("overviewCustomers.totalPrice.CZK", f.format(totalCZ) + " "
                    + MessagesUtils.getMessage("money.label.cz"));
            break;
        case 20:
            // PL
            overview.put("overviewCustomers.totalPrice.CZK", f.format(totalPL_CZK) + " "
                    + MessagesUtils.getMessage("money.label.cz"));
            overview.put("overviewCustomers.totalPrice.PLN", f.format(totalPL) + " "
                    + MessagesUtils.getMessage("money.label.pl"));
            overview.put("exchangeRate.PLN_CZK", Double.valueOf(ratePLN_CZK).toString());
            break;
        }
        return overview;
    }

    public void updateAll(List<Customer> customers) {
        // it does not go throught history audit man, so do it by hand
        // dao.updateAll(customers);
        for (Customer c : customers)
            update(c);
    }

    public void exportCusotmersToWinDuo(List<Customer> customers, PrintWriter writer) {
        if (customers == null)
            return;
        if (writer == null)
            throw new IllegalArgumentException("Writer not initialized.");
        for (Customer c : customers) {
            // append customer to output writer
            // skip silently non czechs
            if (!c.getContact().getAddress().getCountry().equals(Country.CZ))
                continue;
            // header
            writer.printf("H:128\t");
            // ICO, DIC, Name, SupplementaryName
            writer.printf("%s\t%s\t%s\t%s\t", sanate(c.getExportPublicId()), sanate(c.getDIC()), c.getName(), sanate(c
                    .getSupplementaryName()));
            // Street, City, PSC, Country
            writer.printf("%s\t%s\t%s\t%s\t", c.getContact().getAddress().getStreet(), c.getContact().getAddress()
                    .getCity(), sanate(c.getContact().getAddress().getPostalCode()), MessagesUtils.getMessage(c
                    .getContact().getAddress().getCountry().getName(), new Locale("cs")));
            // Account, Bank, Person
            writer.printf("\t\t%s\t", sanate(c.getContact().getName()));
            // Predcisli, Phone1, Phone2, Fax
            writer.printf("\t%s\t%s\t\t", c.getContact().getPhone1(), c.getContact().getPhone2());
            // leave rest empty, 8 Codes
            writer.printf("0\t0\t0\t0\t0\t0\t0\t0\t");
            // 8 Flags
            writer.printf("N\tN\tN\tN\tN\tN\tN\tN\t");
            // 2 Fields
            writer.printf("0\t0\t");
            // Note
            if (GenericValidator.isBlankOrNull(c.getBilling().getDeliverCopyEmail()))
                writer.printf("%s", sanate(c.getContact().getEmail()));
            else
                writer.printf("%s,%s", sanate(c.getContact().getEmail()), c.getBilling().getDeliverCopyEmail());
            // has to have \n
            writer.println();
        }
    }

    private String sanate(String s) {
        return s == null ? "" : s;
    }

    public Service getService(Long serviceId) {
        return sDao.get(serviceId);
    }

    public void insertService(Service service) {
        if (service.getCustomerId() != null) {
            // insert service in customers context
            Customer c = get(service.getCustomerId());
            // need to evic koz customer is transient all chages are auto
            // persisted without audit
            dao.evict(c);
            // add service and persist it
            c.getServices().add(service);
            update(c);
        } else
            // no customer context found, insert separatelly
            sDao.save(service);
    }

    public void updateService(Service service) {
        boolean found = false;
        if (service.getCustomerId() != null) {
            // update service in customers context
            Customer c = get(service.getCustomerId());
            // need to evic koz customer is transient all chages are auto
            // persisted without audit
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
                    found = true;
                    break;
                }
            }
        }
        if (!found)
            // no customer context change save separatelly
            sDao.save(service);
    }

    public void deleteService(Service service) {
        boolean found = false;
        if (service.getCustomerId() != null) {
            // update service in customers context
            Customer c = get(service.getCustomerId());
            // need to evic koz customer is transient all chages are auto
            // persisted without audit
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
                // PL symbol definition
                if (customer.getId() == null) {
                    // not persisted customer, persist him first to have id
                    dao.save(customer);
                    dao.evict(customer);
                }
                // compose Symbol from crippled name and id
                String shortName = StringUtils.replaceChars((customer.getName() + "XXX").substring(0, 3), SearchUtils
                        .getFromChars(), SearchUtils.getToChars());
                customer.setSymbol(shortName.toUpperCase() + "-" + customer.getId().toString());
                log.debug("New Symbol composed :" + customer.getSymbol());
            } else {
                // CZ & SK symbol definition, use ICO
                customer.setSymbol(customer.getPublicId());
                log.debug("New Symbol composed :" + customer.getSymbol());
            }
        }
    }

    public void exportCusotmersToInsert(List<Customer> customers, PrintWriter writer) {
        if (customers == null)
            return;
        if (writer == null)
            throw new IllegalArgumentException("Writer not initialized.");
        DateFormat dFmt = new SimpleDateFormat("yyyyMMddHHmmss");
        Locale locale = new Locale("pl");
        // write header
        String timeStamp = dFmt.format(new Date());
        String exportBy = MessagesUtils.getMessage("billExport.by", locale);
        String infoLine = MessagesUtils.getMessage("billExport.invoice.infoLine", new Object[] { timeStamp, exportBy },
                locale);
        // headers
        writer.printf("[INFO]\r\n%s\r\n\r\n[NAGLOWEK]\n\"KONTRAHENCI\"\n\n[ZAWARTOSC]\n", infoLine);
        String customerName = null;
        String customerLongName = null;
        for (Customer customer : customers) {
            // append customer to output writer
            // skip silently non polish
            if (!customer.getContact().getAddress().getCountry().equals(Country.PL))
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
            writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",,,\"%s\",,,,,,,,,,,,,,,", a.getCity(), a
                    .getPostalCode(), a.getStreet(), customer.getDIC(), customer.getPublicId(), customer.getContact()
                    .getPhone(), customer.getContact().getEmail());
            // trailer
            writer.printf("\"Polska\",\"PL\",0");
            writer.println();
        }
    }

    private String escapeQuotes(String name) {
        return StringUtils.replace(name, "\"", "\"\"");
    }

}