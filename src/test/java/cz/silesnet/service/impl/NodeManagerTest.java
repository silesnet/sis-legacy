package cz.silesnet.service.impl;

import cz.silesnet.model.Node;
import cz.silesnet.model.Wireless;
import cz.silesnet.service.NodeManager;
import org.testng.annotations.Test;
import org.unitils.spring.annotation.SpringBean;

import java.util.List;

/**
 * Testnig NodeManager service.
 *
 * @author Richard Sikora
 */
public class NodeManagerTest extends BaseServiceTestCase {

    @SpringBean("nodeManager")
    private NodeManager nmgr;

    @Test
    public void testCRUD() {

        Wireless node = new Wireless();

        node.setName("Testing node");
        node.setMac("12345");
        node.setParentId(0L);

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

        List<Node> nodes = nmgr.getLevelOfNodes(0);
        log.debug("Root network nodes: " + nodes);
    }
}