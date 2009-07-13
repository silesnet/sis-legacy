package cz.silesnet.service;

import java.util.ArrayList;
import java.util.List;

import cz.silesnet.model.HistoryItem;
import cz.silesnet.model.Wireless;
import cz.silesnet.model.enums.WirelessEnum;

public class HistoryManagerTest extends BaseServiceTestCase {

	// ~ Methods
	// ----------------------------------------------------------------

	public void testSetGetHistory() {
		NodeManager nmgr = (NodeManager) ctx.getBean("nodeManager");
		HistoryManager hmgr = (HistoryManager) ctx.getBean("historyManager");

		assertNotNull(nmgr);
		assertNotNull(hmgr);

		// have node
		Wireless node = new Wireless();
		node.setType(WirelessEnum.AP);
		node.setName("testNode1");
		node.setMac("12345");
		nmgr.insertNode(node);

		// check history record
		assertNotNull(node.getHistoryId());

		List<HistoryItem> history = hmgr.getHistory(node);
		assertNotNull(history);
		log.debug("History of fresh node: " + history);

		// make some changes

		// clean up
		log.debug("Deleting node...");
		nmgr.deleteNode(node);
		history = (ArrayList<HistoryItem>) hmgr.getHistory(node);
		assertEquals(history.size(), 0);
	}
}