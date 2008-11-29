package cz.silesnet.dao.hibernate.support;

import cz.silesnet.model.enums.Frequency;

/**
 * Class for Frequency enum Hibernate mapping.
 *
 * @author Richard Sikora
 */
public class FrequencyHibernateUserType
    extends IntEnumHibernateUserType<Frequency> {

    //~ Constructors -----------------------------------------------------------

    public FrequencyHibernateUserType() {
        super(Frequency.MONTHLY);
    }
}