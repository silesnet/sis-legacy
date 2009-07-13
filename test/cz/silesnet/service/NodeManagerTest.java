package cz.silesnet.service;

import cz.silesnet.model.Node;
import cz.silesnet.model.Wireless;

import java.util.List;

/**
 * Testnig NodeManager service.
 * 
 * @author Richard Sikora
 */
public class NodeManagerTest extends BaseServiceTestCase {

	// ~ Methods
	// ----------------------------------------------------------------

	public void testCRUD() {
		NodeManager nmgr = (NodeManager) ctx.getBean("nodeManager");

		Wireless node = new Wireless();

		node.setName("Testing node");
		node.setMac("12345");
		node.setParentId(Long.valueOf(0));

		log.debug("Inserting node: " + node);
		nmgr.insertNode(node);

		node.setName("Updated name");
		log.debug("Updating node: " + node);
		nmgr.updateNode(node);

		log.debug("Deleting node: " + node);
		nmgr.deleteNode(node);
	}

	public void testGetLevelOfNodes() {
		NodeManager nmgr = (NodeManager) ctx.getBean("nodeManager");

		List<Node> nodes = nmgr.getLevelOfNodes(0);
		log.debug("Root network nodes: " + nodes);
	}
}