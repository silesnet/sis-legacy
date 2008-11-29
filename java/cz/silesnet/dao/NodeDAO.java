package cz.silesnet.dao;

import cz.silesnet.model.Node;

import java.util.List;

/**
 * Node DAO interface for persisting Node class hierarchy.
 *
 * @author Richard Sikora
 */
public interface NodeDAO
    extends DAO {

    //~ Methods ----------------------------------------------------------------

    public Node getNodeById(Long nodeId);

    public Node getNodeByName(String nodeName);

    public List<Node> getSubNodes(Long parentId);

    public List<Node> getSubNodesByDomain(Long parentId, Long domainLabelId);

    public void evictNode(Node node);

    public void removeNode(Node node);

    public void saveNode(Node node);

	public List<Node> getByExample(Node example);

	public List<Node> getAll();

}