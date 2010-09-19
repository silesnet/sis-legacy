package cz.silesnet.dao.hibernate;

import cz.silesnet.dao.GenericDao;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: sikorric
 * Date: May 17, 2010
 * Time: 2:16:29 PM
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
}
