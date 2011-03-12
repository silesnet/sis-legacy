package cz.silesnet.model.invoice;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * User: der3k
 * Date: 12.3.11
 * Time: 15:03
 */
public enum Rounding {
  SCALE_OF_2_HALF_UP_ROUNDING(2, RoundingMode.HALF_UP),
  SCALE_OF_0_HALF_UP_ROUNDING(0, RoundingMode.HALF_UP);

  private final int scale;
  private final RoundingMode roundingMode;

  private Rounding(final int scale, final RoundingMode roundingMode) {
    this.scale = scale;
    this.roundingMode = roundingMode;
  }

  public BigDecimal round(BigDecimal value) {
    return value.setScale(scale, roundingMode);
  }
}
