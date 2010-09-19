package cz.silesnet.model;

import cz.silesnet.utils.MessagesUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import java.util.Date;

/**
 * History of changes class. Object of this class hold one modification to
 * reffered object.
 *
 * @author Richard Sikora
 */
public class HistoryItem extends Entity {

  // ~ Static fields/initializers
  // ---------------------------------------------

  private static final long serialVersionUID = -7246797505077022816L;

  // ~ Instance fields
  // --------------------------------------------------------

  private Long fHistoryId;

  private Label fHistoryTypeLabel;

  private User fUser;

  private Date fTimeStamp;

  private String fFieldName;

  private String fOldValue;

  private String fNewValue;

  // ~ Methods
  // ----------------------------------------------------------------

  public void setFieldName(String fieldName) {
    fFieldName = fieldName;
  }

  public String getFieldName() {
    return fFieldName;
  }

  public void setHistoryId(Long historyId) {
    fHistoryId = historyId;
  }

  public Long getHistoryId() {
    return fHistoryId;
  }

  public void setHistoryTypeLabel(Label historyTypeLabel) {
    fHistoryTypeLabel = historyTypeLabel;
  }

  public Label getHistoryTypeLabel() {
    return fHistoryTypeLabel;
  }

  public String getLogoutTime() {
    Long logoutTime = null;

    try {
      logoutTime = Long.valueOf(fNewValue);
    }
    catch (NumberFormatException e) {
    }

    String logoutStr = null;

    if (logoutTime != null) {
      // there was integer there, count date from it and return it
      Date logoutDate = new Date(logoutTime);
      logoutStr = DateFormatUtils.format(logoutDate,
          "dd.MM.yyyy HH:mm:ss");
    } else
      // give it as message
      logoutStr = MessagesUtils.getMessage(fNewValue);

    return logoutStr;
  }

  public Long getLogoutOrderValue() {
    Long logout = null;
    try {
      logout = Long.valueOf(fNewValue);
    }
    catch (NumberFormatException e) {
    }
    return logout;
  }

  public void setNewValue(String newValue) {
    fNewValue = newValue;
  }

  public String getNewValue() {
    return fNewValue;
  }

  public void setOldValue(String oldValue) {
    fOldValue = oldValue;
  }

  public String getOldValue() {
    return fOldValue;
  }

  public void setTimeStamp(Date timeStamp) {
    fTimeStamp = timeStamp;
  }

  public Date getTimeStamp() {
    return fTimeStamp;
  }

  public void setUser(User user) {
    fUser = user;
  }

  public User getUser() {
    return fUser;
  }

  public Long getCustomerId() {
    Long id = null;
    int commaIndex = getFieldName().indexOf(',');
    if (commaIndex > 0) {
      try {
        id = Long.valueOf(getFieldName().substring(0, commaIndex));
      }
      catch (NumberFormatException e) {
      }
    }
    return id;
  }

  public String getCustomerName() {
    int commaIndex = getFieldName().indexOf(',');
    return commaIndex >= 0 ? getFieldName().substring(commaIndex + 1) : "";
  }
}