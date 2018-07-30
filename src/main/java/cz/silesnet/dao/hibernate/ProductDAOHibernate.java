package cz.silesnet.dao.hibernate;

import com.google.common.collect.Lists;
import cz.silesnet.dao.ProductDAO;
import cz.silesnet.model.Product;
import cz.silesnet.model.enums.Country;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.List;

public class ProductDAOHibernate extends HibernateDaoSupport implements ProductDAO {
  @Override
  public List<Product> getByCountry(Country c) {
    final List<Product> products = getHibernateTemplate().find(
        "from Product as p where p.country=? order by p.position", c.getShortName().toUpperCase());
    return products != null ? products : Lists.<Product>newArrayList();
  }
}
