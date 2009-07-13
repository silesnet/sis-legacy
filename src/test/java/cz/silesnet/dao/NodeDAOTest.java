package cz.silesnet.dao;

import cz.silesnet.model.Label;
import cz.silesnet.model.Node;
import cz.silesnet.model.Wireless;
import cz.silesnet.model.enums.WirelessEnum;

import org.springframework.orm.ObjectRetrievalFailureException;

import java.util.ArrayList;

/**
 * Testing Node class hierary DAO.
 * 
 * @author Richard Sikora
 */
public class NodeDAOTest extends BaseDAOTestCase {

	// ~ Methods
	// ----------------------------------------------------------------

	public void testNodeGetters() {
		NodeDAO dao = (NodeDAO) ctx.getBean("nodeDAO");
		assertNotNull(dao);

		LabelDAO ldao = (LabelDAO) ctx.getBean("labelDAO");
		assertNotNull(ldao);

		log.debug("Received nodeDAO implementation from Spring context.");

		// have 2 different nodes
		Wireless node = new Wireless();

		Long highparentid = Long.valueOf(1000000);

		// save some APs
		node.setId(null);
		node.setHistoryId(Long.valueOf(1));
		node.setParentId(highparentid);
		node.setType(WirelessEnum.AP);
		node.setName("snet-AP1");
		dao.saveNode(node);

		node.setId(null);
		node.setName("snet-AP2");
		node.setHistoryId(2L);
		dao.saveNode(node);

		// save parent id
		Long parentid = node.getId();

		node.setId(null);
		node.setName("snet-AP3");
		node.setHistoryId(3L);
		dao.saveNode(node);

		// save some ClientAPs
		node.setId(null);
		node.setType(WirelessEnum.SA);
		node.setParentId(parentid);
		node.setName("snet-SA1");
		node.setHistoryId(4L);
		dao.saveNode(node);

		node.setId(null);
		node.setName("snet-SA2");
		node.setHistoryId(5L);
		dao.saveNode(node);

		node.setId(null);
		node.setName("snet-SA3");
		node.setHistoryId(6L);
		dao.saveNode(node);

		node.setId(null);
		node.setName("snet-SA4");
		node.setHistoryId(7L);
		dao.saveNode(node);

		node.setId(null);
		node.setName("snet-SA5");
		node.setHistoryId(8L);
		dao.saveNode(node);

		// get last node by name
		Node nodebyname = dao.getNodeByName(node.getName());
		assertNotNull(nodebyname);

		log.debug("Last node retrieved by name: " + nodebyname);

		// get back AP node with parentid set 2 highparentid
		ArrayList<Node> aps = (ArrayList<Node>) dao.getSubNodes(highparentid);
		log.debug("Previously inserted APs: " + aps);

		ArrayList<Node> caps = (ArrayList<Node>) dao.getSubNodes(parentid);
		log.debug("Previously inserted Client APs: " + caps);

		// clean up APs
		for (Node nap : aps)
			dao.removeNode(nap);

		// clean up Client APs
		for (Node ncap : caps)
			dao.removeNode(ncap);
	}

	public void testSaveRemoveNode() {
		NodeDAO dao = (NodeDAO) ctx.getBean("nodeDAO");
		assertNotNull(dao);

		LabelDAO ldao = (LabelDAO) ctx.getBean("labelDAO");
		assertNotNull(ldao);

		log.debug("Received nodeDAO implementation from Spring context.");

		Label domainLabel = ldao.getLabelById(Long.valueOf(22));

		// have Node
		Wireless node = new Wireless();
		node.setHistoryId(Long.valueOf(1));
		node.setParentId(Long.valueOf(10000));
		node.setType(WirelessEnum.AP);
		node.setName("snet-AP");
		node.setInfo("Comment on node 1");
		node.setMac("00001C123456");
		node.setDomain(domainLabel);
		node.setMacAuthorization(true);
		node.setIp("127.0.0.1");
		node.setSsid("ssid_00");
		node.setHistoryId(10L);

		// save it
		dao.saveNode(node);
		log.debug("Saved node: " + node);

		// save node id
		Long nodeid = node.getId();

		Node n2 = dao.getNodeById(nodeid);
		log.debug("Can retrieve saved node by its id: " + n2);

		n2.setName("updated name");
		log.debug("Updating node " + n2);
		dao.saveNode(n2);

		log.debug("Updated node " + n2);

		// delete it
		dao.removeNode(node);
		log.debug("Node deleted...");

		try {
			dao.getNodeById(nodeid);
			fail("Retrieved already deleted node");
		}
		catch (ObjectRetrievalFailureException e) {
			log.debug("Caught expected exception : " + e);
		}
	}
}