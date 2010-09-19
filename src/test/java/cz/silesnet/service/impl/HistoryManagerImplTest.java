package cz.silesnet.service.impl;

import cz.silesnet.dao.HistoryItemDAO;
import cz.silesnet.service.HistoryManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class HistoryManagerImplTest {

  private static final int ITEMS_REMOVED = 2;

  private static final int MONTHS = 10;

  protected final Log log = LogFactory.getLog(getClass());

  private HistoryManagerImpl hm;

  private HistoryItemDAO dao;

  @BeforeMethod
  protected void setUp() throws Exception {
    hm = new HistoryManagerImpl();
    dao = mock(HistoryItemDAO.class);
    hm.setHistoryItemDAO(dao);
  }

  @AfterMethod
  protected void tearDown() throws Exception {
    hm = null;
    dao = null;
  }

  @Test
  public void testRemoveMonthsOldLoginHistory() {
    when(dao.removeLoginHistoryOlderThan(isA(DateTime.class))).thenReturn(ITEMS_REMOVED);
    assertThat(hm.removeMonthsOldLoginHistory(MONTHS), is(ITEMS_REMOVED));
  }

  @Test
  public void testRemoveMonthsOldLoginHistoryDate() {
    hm.removeMonthsOldLoginHistory(MONTHS);
    ArgumentCaptor<DateTime> removeFrom = ArgumentCaptor.forClass(DateTime.class);
    verify(dao).removeLoginHistoryOlderThan(removeFrom.capture());
    Period period = new Period(removeFrom.getValue(), new DateTime());
    assertThat(period.getMonths(), is(MONTHS));
  }

  @Test
  public void testRemoveMonthsOldLoginHistoryIllegalMonths() {
    try {
      hm.removeMonthsOldLoginHistory(HistoryManager.MIN_MONTHS_LOGIN_HISTORY_AGE - 1);
      fail();
    } catch (AssertionError e) {
      // expected
    }
  }

}
