package cz.silesnet.service.impl;

import cz.silesnet.dao.HistoryItemDAO;

import cz.silesnet.model.Customer;
import cz.silesnet.model.Historic;
import cz.silesnet.model.HistoryItem;
import cz.silesnet.model.Invoicing;
import cz.silesnet.model.Label;
import cz.silesnet.model.User;

import cz.silesnet.service.HistoryManager;
import cz.silesnet.service.LabelManager;
import cz.silesnet.utils.DiffUtils;
import cz.silesnet.utils.SecurityUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

/**
 * Concrete HistoryManager implementation using HistoryItemDAO implementation.
 * 
 * @author Richard Sikora
 */
public class HistoryManagerImpl implements HistoryManager {

	// ~ Instance fields
	// --------------------------------------------------------

	protected final Log log = LogFactory.getLog(getClass());

	private HistoryItemDAO dao;

	private LabelManager lmgr;

	// ~ Methods
	// ----------------------------------------------------------------

	public List<HistoryItem> getHistory(Historic historic) {
		// if there is somethink give it out
		if (historic.getHistoryId() != null)
			return (ArrayList<HistoryItem>) dao.getHistory(historic
					.getHistoryId());

		return null;
	}

	// injected by Spring context
	public void setHistoryItemDAO(HistoryItemDAO historyItemDAO) {
		dao = historyItemDAO;
	}

	// injected by Spring context
	public void setLabelManager(LabelManager labelManager) {
		lmgr = labelManager;
	}

	public List<HistoryItem> getLoginHistory() {
		log.info("Getting all login HistoryItems");

		// using zero for system logs
		// FIXME can not be hardcoded so, do it via appliction interface consts
		return dao.getHistory(Long.valueOf(0));
	}

	public Long getNewHistoryId() {
		return dao.getNewHistoryId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cz.silesnet.service.HistoryManager#getUserLoginHistory(cz.silesnet.model
	 * .User)
	 */
	public List<HistoryItem> getUserLoginHistory(User user) {
		// TODO Auto-generated method stub
		log.info("Getting all user login HistoryItems (user: "
				+ user.getLoginName() + ")");

		return null;
	}

	public void deleteHistory(Historic historic) {
		ArrayList<HistoryItem> history = (ArrayList<HistoryItem>) dao
				.getHistory(historic.getHistoryId());
		log.debug("Deleting all history of an object.");

		// history can not be null due to dao implementation
		// of HibernateTemplate.find() (can check in Spring API)
		for (HistoryItem item : history)
			dao.removeHistoryItem(item);

		// FIXME delegate it to DAO method removeHisotory(historyId);
	}

	public void insertHistory(Historic oldHistoric, Historic newHistoric) {
		Assert.notNull(newHistoric, "New historic object not set!");

		// make sure there is not null history id in historic object
		Long historyId = newHistoric.getHistoryId();
		Assert.notNull(historyId, "Historic object without HistoryId set!");

		Label historyTypeLabel = lmgr.getLabelById(newHistoric
				.getHistoryTypeLabelId());
		User user = SecurityUtils.getUser();
		if (user.getId() == 1) {
			// we have anonyousUser here, replace him with system user
			user = new User();
			// 2 is hardcoded system user id
			user.setId(2L);
			user.setLoginName("system");
		}
		// compute diffMap
		Map<String, String[]> diffMap = DiffUtils.getDiffMap(oldHistoric,
				newHistoric, newHistoric.getDiffExcludeFields());

		// loop all changed fields and persisit their history item
		for (String field : diffMap.keySet()) {
			HistoryItem historyItem = new HistoryItem();

			// populate history item with data
			historyItem.setHistoryId(historyId);
			historyItem.setHistoryTypeLabel(historyTypeLabel);
			historyItem.setUser(user);
			historyItem.setTimeStamp(new Date());
			historyItem.setFieldName(field);
			// for database compatibility reasons trim to 254 chars
			historyItem.setOldValue(StringUtils.abbreviate(
					diffMap.get(field)[0], 254));
			historyItem.setNewValue(StringUtils.abbreviate(
					diffMap.get(field)[1], 254));

			// persist it
			log.debug("Persisting new HistoryItem.");
			dao.saveHistoryItem(historyItem);
		}
	}

