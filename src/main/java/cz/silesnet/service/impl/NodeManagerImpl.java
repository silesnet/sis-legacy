package cz.silesnet.service.impl;

import cz.silesnet.dao.NodeDAO;
import cz.silesnet.model.Node;
import cz.silesnet.service.HistoryManager;
import cz.silesnet.service.NodeManager;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Node manager implementation mostly using DAO.
 *
 * @author Richard Sikora
 */
public class NodeManagerImpl implements NodeManager {

  // ~ Instance fields
  // --------------------------------------------------------

  private NodeDAO dao;

  private HistoryManager hmgr;

  // ~ Methods
  // ----------------------------------------------------------------

  // wired by Spring

  public void setHistoryManager(HistoryManager historyManager) {
    hmgr = historyManager;
  }

  public List<Node> getLeveOfNodesByDomain(long parentId, long domainLabelId) {
    return dao.getSubNodesByDomain(parentId, domainLabelId);
  }

  public ArrayList<Node> getLevelOfNodes(long parentId) {
    return (ArrayList<Node>) dao.getSubNodes(Long.valueOf(parentId));
  }

  public Node getNodeById(long nodeId) {
    return dao.getNodeById(Long.valueOf(nodeId));
  }

  public Node getNodeByName(String nodeName) {
    return dao.getNodeByName(nodeName);
  }

  // wired by Spring

  public void setNodeDAO(NodeDAO nodeDAO) {
    this.dao = nodeDAO;
  }

  public void deleteNode(Node node) {
    // before removing node get rid of its history
    hmgr.deleteHistory(node);

    dao.removeNode(node);
  }

  public void insertNode(Node node) {
    // make sure we will insert new node
    node.setId(null);

    // set new historyId
    node.setHistoryId(hmgr.getNewHistoryId());

    // save history
    hmgr.insertHistory(null, node);

    // persist new node with historyId set
    dao.saveNode(node);
  }

  public void updateNode(Node node) {
    // let's first get original copy of it
    Node formerNode = dao.getNodeById(node.getId());

    // detatch formerNode from hibernate session
    dao.evictNode(formerNode);

    // get sure we have valid historyId in node
    Assert.notNull(formerNode.getHistoryId(),
        "Persisted node without historyId.");

    // just in the case something weird is going on outside
    Assert.isTrue(formerNode.getHistoryId().equals(node.getHistoryId()),
        "Outside (illegal) historyId change.");

    // save diff
    hmgr.insertHistory(formerNode, node);

    // save node
    dao.saveNode(node);
  }

  public Node get(Long id) {
    return getNodeById(id);
  }

  public List<Node> getAll() {
    return dao.getAll();
  }

  public List<Node> getByExample(Node example) {
    return dao.getByExample(example);
  }

  public void insert(Node entity) {
    insertNode(entity);
  }

  public void update(Node entity) {
    updateNode(entity);
  }

  public void delete(Node entity) {
    deleteNode(entity);
  }
}