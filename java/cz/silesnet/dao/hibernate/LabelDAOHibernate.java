package cz.silesnet.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import cz.silesnet.dao.LabelDAO;
import cz.silesnet.dao.hibernate.support.SqlHibernateOrder;
import cz.silesnet.model.Label;
import cz.silesnet.utils.SearchUtils;

/**
 * Implementation of LabelDAO interface using Hibernate.
 *
 * @author Richard Sikora
 */
public class LabelDAOHibernate
    extends HibernateDaoSupport
    implements LabelDAO {

    //~ Methods ----------------------------------------------------------------

    public Label getLabelById(Long labelId) {
        Label label = (Label) getHibernateTemplate()
                                      .get(Label.class, labelId);

        return label;
    }

    public List getSubLabels(Long labelId) {
        return (ArrayList<Label>) getHibernateTemplate()
                                          .find("from cz.silesnet.model.Label as label where label.parentId=?",
                labelId);
    }

    public void removeLabel(Label label) {
        getHibernateTemplate().delete(label);
    }

    public void saveLabel(Label label) {
        getHibernateTemplate().saveOrUpdate(label);
    }

	public List<Label> getByExmaple(Label example) {
    	DetachedCriteria crit = DetachedCriteria.forClass(Label.class);
    	// set restrictions from exmaple object
    	SearchUtils.addEqRestriction(crit, "parent_id", example.getParentId());
    	SearchUtils.addIlikeRestrictionI18n(crit, "name", example.getName());
    	// set ordering
    	crit.addOrder(SqlHibernateOrder.asc(SearchUtils.getTranslateOrder("name")));
		return (ArrayList<Label>) getHibernateTemplate().findByCriteria(crit);
	}
	
	public List<Label> getAll() {
    	return (ArrayList<Label>) getHibernateTemplate().find("from Label l order by l.name");
	}
}