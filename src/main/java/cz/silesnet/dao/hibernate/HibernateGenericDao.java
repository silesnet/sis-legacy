package cz.silesnet.dao.hibernate;

import cz.silesnet.dao.GenericDao;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.lang.reflect.ParameterizedType;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: sikorric
 * Date: May 17, 2010
 * Time: 2:16:29 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class HibernateGenericDao<E> extends HibernateDaoSupport implements GenericDao<E> {

    private final Class<E> entityClass;

    @SuppressWarnings("unchecked")
    public HibernateGenericDao() {
        this.entityClass = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @SuppressWarnings("unchecked")
    public E find(long id) {
        return (E) getHibernateTemplate().get(entityClass, id);
    }

    public void store(E entity) {
        getHibernateTemplate().saveOrUpdate(entity);
    }

    public void remove(E entity) {
        getHibernateTemplate().delete(entity);
    }

    @SuppressWarnings("unchecked")
    public List<E> findByCriteria(DetachedCriteria criteria) {
        return getHibernateTemplate().findByCriteria(criteria);
    }

    public DetachedCriteria newCriteria() {
        return DetachedCriteria.forClass(entityClass);
    }

    @SuppressWarnings("unchecked")
    public List<E> findByCriteria(Criterion... criterion) {
        DetachedCriteria criteria = newCriteria();
        for (Criterion c : criterion)
            criteria.add(c);
        return getHibernateTemplate().findByCriteria(criteria);
    }
    
    @SuppressWarnings("unchecked")
    public List<E> findByCriteria(Order... order) {
        DetachedCriteria criteria = newCriteria();
        for (Order o : order)
            criteria.addOrder(o);
        return getHibernateTemplate().findByCriteria(criteria);
    }


}
