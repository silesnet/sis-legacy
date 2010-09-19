package cz.silesnet.service.impl;

import cz.silesnet.model.HistoryItem;
import cz.silesnet.model.Wireless;
import cz.silesnet.model.enums.WirelessEnum;
import cz.silesnet.service.HistoryManager;
import cz.silesnet.service.NodeManager;
import org.testng.annotations.Test;
import org.unitils.spring.annotation.SpringBean;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

public class HistoryManagerTest extends BaseServiceTestCase {

  @SpringBean("nodeManager")
  private NodeManager nmgr;

  @SpringBean("historyManager")
  private HistoryManager hmgr;

  @Test
  public void testSetGetHistory() {
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