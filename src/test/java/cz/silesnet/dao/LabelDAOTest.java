package cz.silesnet.dao;

import static org.hamcrest.MatcherAssert.*;
import static org.unitils.reflectionassert.ReflectionAssert.*;
import static org.hamcrest.Matchers.*;

import cz.silesnet.model.Label;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.Test;
import org.unitils.UnitilsTestNG;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.orm.hibernate.HibernateUnitils;
import org.unitils.reflectionassert.ReflectionComparatorMode;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import java.util.ArrayList;
import java.util.List;

@SpringApplicationContext({"context/sis-properties.xml", "context/sis-db.xml", "context/sis-hibernate.xml", "context/sis-dao.xml"})
public class LabelDAOTest extends UnitilsTestNG {

    private final Log log = LogFactory.getLog(getClass());

    @SpringBean("labelDAO")
    private LabelDAO dao;

    @Test
    public void testSaveRemoveLabel() {
        Label lab = new Label();
        lab.setName("AP Type");
        lab.setParentId(0L);

        // save label
        dao.saveLabel(lab);

        // store label id
        long labelid = lab.getId();

        // remove label
        dao.removeLabel(lab);

        // when trying to get not existing label return null
        assertThat(dao.getLabelById(labelid), is(nullValue()));
    }

    @DataSet("LabelDAOTest-find.xml")
    @Test
    public void findById() throws Exception {
        Label label = new Label();
        label.setId(11L);
        label.setParentId(10L);
        label.setName("Label 1");
        
        Label persistedLabel = dao.getLabelById(11L);
        assertReflectionEquals(label, persistedLabel);
    }

    @Test
    public void defaultLabels() throws Exception {
        List<Label> labels = dao.getSubLabels(300L);
        assertThat(labels.size(), is(greaterThanOrEqualTo(2)));
    }
}
