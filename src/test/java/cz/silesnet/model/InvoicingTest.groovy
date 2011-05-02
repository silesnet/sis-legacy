package cz.silesnet.model

import spock.lang.Specification

/**
 * User: der3k
 * Date: 10.4.11
 * Time: 21:56
 */
class InvoicingTest extends Specification {

  def 'fails when setting numbering base twice'() {
    def invoicing = new Invoicing()
    invoicing.setNumberingBase('2011000')
  when:
    invoicing.setNumberingBase('2011000')
  then:
    thrown IllegalStateException
  }
}
