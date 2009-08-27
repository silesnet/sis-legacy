package cz.silesnet.service.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.mockito.ArgumentCaptor;

import cz.silesnet.dao.HistoryItemDAO;
import cz.silesnet.service.HistoryManager;

public class HistoryManagerImplTest extends TestCase {

    private static final int ITEMS_REMOVED = 2;

    private static final int MONTHS = 10;

    protected final Log log = LogFactory.getLog(getClass());

    private HistoryManagerImpl hm;

    private HistoryItemDAO dao;

    protected void setUp() throws Exception {
        super.setUp();
        hm = new HistoryManagerImpl();
        dao = mock(HistoryItemDAO.class);
        hm.setHistoryItemDAO(dao);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        hm = null;
        dao = null;
    }

    public void testRemoveMonthsOldLoginHistory() {
        when(dao.removeLoginHistoryOlderThan(isA(DateTime.class))).thenReturn(
                ITEMS_REMOVED);
        assertThat(hm.removeMonthsOldLoginHistory(MONTHS), is(ITEMS_REMOVED));
    }

    public void testRemoveMonthsOldLoginHistoryDate() {
        hm.removeMonthsOldLoginHistory(MONTHS);
        ArgumentCaptor<DateTime> removeFrom = ArgumentCaptor
                .forClass(DateTime.class);
        verify(dao).removeLoginHistoryOlderThan(removeFrom.capture());
        Period period = new Period(removeFrom.getValue(), new DateTime());
        assertThat(period.getMonths(), is(MONTHS));
    }

    public void testRemoveMonthsOldLoginHistoryIllegalMonths() {
        try {
            hm
                    .removeMonthsOldLoginHistory(HistoryManager.MIN_MONTHS_LOGIN_HISTORY_AGE - 1);
            fail();
        } catch (AssertionError e) {
            // expected
        }
    }

}
