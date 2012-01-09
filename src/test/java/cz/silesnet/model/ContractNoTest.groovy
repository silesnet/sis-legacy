package cz.silesnet.model;


import spock.lang.Specification
import static cz.silesnet.model.ContractNo.contractNo

/**
 * User: admin
 * Date: 9.1.12
 * Time: 21:52
 */
public class ContractNoTest extends Specification {
    def 'instantiates from integer'() {
        def contractNo = new ContractNo(102010)
    expect:
        contractNo.sequence() == 10
        contractNo.year() == 2010
        contractNo.value() == 102010
    }

    def 'instantiates from string'() {
        def contractNo = new ContractNo('102010')
    expect:
        contractNo.sequence() == 10
        contractNo.year() == 2010
        contractNo.value() == 102010
    }

    def 'cannot instantiate from too small number'() {
    when: contractNo(9999)
    then: thrown IllegalArgumentException
    }

    def 'cannot instantiate from too big number'() {
    when: contractNo(100000000)
    then: thrown IllegalArgumentException
    }

    def 'string representation does not have leading zeros'() {
    expect:
        contractNo('00012012').toString() == '12012'
    }
}
