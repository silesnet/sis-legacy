package cz.silesnet.model.invoice;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * User: der3k
 * Date: 11.3.11
 * Time: 20:56
 */
public class BillingContextBean implements BillingContext {
  private static final int PERCENTAGE_SCALE = 2;
  private BigDecimal vatRate;
  private Rounding vatRounding;
  private Rounding totalRounding;
  private int purgeDays;

  public void setVatPercent(final int vatPercent) {
    this.vatRate = BigDecimal.valueOf(vatPercent, PERCENTAGE_SCALE);
  }

  public void setVatRounding(final Rounding rounding) {
    this.vatRounding = rounding;
  }

  public void setTotalRounding(final Rounding rounding) {
    this.totalRounding = rounding;
  }

  public void setPurgeDays(final int purgeDays) {
    this.purgeDays = purgeDays;
  }

  public Amount calculateVatFor(final Amount amount) {
    return Amount.of(vatRounding.round(amount.value().multiply(vatRate)));
  }

  public Amount roundTotalOf(final Amount amount) {
    return Amount.of(totalRounding.round(amount.value()));
  }

  public Date purgeDateFor(final Date date) {
    Calendar purgeDate = GregorianCalendar.getInstance();
    purgeDate.setTime(date);
    purgeDate.add(Calendar.DATE, purgeDays);
    return purgeDate.getTime();
  }

}
