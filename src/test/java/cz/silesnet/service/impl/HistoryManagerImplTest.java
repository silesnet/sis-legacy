package cz.silesnet.service.impl;

import static org.mockito.Mockito.*;
import junit.framework.TestCase;

import org.joda.time.DateTime;
import org.mockito.ArgumentCaptor;

import cz.silesnet.dao.HistoryItemDAO;
import cz.silesnet.service.HistoryManager;

public class HistoryManagerImplTest extends TestCase {

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
		ArgumentCaptor<DateTime> date = ArgumentCaptor.forClass(DateTime.class);
		when(dao.removeLoginHistoryOlderThan(date.capture())).thenReturn(2);
		hm
				.removeMonthsOldLoginHistory(HistoryManager.MIN_MONTHS_LOGIN_HISTORY_AGE);
		// TODO test the value
		System.out.println(date.getValue());
	}
}
