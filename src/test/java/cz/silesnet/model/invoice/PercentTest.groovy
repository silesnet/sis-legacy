package cz.silesnet.model.invoice

import spock.lang.Specification

/**
 * User: der3k
 * Date: 18.3.11
 * Time: 20:32
 */
class PercentTest extends Specification {
  def 'is created from integer'() {
  expect:
    Percent.rate(19) != null
  }

  def 'can not be created from negative integer'() {
  when:
    Percent.rate(-1)
  then:
    thrown IllegalArgumentException
  }

  def 'calculates percentage of amount'() {
  expect:
    Percent.ZERO.of(Amount.HUNDRED) == Amount.ZERO
    Percent.ONE.of(Amount.of('0.4')) == Amount.of('0.00')
    Percent.ONE.of(Amount.of('0.5')) == Amount.of('0.01')
    Percent.ONE.of(Amount.HUNDRED) == Amount.ONE
    Percent.TEN.of(Amount.HUNDRED) == Amount.TEN
    Percent.HUNDRED.of(Amount.of('0.01')) == Amount.of('0.01')
    Percent.rate(200).of(Amount.ONE) == Amount.of('2.00')
    Percent.rate(19).of(Amount.THOUSAND) == Amount.of('190.00')
  }

  def 'has predefined values'() {
  expect:
    percent.rate == rate
  where:
    percent | rate
    Percent.ZERO | 0.00
    Percent.ONE | 0.01
    Percent.TEN | 0.10
    Percent.FIFTY | 0.50
    Percent.HUNDRED | 1.00
  }

  def 'adds another percent'() {
  expect:
    Percent.ONE.plus(Percent.ONE) == Percent.rate(2)
    Percent.ZERO.plus(Percent.ZERO) == Percent.ZERO
    Percent.ZERO.plus(Percent.HUNDRED) == Percent.HUNDRED
  }

  def 'implements toString()'() {
  expect:
    Percent.HUNDRED.toString() == '100 %'
  }


}
