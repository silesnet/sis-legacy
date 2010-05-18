package cz.silesnet.dao;

import org.testng.annotations.BeforeMethod;
import org.unitils.UnitilsTestNG;

/**
 * Created by IntelliJ IDEA.
 * User: sikorric
 * Date: May 18, 2010
 * Time: 4:58:46 PM
 */
public abstract class DaoTestSupport<D> extends UnitilsTestNG {

    protected D dao;

    protected abstract D configureDao();

    @BeforeMethod
    public void setUp() {
        if (dao == null)
            dao = configureDao();
    }

}
