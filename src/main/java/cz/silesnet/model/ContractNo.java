package cz.silesnet.model;

/**
 * User: admin
 * Date: 9.1.12
 * Time: 21:48
 */
public final class ContractNo {
    private static final int YEAR_MASK = 10000;
    private static final int MAX_SEQUENCE = 9999;
    final private int sequence;
    final private int year;
    final private String value;

    public ContractNo(final int contractNo) {
        if (contractNo < YEAR_MASK)
            throw new IllegalArgumentException("contract number value too low'" + contractNo + "'");
        sequence = contractNo / YEAR_MASK;
        if (sequence > MAX_SEQUENCE)
            throw new IllegalArgumentException("contract number value too big '" + contractNo + "'");
        year = contractNo % YEAR_MASK;
        value = "" + contractNo;
    }

    public ContractNo(final String contractNo) {
        this(Integer.valueOf(contractNo));
    }

    public static ContractNo contractNo(final int contractNo) {
        return new ContractNo(contractNo);
    }

    public static ContractNo contractNo(final String contractNo) {
        return new ContractNo(contractNo);
    }

    public int sequence() {
        return sequence;
    }

    public int year() {
        return year;
    }

    public int value() {
        return Integer.valueOf(value);
    }
    
    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContractNo that = (ContractNo) o;

        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
