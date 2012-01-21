package cz.silesnet.model;

import cz.silesnet.model.enums.BillingStatus;
import cz.silesnet.model.enums.Frequency;
import cz.silesnet.model.enums.InvoiceFormat;
import cz.silesnet.utils.Dates;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.util.StringUtils;

import java.util.Calendar;
import java.util.Date;

import static cz.silesnet.model.ServiceId.serviceId;

/**
 * User: admin
 * Date: 8.1.12
 * Time: 18:49
 */
public class ServiceBlueprint {
    private Integer id;
    private Integer customerId;
    private Integer price;
    private String name;
    private Integer download;
    private Integer upload;
    private String responsible;
    private String info;
    private Date periodFrom;
    private Date billingOn;
    private boolean newCustomerCreated = false;

    public Customer initializeNewCustomer(final Label responsible) {
        final Customer customer = new Customer();
        final ServiceId serviceId = serviceId(id);
        if (serviceId.orderNo() != 0)
            throw new IllegalStateException("cannot create customer when the service '" + serviceId + "' is not first in contract");
        final ContractNo contractNo = serviceId.contractNo();
        final String uniqueFoo = "?" + contractNo;
        customer.setName(uniqueFoo);
        customer.setContractNo(contractNo.toString());
        customer.setPublicId(contractNo.toString());
        customer.getContact().getAddress().setStreet(uniqueFoo);
        customer.getContact().getAddress().setCity(uniqueFoo);
        customer.getContact().getAddress().setPostalCode(uniqueFoo);
        customer.getContact().getAddress().setCountry(serviceId.country());
        final Billing billing = customer.getBilling();
        billing.setLastlyBilled(calculateLastlyBilled(periodFrom));
        billing.setFrequency(Frequency.MONTHLY);
        billing.setIsBilledAfter(false);
        billing.setDeliverByMail(false);
        billing.setDeliverByEmail(true);
        billing.setFormat(InvoiceFormat.LINK);
        billing.setDeliverSigned(false);
        billing.setResponsible(responsible);
        billing.setIsActive(true);
        billing.setStatus(BillingStatus.INVOICE);
        billing.setVariableSymbol(contractNo.value());
        customer.setInfo(info);
        customer.setInsertedOn(new Date());
        newCustomerCreated = true;
        return customer;
    }

    public Service buildService(final Customer customer) {
        checkIfCanBuild(customer);
        final Service service = new Service();
        service.setId(id.longValue());
        service.setCustomerId(customer.getId());
        service.setPrice(price);
        service.setFrequency(Frequency.MONTHLY);
        service.setPeriod(new Period(periodFrom, null));
        service.setName(name);
        service.setAdditionalName(null);
        service.getConnectivity().setDownload(download);
        service.getConnectivity().setUpload(upload);
        if ((download != null && download == 512) || (upload != null && upload == 512))
            // EXCEPTION for specific PL service
            service.getConnectivity().setBps("k");
        else
            service.getConnectivity().setBps("M");
        service.getConnectivity().setIsAggregated(false);
        service.getConnectivity().setAggregationId(null);
        service.setInfo(info);
        return service;
    }

    public Customer imprintNewServiceOn(final Customer customer) {
        customer.getBilling().setIsActive(true);
        final ServiceId serviceId = serviceId(id);
        if (!isNewCustomer() && isNewContract()) {
            customer.setContractNo(appendContractNo(customer.getStoredContractNo(), serviceId.contractNo()));
        }
        return customer;
    }

    private String appendContractNo(final String base, final ContractNo contractNo) {
        return StringUtils.hasText(base) ? base + ", " + contractNo.toString() : contractNo.toString();
    }

    private Date calculateLastlyBilled(final Date from) {
        final Period period = Frequency.MONTHLY.periodFor(from);
        final Calendar first = Dates.calendarWithZeroTimeFrom(period.getFrom());
        final Calendar last = (Calendar) first.clone();
        last.add(Calendar.DATE, -1);
        return last.getTime();
    }

    private void checkIfCanBuild(final Customer customer) {
        final ServiceId serviceId = serviceId(id);
        if (customer == null)
            throw new IllegalStateException("customer cannot be null");
        if (customer.getId() == null || customer.getId() == 0)
            throw new IllegalStateException("customer id cannot be zero or null");
        if (id == null)
            throw new IllegalStateException("service id cannot be null");
        if (!isNewCustomer() && customer.getId() != customerId.longValue())
            throw new IllegalArgumentException("customer id must match service blueprint customer id");
        if (price == null)
            throw new IllegalStateException("service price cannot be null");
        if (periodFrom == null)
            throw new IllegalStateException("service starting period cannot be null");
        if (name == null)
            throw new IllegalStateException("service name cannot be null");
        if (!isNewContract() && isNewCustomer())
            throw new IllegalStateException("existing contract '" + serviceId.contractNo() + "', but customer id not set");
        if (!customer.getContact().getAddress().getCountry().equals(serviceId.country()))
            throw new IllegalStateException("cannot add service '" + serviceId.country() + "' to the customer '" +
                    customer.getContact().getAddress().getCountry() + "' from different country");
    }

    public boolean isNewContract() {
        return serviceId(id).orderNo() == 0;
    }

    public boolean isNewCustomer() {
        return customerId == null || customerId == 0;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDownload() {
        return download;
    }

    public void setDownload(Integer download) {
        this.download = download;
    }

    public Integer getUpload() {
        return upload;
    }

    public void setUpload(Integer upload) {
        this.upload = upload;
    }

    public String getResponsible() {
        return responsible;
    }

    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Date getPeriodFrom() {
        return periodFrom;
    }

    public void setPeriodFrom(Date periodFrom) {
        this.periodFrom = periodFrom;
    }

    public Date getBillingOn() {
        return billingOn;
    }

    public void setBillingOn(Date billingOn) {
        this.billingOn = billingOn;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public boolean isNewCustomerCreated() {
        return newCustomerCreated;
    }

    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this,
                ToStringStyle.MULTI_LINE_STYLE);
    }
}


