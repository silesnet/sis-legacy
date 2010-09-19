package cz.silesnet.dao.hibernate;

import cz.silesnet.dao.LabelDAO;
import cz.silesnet.model.Label;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.MatchMode;
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
    return findByCriteria(newCriteria()
        .add(Expression.eq("parentId", labelId)));
  }

  public void removeLabel(Label label) {
    remove(label);
  }

  public void saveLabel(Label label) {
    store(label);
  }

  public List<Label> findAll() {
    return findByCriteria(newCriteria()
        .addOrder(Order.asc("name")));
  }

  public List<Label> getByExmaple(Label example) {
    DetachedCriteria criteria = newCriteria();
    if (example.getParentId() != null)
      criteria.add(Expression.eq("parentId", example.getParentId()));
    if (example.getName() != null)
      criteria.add(Expression.ilike("name", example.getName(), MatchMode.START));
    criteria.addOrder(Order.asc("name"));
    return findByCriteria(criteria);
  }
}