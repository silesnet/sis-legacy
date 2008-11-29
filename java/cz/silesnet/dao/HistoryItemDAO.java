package cz.silesnet.dao;

import cz.silesnet.model.HistoryItem;
import cz.silesnet.model.Label;
import cz.silesnet.model.User;

import java.util.List;

/**
 * DAO interface to manipulate history items data.
 *
 * @author Richard Sikora
 */
public interface HistoryItemDAO
    extends DAO {

    //~ Methods ----------------------------------------------------------------

    public List<HistoryItem> getHistory(Long historyId);

    public Long getNewHistoryId();

    // TODO have more generic finding methot (byExample or so)
    // to retrieve open login items, at this level there should
    // not be such details...
    public List<HistoryItem> getOpenLoginItems(String sessionId);

    public List<HistoryItem> getUserHistory(User user, Label HistoryType);

    public void removeHistory(Long historyId);

    public void removeHistoryItem(HistoryItem historyItem);

    public void saveHistoryItem(HistoryItem historyItem);

    public void clearBillingAudit();
}