package cz.silesnet.dao;

import cz.silesnet.model.Label;
import org.testng.annotations.Test;
import org.unitils.dbunit.annotation.DataSet;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;

@DataSet("/cz/silesnet/dao/LabelDAOTest.xml")
public abstract class LabelDAOTest extends DaoTestSupport<LabelDAO> {

    @Test
    public void findAll() {
        List<Label> labels = dao.findAll();
        assertThat(labels.size(), is(greaterThanOrEqualTo(3)));
    }

    @Test
    public void getSubLabels() throws Exception {
        List<Label> labels = dao.getSubLabels(10L);
        assertThat(labels.size(), is(greaterThanOrEqualTo(3)));
    }

    @Test
    public void getByExmaple() throws Exception {
        Label label = new Label();
        label.setName("Label");
        List<Label> labels = dao.getByExmaple(label);
        assertThat(labels.size(), is(greaterThanOrEqualTo(3)));
        assertThat(labels.get(0).getName(), is("Label 1"));
    }

}
