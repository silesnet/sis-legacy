package cz.silesnet.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

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
    private String name;
    private Integer download;
    private Integer upload;
    private String responsible;
    private String info;
    private Date periodFrom;
    private Date billingOn;

    public Service buildService(final int price) {
        if (!isNewContract() && isNewCustomer())
            throw new IllegalStateException("existing contract '" + serviceId(id).contractNo() + "', but customer id not set");
        final Service service = new Service();
        service.setId(id.longValue());
        service.setCustomerId(customerId.longValue());
        service.setPrice(price);
        service.setPeriod(new Period(periodFrom, null));
        service.setName(name);
        service.setAdditionalName(null);
        service.getConnectivity().setDownload(download);
        service.getConnectivity().setUpload(upload);
        service.getConnectivity().setBps("M");
        service.getConnectivity().setIsAggregated(false);
        service.getConnectivity().setAggregationId(null);
        service.setInfo(info);
        return service;
    }

    public boolean isNewContract() {
        return serviceId(id).orderNo() == 0;
    }

    public boolean isNewCustomer() {
        return customerId == null;
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


