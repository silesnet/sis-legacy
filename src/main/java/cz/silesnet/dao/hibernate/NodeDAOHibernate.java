package cz.silesnet.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import cz.silesnet.dao.NodeDAO;
import cz.silesnet.dao.hibernate.support.SqlHibernateOrder;
import cz.silesnet.model.Node;
import cz.silesnet.model.Wireless;
import cz.silesnet.utils.SearchUtils;

/**
 * Implemnets NodeDAO for manipulating network node objects.
 * 
 * @author Richard Sikora
 */
public class NodeDAOHibernate extends HibernateDaoSupport implements NodeDAO {

	// ~ Instance fields
	// --------------------------------------------------------

	protected final Log log = LogFactory.getLog(getClass());

	// ~ Methods
	// ----------------------------------------------------------------

	public Node getNodeById(Long nodeId) {
		Node node = (Node) getHibernateTemplate().get(Node.class, nodeId);

		if (node == null)
			throw new ObjectRetrievalFailureException(Node.class, nodeId);

		return node;
	}

	public Node getNodeByName(String nodeName) {
		ArrayList<Node> nodes = (ArrayList<Node>) getHibernateTemplate().find(
				"from cz.silesnet.model.Node as node where node.name=?",
				nodeName);

		// node name is unique
		if ((nodes.size() == 0) || (nodes.size() > 1))
			throw new ObjectRetrievalFailureException(Node.class, nodeName);

		return nodes.get(0);
	}

	public List<Node> getSubNodes(Long parentId) {
		return (ArrayList<Node>) getHibernateTemplate()
				.find(
						"from cz.silesnet.model.Node as node where node.parentId=? order by node.name",
						parentId);
	}

	public List<Node> getSubNodesByDomain(Long parentId, Long domainLabelId) {
		return (ArrayList<Node>) getHibernateTemplate()
				.find(
						"from cz.silesnet.model.Node as node where (node.parentId=?) and (node.domainLabel.id=?) order by node.name",
						new Object[] { parentId, domainLabelId });
	}

	public void evictNode(Node node) {
		getHibernateTemplate().evict(node);
	}

	public void removeNode(Node node) {
		getHibernateTemplate().delete(node);
	}

	public void saveNode(Node node) {
		getHibernateTemplate().saveOrUpdate(node);
	}

	public List<Node> getByExample(Node example) {
		if (example == null) {
			// no example given return all nodes
			log.debug("No example Node given, returning all nodes.");
			return getAll();
		}
		log.debug(example);
		DetachedCriteria crit = null;
		if (example instanceof Wireless) {
			crit = DetachedCriteria.forClass(Wireless.class);
			Wireless wireless = (Wireless) example;
			// set wireless specific restrictions
			SearchUtils.addEqRestriction(crit, "type", wireless.getType());
			SearchUtils.addEqRestriction(crit, "domain_lid", wireless
					.getDomain());
			SearchUtils.addIlikeRestriction(crit, "mac", wireless.getMac());
			SearchUtils.addIlikeRestriction(crit, "ip", wireless.getIp());
		}
		else {
			crit = DetachedCriteria.forClass(Node.class);
		}
		// set common restrictions from exmaple object
		SearchUtils.addEqRestriction(crit, "parent_id", example.getParentId());
		SearchUtils.addIlikeRestrictionI18n(crit, "name", example.getName());
		SearchUtils.addEqRestriction(crit, "active", example.isActive());
		// set ordering by name
		crit.addOrder(SqlHibernateOrder.asc(SearchUtils
				.getTranslateOrder("name")));
		return (ArrayList<Node>) getHibernateTemplate().findByCriteria(crit);
	}

	public List<Node> getAll() {
		return (ArrayList<Node>) getHibernateTemplate().find(
				"from Node n order by n.name");
	}
}