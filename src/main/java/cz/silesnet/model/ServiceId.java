package cz.silesnet.model;

import cz.silesnet.model.enums.Country;

/**
 * User: admin
 * Date: 9.1.12
 * Time: 21:46
 */
public final class ServiceId {
    private static final int COUNTRY_MASK = 1000000000;
    private static final int ORDER_MASK = 10;

    final private int id;
    final private Country country;
    final private ContractNo contractNo;
    final private int orderNo;

    public ServiceId(final int id) {
        this.id = id;
        int countryId = id / COUNTRY_MASK;
        if (countryId > 1 || countryId < 0)
            throw new IllegalArgumentException("unknown country id '" + countryId + "'");
        country = countryId == 0 ? Country.CZ : Country.PL;
        contractNo = new ContractNo((id % COUNTRY_MASK) / ORDER_MASK);
        orderNo = id % 10;
    }

    public static ServiceId serviceId(final int id) {
        return new ServiceId(id);
    }

    public int id() {
        return id;
    }

    public Country country() {
        return country;
    }

    public ContractNo contractNo() {
        return contractNo;
    }

    public int orderNo() {
        return orderNo;
    }

    @Override
    public String toString() {
        return "" + id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServiceId serviceId = (ServiceId) o;

        if (id != serviceId.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
