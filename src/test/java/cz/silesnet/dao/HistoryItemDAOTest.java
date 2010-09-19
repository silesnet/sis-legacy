package cz.silesnet.dao;

import cz.silesnet.model.HistoryItem;
import cz.silesnet.model.Label;
import cz.silesnet.utils.SecurityUtils;
import org.joda.time.DateTime;
import org.testng.annotations.Test;
import org.unitils.dbunit.annotation.DataSet;

import java.util.ArrayList;
import java.util.Date;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.is;

@DataSet("/cz/silesnet/dao/HistoryItemDAOTest.xml")
public abstract class HistoryItemDAOTest extends DaoTestSupport<HistoryItemDAO> {

  private Label label;

  private Label getLabel() {
    if (label == null) {
      label = new Label();
      label.setId(16L);
      label.setParentId(10L);
      label.setName("TestHistory");
    }
    return label;
  }

  @Test
  public void testGetHistory() {
    Long historyId = dao.getNewHistoryId();

    HistoryItem hi = new HistoryItem();
    hi.setHistoryId(historyId);
    hi.setTimeStamp(new Date());
    hi.setUser(SecurityUtils.getUser());
    hi.setHistoryTypeLabel(getLabel());
    hi.setFieldName("fSomeField1");
    hi.setOldValue("old field value1");
    hi.setNewValue("new field value1");
    dao.saveHistoryItem(hi);

    hi = new HistoryItem();
    hi.setHistoryId(historyId);
    hi.setTimeStamp(new Date());
    hi.setUser(SecurityUtils.getUser());
    hi.setHistoryTypeLabel(getLabel());
    hi.setFieldName("fSomeField2");
    hi.setOldValue("old field value2");
    hi.setNewValue("new field value2");
    dao.saveHistoryItem(hi);

    hi = new HistoryItem();
    hi.setHistoryId(historyId);
    hi.setTimeStamp(new Date());
    hi.setUser(SecurityUtils.getUser());
    hi.setHistoryTypeLabel(getLabel());
    hi.setFieldName("fSomeField3");
    hi.setOldValue("old field value3");
    hi.setNewValue("new field value3");
    dao.saveHistoryItem(hi);

    log.debug("Retrieving history of id: " + historyId);

    ArrayList<HistoryItem> history = (ArrayList<HistoryItem>) dao.getHistory(historyId);

    assertThat(history, is(not(nullValue())));

    log.debug("Retrieved history" + history);

    // clean up
    for (HistoryItem h : history)
      dao.removeHistoryItem(h);
  }

  @Test
  public void testGetNewHistoryId() {
    Long newHistoryId = dao.getNewHistoryId();
    log.debug("New historyId returned " + newHistoryId);
  }

  @Test
  public void testSaveRemoveHistoryItem() {

    HistoryItem hi = new HistoryItem();
    hi.setHistoryId(1L);
    hi.setTimeStamp(new Date());
    hi.setUser(SecurityUtils.getUser());
    hi.setHistoryTypeLabel(getLabel());
    hi.setFieldName("fSomeField");
    hi.setOldValue("old field value");
    hi.setNewValue("new field value");

    log.debug("Persistnig history item: " + hi);
    dao.saveHistoryItem(hi);

    log.debug("Persisted history item: " + hi);

    log.debug("Deleting history item: " + hi);
    dao.removeHistoryItem(hi);
  }

  @Test
  public void testRemoveLoginHistoryOlderThan() throws Exception {
    int removedCount = dao.removeLoginHistoryOlderThan(new DateTime("2009-08-13"));
    assertThat(removedCount, is(2)); // see details in HistoryItemDAOTest.xml
  }
}