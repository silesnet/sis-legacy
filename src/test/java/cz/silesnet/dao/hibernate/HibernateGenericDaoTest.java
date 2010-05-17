package cz.silesnet.dao.hibernate;

import cz.silesnet.dao.GenericDao;
import org.hibernate.criterion.DetachedCriteria;
import org.mockito.Matchers;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * Created by IntelliJ IDEA.
 * User: sikorric
 * Date: May 17, 2010
 * Time: 4:57:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class HibernateGenericDaoTest {

    private GenericDao<Object> dao;
    private HibernateGenericDao<Object> hibernateDao;
    private HibernateTemplate template;
    private final Object entity = new Object();

    @BeforeMethod
    public void setUp() throws Exception {
        hibernateDao = new HibernateGenericDao<Object>() {};
        dao = hibernateDao;
        template = mock(HibernateTemplate.class);
        hibernateDao.setHibernateTemplate(template);
    }

    @Test
    public void find() throws Exception {
        when(template.get(Object.class, 1L)).thenReturn(entity);
        assertThat(dao.find(1L), is(entity));
    }

    @Test
    public void store() throws Exception {
        dao.store(entity);
        verify(template).saveOrUpdate(entity);
    }

    @Test
    public void remove() throws Exception {
        dao.remove(entity);
        verify(template).delete(entity);
    }

    @Test
    public void findByCriteria() throws Exception {
        List<Object> allEntities = Arrays.asList(entity);
        when(template.findByCriteria((DetachedCriteria) anyObject())).thenReturn(allEntities);
        assertThat(hibernateDao.findByCriteria(hibernateDao.newCriteria()).get(0), is(entity));
    }
}
