package cz.silesnet.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.silesnet.model.enums.BillingStatus;
import cz.silesnet.model.enums.Frequency;
import cz.silesnet.utils.SearchUtils;

/**
 * Entity class to hold customers info.
 * 
 * @author Richard Sikora
 */
public class Customer extends Entity implements Historic {
    private static final long serialVersionUID = 2203444523758922808L;

    // FIXME should do it more elegant!!!
    // it depends on id value in db !!!
    private static final Long sHistoryTypeLabelId = Long.valueOf(41);
    private static final String[] sDiffExcludeFields = { "class$0", "serialVersionUID", "sHistoryTypeLabelId",
            "sDiffExcludeFields", "fId", "fHistoryId" };
    private Long fHistoryId;
    private String fContractNo;
    private String fName;
    private String fSupplementaryName;
    private String fPublicId;
    private String fDIC;
    private String fSymbol;
    private Contact fContact = new Contact();
    private List<Service> fServices = new ArrayList<Service>();
    private Billing fBilling = new Billing();
    private String fConnectionSpot;
    private String fInfo;
    private Date fInsertedOn;
    private Date fUpdated;
    private Date fSynchronized;

    public void setConnectionSpot(String connectionSpot) {
        fConnectionSpot = connectionSpot;
    }

    public String getConnectionSpot() {
        return fConnectionSpot;
    }

    public void setDIC(String dic) {
        fDIC = dic;
    }

    public String getDIC() {
        return fDIC;
    }

    public String getSymbol() {
        return fSymbol;
    }

    public void setSymbol(String symbol) {
        fSymbol = symbol;
    }

    public String[] getDiffExcludeFields() {
        return sDiffExcludeFields;
    }

    public void setHistoryId(Long historyId) {
        fHistoryId = historyId;
    }

    public Long getHistoryId() {
        return fHistoryId;
    }

    public Long getHistoryTypeLabelId() {
        return sHistoryTypeLabelId;
    }

    public void setInfo(String info) {
        fInfo = info;
    }

    public String getInfo() {
        return fInfo;
    }

    public void setName(String name) {
        fName = name;
    }

    public String getName() {
        return fName;
    }

    public void setPublicId(String publicId) {
        fPublicId = publicId;
    }

    public String getPublicId() {
        return fPublicId;
    }

    public void setServices(List<Service> services) {
        fServices = services;
    }

    public List<Service> getServices() {
        return fServices;
    }

    public Date getInsertedOn() {
        return fInsertedOn;
    }

    public void setInsertedOn(Date insertedOn) {
        fInsertedOn = insertedOn;
    }

    public Date getUpdated() {
        return fUpdated;
    }

    public void setUpdated(Date updated) {
        fUpdated = updated;
    }

    public Date getSynchronized() {
        return fSynchronized;
    }

    public void setSynchronized(Date synchronized1) {
        fSynchronized = synchronized1;
    }

    public Integer getOverallDownload() {
        List<Service> sList = getConnectivityServices();
        Integer download = 0;
        // sum services download
        for (Service s : sList) {
            if (s.getConnectivity().getDownload() != null)
                download += s.getConnectivity().getDownload();
        }
        return download;
    }

    public Integer getOverallUpload() {
        List<Service> sList = getConnectivityServices();
        Integer upload = 0;
        // sum services upload
        for (Service s : sList) {
            if (s.getConnectivity().getUpload() != null)
                upload += s.getConnectivity().getUpload();
        }
        return upload;
    }

    public Integer getOverallPrice() {
        List<Service> sList = getServices();
        // report zero to non billed customers
        if (!BillingStatus.INVOICE.equals(getBilling().getStatus()))
            return 0;
        Integer price = 0;
        // sum services price
        for (Service s : sList) {
            if (!Frequency.ONE_TIME.equals(s.getFrequency()) && (s.getPrice() != null))
                price += s.getPrice();
        }
        return price;
    }

    private List<Service> getConnectivityServices() {
        // filter service for monthy connectivity
        ArrayList<Service> cServices = new ArrayList<Service>();
        for (Service s : getServices()) {
            if (Frequency.MONTHLY.equals(s.getFrequency()) && s.getIsConnectivity() && (s.getConnectivity() != null))
                cServices.add(s);
        }
        return cServices;
    }

    public String getServicesInfo() {
        String comm = "";
        if (getBilling().getShire() != null)
            comm = "[" + getBilling().getShire().getName().substring(0, 3) + "]";
        if (getServices().size() == 0)
            return comm;
        return getServices().size() > 1 ? getServices().get(0).getShortInfo() + " (+�) " + comm : getServices().get(0)
                .getShortInfo()
                + " " + comm;
    }

    public String getContractNo() {
        return fContractNo;
    }

    public void setContractNo(String contractNo) {
        fContractNo = contractNo;
    }

    public String getSupplementaryName() {
        return fSupplementaryName;
    }

    public void setSupplementaryName(String supplementaryName) {
        fSupplementaryName = supplementaryName;
    }

    public Billing getBilling() {
        return fBilling;
    }

    public void setBilling(Billing billing) {
        fBilling = billing;
    }

    public Contact getContact() {
        return fContact;
    }

    public void setContact(Contact contact) {
        fContact = contact;
    }

    public String getExportPublicId() {
        if (getPublicId() == null)
            return null;
        String noSlash = getPublicId().replace("/", "");
        return noSlash.length() > 8 ? noSlash.substring(0, 8) : noSlash;
    }

    public String getExportName() {
        // return cripled first two words from customers name
        String[] words = getName().split(" ");
        StringBuffer exportName = new StringBuffer(SearchUtils.translate(words[0]));
        if (words.length > 1)
            exportName.append("_" + SearchUtils.translate(words[1]));
        return exportName.toString();
    }

    public boolean isDeactivateCandidate(Date due) {
        // throw exception if due not set
        if (due == null)
            throw new NullPointerException("Due date not set!");
        // skip suspensed customers
        if (BillingStatus.CEASE.equals(getBilling().getStatus()))
            return false;
        // skip no service customers
        if (getServices().size() == 0)
            return false;
        // check services
        Date maxTo = null;
        Date to = null;
        for (Service service : getServices()) {
            // skip if one time service found
            if (Frequency.ONE_TIME.equals(service.getFrequency()))
                return false;
            // skip if service without stop date found
            to = service.getPeriod().getTo();
            if (to == null)
                return false;
            // update max services to date
            if (maxTo != null) {
                if (maxTo.before(to))
                    maxTo = to;
            } else {
                maxTo = to;
            }
        }
        // skip if we have active service till due
        if (!maxTo.before(due))
            return false;
        // skip if billing lastlyBilled not set
        if (getBilling().getLastlyBilled() == null)
            return false;
        // skip if we have inactive service, not yet fully billed
        if (maxTo.after(getBilling().getLastlyBilled()))
            return false;
        // we made it here, so here are conditions met (review):
        // 1. customer has BillingStatus <> CEASE
        // 2. customer has at least one service
        // 3. customer has no ONE_TIME frequency service
        // 4. customer has all services with period.to date set
        // 5. maxTo < due
        // 6. customer has been billed, lastlyBilled != null
        // 7. maxTo <= lastlyBilled
        // => customer is deactivate candidate
        return true;
    }

    public boolean isSpsSynchronized() {
        if ((getSynchronized() != null) && (getUpdated() != null) && (getUpdated().before(getSynchronized()))) {
            return true;
        }
        return false;
    }
}
