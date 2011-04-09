package cz.silesnet.model.invoice;

import java.math.BigDecimal;

/**
 * User: der3k
 * Date: 18.3.11
 * Time: 20:29
 */
public class Percent {
  public static final Percent ZERO = Percent.rate(0);
  public static final Percent ONE = Percent.rate(1);
  public static final Percent TEN = Percent.rate(10);
  public static final Percent FIFTY = Percent.rate(50);
  public static final Percent HUNDRED = Percent.rate(100);

  private final BigDecimal rate;

  public static Percent rate(int rate) {
    return new Percent(rate);
  }

  private Percent(final int rate) {
    if (rate < 0)
      throw new IllegalArgumentException("trying to create negative percentage from " + rate);
    this.rate = BigDecimal.valueOf(rate).movePointLeft(2);
  }

  private Percent(final BigDecimal rate) {
    this.rate = rate;
  }

  public Amount of(Amount base) {
    return Amount.of(base.value().multiply(rate));
  }

  public Percent plus(Percent percent) {
    return new Percent(this.rate.add(percent.rate));
  }

  public BigDecimal toBigDecimal() {
    return rate;
  }

  @Override
  public String toString() {
    return String.format("%d %%", rate.movePointRight(2).intValue());
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (!(o instanceof Percent)) return false;

    final Percent percent = (Percent) o;

    if (rate != null ? !rate.equals(percent.rate) : percent.rate != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return rate != null ? rate.hashCode() : 0;
  }
}
