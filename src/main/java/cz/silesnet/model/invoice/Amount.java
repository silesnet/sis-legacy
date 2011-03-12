package cz.silesnet.model.invoice;

import java.math.BigDecimal;

/**
 * User: der3k
 * Date: 12.3.11
 * Time: 10:20
 */
public class Amount implements Comparable<Amount> {
  private static final Rounding ROUNDING = Rounding.SCALE_OF_2_HALF_UP_ROUNDING;
  private final BigDecimal value;

  public static Amount of(final BigDecimal number) {
    return new Amount(number);
  }

  public static Amount of(final String number) {
    return new Amount(new BigDecimal(number));
  }

  public static Amount of(final Number number) {
    return of(number.toString());
  }

  private Amount(final BigDecimal value) {
    if (value == null)
      throw new IllegalArgumentException("value can not be null");
    this.value = ROUNDING.round(value);
  }

  public BigDecimal value() {
    return value;
  }

  public int compareTo(final Amount other) {
    return this.value.compareTo(other.value);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final Amount amount = (Amount) o;

    return value.equals(amount.value);
  }

  @Override
  public int hashCode() {
    return value != null ? value.hashCode() : 0;
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
