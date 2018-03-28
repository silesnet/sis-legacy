package cz.silesnet.dao;

import cz.silesnet.model.Product;
import cz.silesnet.model.enums.Country;

import java.util.List;

public interface ProductDAO extends DAO {
    List<Product> getByCountry(Country c);
}