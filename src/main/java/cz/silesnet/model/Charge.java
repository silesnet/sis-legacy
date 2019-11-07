package cz.silesnet.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class Charge {
    public static final Charge ZERO = Charge.of(0, 0, 0);

    private final BigDecimal net;
    private final BigDecimal vat;
    private final BigDecimal brt;

    private Charge(final BigDecimal net, final BigDecimal vat, final BigDecimal brt) {
        if (!brt.equals(net.add(vat))) {
            throw new IllegalArgumentException("invariant check failed: brt = net + vat");
        }
        this.net = net;
        this.vat = vat;
        this.brt = brt;
    }

    public static Charge of(float amount, int unitPrice, int vatPct) {
        try {
            final BigDecimal vatRate = BigDecimal.valueOf(100 + vatPct, 2);
            final BigDecimal brt = BigDecimal.valueOf(amount * unitPrice)
                    .multiply(vatRate).setScale(0, RoundingMode.HALF_UP).setScale(2);
            final BigDecimal net = brt.divide(vatRate, 2, RoundingMode.HALF_UP);
            final BigDecimal vat = brt.subtract(net);
            return new Charge(net, vat, brt);
        } catch (Exception e) {
            return ZERO;
        }
    }

    public static Charge of(int net, int vatRate) {
        return Charge.of(1.0f, net, vatRate);
    }

    public BigDecimal getNet() {
        return net;
    }

    public BigDecimal getVat() {
        return vat;
    }

    public BigDecimal getBrt() {
        return brt;
    }

    public Charge add(Charge other) {
        return new Charge(this.net.add(other.net), this.vat.add(other.vat), this.brt.add(other.brt));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Charge charge = (Charge) o;
        return net.equals(charge.net) &&
                vat.equals(charge.vat) &&
                brt.equals(charge.brt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(net, vat, brt);
    }

    @Override
    public String toString() {
        return "Charge{" +
                "net=" + net +
                ", vat=" + vat +
                ", brt=" + brt +
                '}';
    }
}
