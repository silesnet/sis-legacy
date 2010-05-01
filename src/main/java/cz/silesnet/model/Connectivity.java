package cz.silesnet.model;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Class that holds connectivity info of service.
 * 
 * @author Richard Sikora
 */
public class Connectivity implements HistoricToString, Serializable {

  // ~ Instance fields
  // --------------------------------------------------------

  private static final long serialVersionUID = -2833510950229610422L;

  private Integer fDownload;

  private Integer fUpload;

  private Boolean fIsAggregated = false;

  private Integer fAggregationId = 0;

  // ~ Constructors
  // -----------------------------------------------------------

  public Connectivity() {
    super();
  }

  /**
   * @param download
   * @param upload
   */
  public Connectivity(Integer download, Integer upload) {
    this(download, upload, false, 0);
  }

  /**
   * @param download
   * @param upload
   * @param aggregated
   * @param id
   */
  public Connectivity(Integer download, Integer upload, Boolean aggregated, Integer id) {
    this();
    fDownload = download;
    fUpload = upload;
    fIsAggregated = aggregated;
    fAggregationId = id;
  }

  // ~ Methods
  // ----------------------------------------------------------------

  public void setAggregationId(Integer aggregationId) {
    fAggregationId = aggregationId;
  }

  public Integer getAggregationId() {
    return fAggregationId;
  }

  public void setDownload(Integer download) {
    fDownload = download;
  }

  public Integer getDownload() {
    return fDownload;
  }

  public String getHistoricToString() {
    return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE).append(getDownload()).append(
        getUpload()).append(getIsAggregated()).append(getAggregationId()).toString();
  }

  public void setIsAggregated(Boolean isAggregated) {
    fIsAggregated = isAggregated;
  }

  public Boolean getIsAggregated() {
    return fIsAggregated;
  }

  public void setUpload(Integer upload) {
    fUpload = upload;
  }

  public Integer getUpload() {
    return fUpload;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
  }

  @Override
  public boolean equals(Object o) {
    return EqualsBuilder.reflectionEquals(this, o);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

}