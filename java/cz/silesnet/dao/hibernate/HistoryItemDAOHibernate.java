package cz.silesnet.dao.hibernate;

import cz.silesnet.dao.HistoryItemDAO;
import cz.silesnet.model.HistoryItem;
import cz.silesnet.model.Label;
import cz.silesnet.model.User;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Hibernate HistoryItemDAO implementation.
 * 
 * @author Richard Sikora
 */
public class HistoryItemDAOHibernate extends HibernateDaoSupport implements
		HistoryItemDAO {

	// ~ Instance fields
	// --------------------------------------------------------

	protected final Log log = LogFactory.getLog(getClass());

	// ~ Methods
	// ----------------------------------------------------------------

	public List<HistoryItem> getHistory(Long historyId) {
		return (ArrayList<HistoryItem>) getHibernateTemplate()
				.find(
						"from cz.silesnet.model.HistoryItem as item where item.historyId=? order by item.timeStamp desc, item.id desc",
						historyId);
	}

	public Long getNewHistoryId() {
		// return max on history_id column + 1
		Object result = getHibernateTemplate()
				.find(
						"select max(item.historyId) from cz.silesnet.model.HistoryItem as item)")
				.get(0);

		log.debug("returned max: " + result);

		if (result != null)
			return Long.valueOf(((Long) result) + 1);

		return Long.valueOf(1);
	}

	public List<HistoryItem> getOpenLoginItems(String sessionId) {
		// FIXME 17, and mOnline can not be hardcoded, use global const
		return (ArrayList<HistoryItem>) getHibernateTemplate()
				.find(
						"from cz.silesnet.model.HistoryItem as item where (item.historyTypeLabel.id=?) and (item.oldValue=?) and (item.newValue=?) order by item.timeStamp desc",
						new Object[] { Long.valueOf(17), sessionId,
								"lastLogin.Online" });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cz.silesnet.dao.HistoryItemDAO#getUserHistory(cz.silesnet.model.User,
	 * cz.silesnet.model.Label)
	 */
	public List<HistoryItem> getUserHistory(User user, Label HistoryType) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cz.silesnet.dao.HistoryItemDAO#removeHistory(java.lang.Long)
	 */
	public void removeHistory(Long historyId) {
		// TODO Auto-generated method stub
	}

	public void removeHistoryItem(HistoryItem historyItem) {
		getHibernateTemplate().delete(historyItem);
	}

	public void saveHistoryItem(HistoryItem historyItem) {
		getHibernateTemplate().saveOrUpdate(historyItem);
	}

	public void clearBillingAudit() {
		// FIXME 1 is hardcode value for billing audit
		getHibernateTemplate().deleteAll(getHistory(Long.valueOf(1)));
	}
}