package cz.silesnet.model.invoice

import spock.lang.Specification

/**
 * User: der3k
 * Date: 12.3.11
 * Time: 10:21
  */
class AmountTest extends Specification {

  def 'scales to 2 with half-up rounding'() {
    def amount = Amount.of(number)
    expect:
      amount.value() == value
    where:
      number | value
      1 | 1
      1.004 | 1
      1.005 | 1.01
  }

  def 'has predefined values'() {
    expect:
      predefined == Amount.of(expected)
    where:
      predefined | expected
      Amount.ZERO | '0.00'
      Amount.ONE | '1.00'
      Amount.TEN | '10.00'
      Amount.HUNDRED | '100.00'
      Amount.THOUSAND | '1000.00'
  }

}
