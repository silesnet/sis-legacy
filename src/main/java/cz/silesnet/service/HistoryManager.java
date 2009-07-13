package cz.silesnet.service;

import cz.silesnet.model.Customer;
import cz.silesnet.model.Historic;
import cz.silesnet.model.HistoryItem;
import cz.silesnet.model.Invoicing;
import cz.silesnet.model.User;

import java.util.List;

import javax.servlet.http.HttpSession;

/**
 * Service responsible for managing objects history. Objects need to implement
 * Historic interface.
 * 
 * @author Richard Sikora
 */
public interface HistoryManager {

	// ~ Methods
	// ----------------------------------------------------------------

	public List<HistoryItem> getHistory(Historic historic);

	public List<HistoryItem> getLoginHistory();

	public Long getNewHistoryId();

	public List<HistoryItem> getUserLoginHistory(User user);

	public void deleteHistory(Historic historic);

	public void insertHistory(Historic oldHistoric, Historic newHistoric);

	public void insertLogin(User user, String ip, String sessionId);

	public void updateLogout(HttpSession session);

	public void insertSystemBillingAudit(Invoicing invoicing,
			Customer customer, String msg, String status);

	public void insertCustomerBillingAudit(Customer customer, String msg,
			String status);

	public List<HistoryItem> getSystemBillingAudit();

	public void clearBillingAudit();

}