package cz.silesnet.model;


import spock.lang.Specification
import static cz.silesnet.model.ContractNo.contractNo

/**
 * User: admin
 * Date: 9.1.12
 * Time: 21:52
 */
public class ContractNoTest extends Specification {
    private final static int MIN_CONTRACT_NO = 1
    private final static int MAX_CONTRACT_NO = 99999

    def 'instantiates from integer'() {
        def contractNo = new ContractNo(MIN_CONTRACT_NO)
    expect:
        contractNo.value() == MIN_CONTRACT_NO
    }

    def 'instantiates from string'() {
        def contractNo = new ContractNo('' + MAX_CONTRACT_NO)
    expect:
        contractNo.value() == MAX_CONTRACT_NO
    }

    def 'cannot instantiate from too small number'() {
    when: contractNo(0)
    then: thrown IllegalArgumentException
    }

    def 'cannot instantiate from too big number'() {
    when: contractNo(100000)
    then: thrown IllegalArgumentException
    }

    def 'string representation does not have leading zeros'() {
    expect:
        contractNo('000' + MIN_CONTRACT_NO).toString() == '' + MIN_CONTRACT_NO
    }
}