	public void insertLogin(User user, String ip, String sessionId) {
		log.info("Inserting new user login HistoryItem (user: "
				+ user.getLoginName() + ", ip: " + ip + ", sessionId: "
				+ sessionId + ")");

		// populate new history item with given info
		HistoryItem historyItem = new HistoryItem();

		// get apropriate history label
		// FIXME can not be hardcoded so, do it via appliction interface consts
		Label historyTypeLabel = lmgr.getLabelById(Long.valueOf(17));

		// using zero for system logs
		// FIXME can not be hardcodes so, do it via appliction interface consts
		historyItem.setHistoryId(Long.valueOf(0));
		historyItem.setHistoryTypeLabel(historyTypeLabel);
		historyItem.setTimeStamp(new Date());
		historyItem.setUser(user);
		historyItem.setFieldName(ip);
		historyItem.setOldValue(sessionId);
		// FIXME can not be hardcoded here, global application const
		historyItem.setNewValue("lastLogin.Online");

		// before persisting new login item into database
		// make sure that there is no user logged in
		// with the same sessionId, this can happend while
		// being properly logged in, going manually to login.jsp
		// page and perform another successful login then
		// session will not be destroyed but new user can be
		// associated with it...so we would have online user
		// in database which is actualy alredy logged off
		updateLogout(sessionId);

		// persist new login history item
		if (log.isDebugEnabled())
			log.debug("Persisting login history item.");

		dao.saveHistoryItem(historyItem);
	}

	public void updateLogout(HttpSession session) {
		log.info("Updating logout time in HistoryItem (session: "
				+ session.getId() + ")");
		updateLogout(session.getId());
	}

	private void updateLogout(String sessionId) {
		// logoff login item with given sessionId and being set online
		List<HistoryItem> onlineItems = dao.getOpenLoginItems(sessionId);

		if (onlineItems.size() > 1)
			log.error("Found more than 1 online sessions (sessionId: "
					+ sessionId + ")");

		if (onlineItems.size() == 1) {
			// update Logout time
			onlineItems.get(0).setNewValue(
					Long.valueOf((new Date()).getTime()).toString());

			if (log.isDebugEnabled())
				log.debug("Logout time set in history item: "
						+ onlineItems.get(0));

			// persist it
			dao.saveHistoryItem(onlineItems.get(0));
		}
		else
			// no open session
			log.debug("No opened session found for: " + sessionId);
	}

	public void insertSystemBillingAudit(Invoicing invoicing,
			Customer customer, String msg, String status) {
		log.debug("Inserting System BillingAudit.");
		if (log.isDebugEnabled()) {
			if (customer != null)
				log.debug("[" + customer.getName() + "] " + msg + " (" + status
						+ ")");
			else
				log.debug(msg + " (" + status + ")");
		}
		// populate new history item with given info
		HistoryItem historyItem = new HistoryItem();
		// using 1 for system billing logs
		// FIXME can not be hardcodes so, do it via appliction interface consts
		historyItem.setHistoryId(invoicing.getHistoryId());
		historyItem.setHistoryTypeLabel(lmgr.getLabelById(invoicing
				.getHistoryTypeLabelId()));
		User user = SecurityUtils.getUser();
		if (user.getId() == 1) {
			// we have anonyousUser here, replace him with system user
			user = new User();
			// 2 is hardcoded system user id
			user.setId(2L);
			user.setLoginName("system");
		}
		historyItem.setUser(user);
		historyItem.setTimeStamp(new Date());
		if (customer != null)
			historyItem.setFieldName(customer.getId().toString() + ","
					+ customer.getName());
		else
			historyItem.setFieldName("");
		historyItem.setOldValue(msg);
		historyItem.setNewValue(status);
		dao.saveHistoryItem(historyItem);
	}

	public void insertCustomerBillingAudit(Customer customer, String msg,
			String status) {
		// populate history item with data
		HistoryItem historyItem = new HistoryItem();
		historyItem.setHistoryId(customer.getHistoryId());
		historyItem.setHistoryTypeLabel(lmgr.getLabelById(customer
				.getHistoryTypeLabelId()));
		User user = SecurityUtils.getUser();
		if (user.getId() == 1) {
			// we have anonyousUser here, replace him with system user
			user = new User();
			// 2 is hardcoded system user id
			user.setId(2L);
			user.setLoginName("system");
		}
		historyItem.setUser(user);
		historyItem.setTimeStamp(new Date());
		historyItem.setFieldName("Customer.billingAudit");
		historyItem.setOldValue(msg);
		historyItem.setNewValue(status);
		dao.saveHistoryItem(historyItem);
	}

	public List<HistoryItem> getSystemBillingAudit() {
		log.info("Getting system billing audit.");
		// using 1 for billing audit
		// FIXME can not be hardcoded so, do it via appliction interface consts
		return dao.getHistory(Long.valueOf(1));
	}

	public void clearBillingAudit() {
		dao.clearBillingAudit();
	}
}
