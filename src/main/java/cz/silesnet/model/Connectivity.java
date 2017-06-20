package cz.silesnet.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;

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

  private String fBps = "M";

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
   */
  public Connectivity(Integer download, Integer upload, Boolean aggregated, Integer id) {
    this();
    fDownload = download;
    fUpload = upload;
  }

  // ~ Methods
  // ----------------------------------------------------------------

  public void setDownload(Integer download) {
    fDownload = download;
  }

  public Integer getDownload() {
    return fDownload;
  }

  public String getHistoricToString() {
    return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE).append(getDownload()).append(
        getUpload()).toString();
  }

  public void setUpload(Integer upload) {
    fUpload = upload;
  }

  public Integer getUpload() {
    return fUpload;
  }

  public String getBps() {
    return fBps;
  }

  public void setBps(String fBps) {
    this.fBps = fBps;
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

  public String getUnit() {
    return fBps + "bps";
  }

  public String getLinkSpeedText() {
    StringBuilder speed = new StringBuilder();
    if (fDownload != null) {
      speed.append(fDownload);
      if (fUpload != null)
        speed.append("/").append(fUpload);
      speed.append(" ").append(getUnit());
    }
    return speed.toString();
  }

}