package cz.silesnet.dao.hibernate;

import cz.silesnet.dao.LabelDAO;
import cz.silesnet.dao.hibernate.support.SqlHibernateOrder;
import cz.silesnet.model.Label;
import cz.silesnet.utils.SearchUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import java.util.List;

/**
 * Implementation of LabelDAO interface using HibernateGenericDao.
 *
 * @author Richard Sikora
 */
public class LabelDAOHibernate extends HibernateGenericDao<Label> implements LabelDAO {

    public Label getLabelById(Long labelId) {
        return find(labelId);
    }

    public List<Label> getSubLabels(Long labelId) {
        return findByCriteria(Expression.eq("parentId", labelId));
    }

    public void removeLabel(Label label) {
        remove(label);
    }

    public void saveLabel(Label label) {
        store(label);
    }

    public List<Label> getByExmaple(Label example) {
        DetachedCriteria criteria = newCriteria();
        SearchUtils.addEqRestriction(criteria, "parent_id", example.getParentId());
        SearchUtils.addIlikeRestrictionI18n(criteria, "name", example.getName());
        criteria.addOrder(SqlHibernateOrder.asc(SearchUtils.getTranslateOrder("name")));
        return findByCriteria(criteria);
    }

    public List<Label> findAll() {
        return findByCriteria(Order.asc("name"));
    }

}