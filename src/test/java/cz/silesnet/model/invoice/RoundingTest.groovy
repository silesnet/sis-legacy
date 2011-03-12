package cz.silesnet.model.invoice

import spock.lang.Specification

/**
 * User: der3k
 * Date: 12.3.11
 * Time: 15:34
 */
class RoundingTest extends Specification {
  def 'actual rounding is done only when scale differs'() {
    def value = BigDecimal.valueOf(1, scale) 
  expect:
    rounding.round(value).is(value)
  where:
    rounding | scale
    Rounding.SCALE_OF_0_HALF_UP_ROUNDING | 0
    Rounding.SCALE_OF_2_HALF_UP_ROUNDING | 2
  }
}
