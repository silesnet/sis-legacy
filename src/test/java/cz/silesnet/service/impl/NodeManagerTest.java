package cz.silesnet.service.impl;

import cz.silesnet.model.Node;
import cz.silesnet.model.Wireless;
import cz.silesnet.service.NodeManager;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Testnig NodeManager service.
 *
 * @author Richard Sikora
 */
public class NodeManagerTest extends BaseServiceTestCase {

    @Test
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

    @Test
    public void testGetLevelOfNodes() {
        NodeManager nmgr = (NodeManager) ctx.getBean("nodeManager");

        List<Node> nodes = nmgr.getLevelOfNodes(0);
        log.debug("Root network nodes: " + nodes);
    }
}