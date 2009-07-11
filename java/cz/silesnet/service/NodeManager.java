package cz.silesnet.service;

import cz.silesnet.model.Node;

import java.util.List;

/**
 * Network node management services interface.
 * 
 * @author Richard Sikora
 */
public interface NodeManager extends PersistenceManager<Node> {

	// ~ Methods
	// ----------------------------------------------------------------

	public List<Node> getLeveOfNodesByDomain(long parentId, long domainLabelId);

	public List<Node> getLevelOfNodes(long parentId);

	public Node getNodeById(long nodeId);

	public Node getNodeByName(String nodeName);

	public void deleteNode(Node node);

	public void insertNode(Node node);

	public void updateNode(Node node);
}