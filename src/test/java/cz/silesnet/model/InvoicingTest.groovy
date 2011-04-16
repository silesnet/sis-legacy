package cz.silesnet.model

import spock.lang.Specification

/**
 * User: der3k
 * Date: 10.4.11
 * Time: 21:56
 */
class InvoicingTest extends Specification {

  def 'generates next bill number'() {
    def invoicing = new Invoicing()
    invoicing.setNumberingBase('2011000')
  expect:
    invoicing.nextBillNumber() == 2011001L
    invoicing.nextBillNumber() == 2011002L
  }

  def 'fails when setting numbering base twice'() {
    def invoicing = new Invoicing()
    invoicing.setNumberingBase('2011000')
  when:
    invoicing.setNumberingBase('2011000')
  then:
    thrown IllegalStateException
  }

  def 'next bill number fail when numbering base not set'() {
    def invoicing = new Invoicing()
  when:
    invoicing.nextBillNumber()
  then:
    thrown IllegalStateException
  }
}
