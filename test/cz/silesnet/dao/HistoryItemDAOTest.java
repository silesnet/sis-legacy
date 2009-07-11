package cz.silesnet.dao;

import cz.silesnet.model.HistoryItem;

import cz.silesnet.utils.SecurityUtils;

import java.util.ArrayList;
import java.util.Date;

public class HistoryItemDAOTest extends BaseDAOTestCase {

	// ~ Methods
	// ----------------------------------------------------------------

	public void testGetHistory() {
		HistoryItemDAO dao = (HistoryItemDAO) ctx.getBean("historyItemDAO");
		LabelDAO ldao = (LabelDAO) ctx.getBean("labelDAO");

		Long historyId = dao.getNewHistoryId();

		HistoryItem hi = new HistoryItem();
		hi.setHistoryId(historyId);
		hi.setTimeStamp(new Date());
		hi.setUser(SecurityUtils.getUser());
		hi.setHistoryTypeLabel(ldao.getLabelById(Long.valueOf(16)));
		hi.setFieldName("fSomeField1");
		hi.setOldValue("old field value1");
		hi.setNewValue("new field value1");
		dao.saveHistoryItem(hi);

		hi.setId(null);
		hi.setHistoryId(historyId);
		hi.setTimeStamp(new Date());
		hi.setUser(SecurityUtils.getUser());
		hi.setHistoryTypeLabel(ldao.getLabelById(Long.valueOf(16)));
		hi.setFieldName("fSomeField2");
		hi.setOldValue("old field value2");
		hi.setNewValue("new field value2");
		dao.saveHistoryItem(hi);

		hi.setId(null);
		hi.setHistoryId(historyId);
		hi.setTimeStamp(new Date());
		hi.setUser(SecurityUtils.getUser());
		hi.setHistoryTypeLabel(ldao.getLabelById(Long.valueOf(16)));
		hi.setFieldName("fSomeField3");
		hi.setOldValue("old field value3");
		hi.setNewValue("new field value3");
		dao.saveHistoryItem(hi);

		log.debug("Retrieving history of id: " + historyId);

		ArrayList<HistoryItem> history = (ArrayList<HistoryItem>) dao
				.getHistory(historyId);

		assertNotNull(history);

		log.debug("Retrieved history" + history);

		// clean up
		for (HistoryItem h : history)
			dao.removeHistoryItem(h);
	}

	public void testGetNewHistoryId() {
		HistoryItemDAO dao = (HistoryItemDAO) ctx.getBean("historyItemDAO");

		Long newHistoryId = dao.getNewHistoryId();
		log.debug("New historyId returned " + newHistoryId);
	}

	public void testSaveRemoveHistoryItem() {
		HistoryItemDAO dao = (HistoryItemDAO) ctx.getBean("historyItemDAO");
		LabelDAO ldao = (LabelDAO) ctx.getBean("labelDAO");

		HistoryItem hi = new HistoryItem();
		hi.setHistoryId(Long.valueOf(1));
		hi.setTimeStamp(new Date());
		hi.setUser(SecurityUtils.getUser());
		hi.setHistoryTypeLabel(ldao.getLabelById(Long.valueOf(16)));
		hi.setFieldName("fSomeField");
		hi.setOldValue("old field value");
		hi.setNewValue("new field value");

		log.debug("Persistnig history item: " + hi);
		dao.saveHistoryItem(hi);

		log.debug("Persisted history item: " + hi);

		log.debug("Deleting history item: " + hi);
		dao.removeHistoryItem(hi);
	}
}