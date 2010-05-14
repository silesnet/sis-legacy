package cz.silesnet.dao;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

import cz.silesnet.model.Label;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.Test;
import org.unitils.UnitilsTestNG;
import org.unitils.orm.hibernate.HibernateUnitils;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import java.util.ArrayList;

@SpringApplicationContext({"context/sis-properties.xml", "context/sis-db.xml", "context/sis-hibernate.xml", "context/sis-dao.xml"})
public class LabelDAOTest extends UnitilsTestNG {

    private final Log log = LogFactory.getLog(getClass());

    private ApplicationContext ctx;

    @SpringBean("labelDAO")
    private LabelDAO dao;

    @Test
    public void testGetters() {
//        LabelDAO dao = (LabelDAO) ctx.getBean("labelDAO");

        Label lab = new Label();
        lab.setName("Test Type");
        lab.setParentId(0L);

        // save root of Test Type labels
        dao.saveLabel(lab);

        // store parent label id
        assertThat(lab.getParentId(), is(0L));

        long parentid = lab.getId();

        // have some sublabels
        lab.setId(null);
        lab.setName("TT Label 1");
        lab.setParentId(parentid);
        dao.saveLabel(lab);

        lab.setId(null);
        lab.setName("TT Label 2");
        lab.setParentId(parentid);
        dao.saveLabel(lab);

        lab.setId(null);
        lab.setName("TT Label 3");
        lab.setParentId(parentid);
        dao.saveLabel(lab);

        lab.setId(null);
        lab.setName("TT Label 4");
        lab.setParentId(parentid);
        dao.saveLabel(lab);

        lab = null;

        // try to get all sublabels from id we stored

        // first get parent label by id
        lab = dao.getLabelById(parentid);
        assertThat(lab, is(notNullValue()));
        log.debug("Retrieved root Test Type label : " + lab);

        // now get sublabels
        ArrayList<Label> sublabels = (ArrayList<Label>) dao.getSubLabels(lab
                .getId());
        assertThat(sublabels.size(), is(not(0)));
        log.debug("Retrieved sublabels of Test Type : " + sublabels);

        // do some clean up
        for (Label l : sublabels)
            dao.removeLabel(l);

        // remove also root Test Type label
        dao.removeLabel(lab);
    }

    @Test
    public void testSaveRemoveLabel() {
//        LabelDAO dao = (LabelDAO) ctx.getBean("labelDAO");

        Label lab = new Label();
        lab.setName("AP Type");
        lab.setParentId(Long.valueOf(0));

        // save label
        dao.saveLabel(lab);

        // store label id
        long labelid = lab.getId();

        // remove label
        dao.removeLabel(lab);

        // when tryin to get not existing label return null
        assertThat(dao.getLabelById(labelid), is(nullValue()));
    }

    @Test
    public void testMapping() throws Exception {
        HibernateUnitils.assertMappingWithDatabaseConsistent();
    }
}
