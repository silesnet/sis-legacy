package cz.silesnet.model;


import cz.silesnet.model.enums.Country
import spock.lang.Specification
import static cz.silesnet.model.ContractNo.contractNo
import static cz.silesnet.model.ServiceId.*

/**
 * User: admin
 * Date: 9.1.12
 * Time: 23:20
 */
public class ServiceIdTest extends Specification {

    private static final int CZ_NEW = 10000101
    private static final int CZ_NEXT = 10000102
    private static final int CZ_MAX_LAST = 19999999
    private static final int CZ_ONETIME = 110000101
    private static final int CZ_ONETIME_MAX_LAST = 119999999
    private static final int CZ_INVALID_ORDER = 10000100
    private static final int PL_NEW = 20000101
    private static final int PL_NEXT = 20000102
    private static final int PL_ONETIME = 120000101
    private static final int PL_MAX_LAST = 29999999
    private static final int PL_ONETIME_MAX_LAST = 129999999
    private static final int PL_INVALID_ORDER = 20000100

    private static final ContractNo CONTRACT_NO_1 = contractNo(ORDER_FIRST)
    private static final ContractNo CONTRACT_NO_99999 = contractNo(99999)
    private static final int ORDER_FIRST = 1
    private static final int ORDER_NEXT = 2
    private static final int ORDER_LAST = 99

    def 'should instantiate from integer'() {
    expect:
        serviceId(CZ_NEW)
        new ServiceId(CZ_NEW)
    }

    def 'should recognize onetime service'() {
    expect:
        serviceId(id).isOnetime() == isOnetime
    where:
        id         | isOnetime
        CZ_NEW     | false
        CZ_NEXT    | false
        CZ_ONETIME | true
        PL_NEW     | false
        PL_NEXT    | false
        PL_ONETIME | true
    }

    def 'should return service country'() {
    expect:
        serviceId(id).country() == country
    where:
        id         | country
        CZ_NEW     | Country.CZ
        CZ_NEXT    | Country.CZ
        CZ_ONETIME | Country.CZ
        PL_NEW     | Country.PL
        PL_NEXT    | Country.PL
        PL_ONETIME | Country.PL
    }

    def 'should return service contract number'() {
    expect:
        serviceId(id).contractNo() == contractNo
    where:
        id                  | contractNo
        CZ_NEW              | CONTRACT_NO_1
        CZ_NEXT             | CONTRACT_NO_1
        CZ_ONETIME          | CONTRACT_NO_1
        PL_NEW              | CONTRACT_NO_1
        PL_NEXT             | CONTRACT_NO_1
        PL_ONETIME          | CONTRACT_NO_1
        CZ_MAX_LAST         | CONTRACT_NO_99999
        PL_MAX_LAST         | CONTRACT_NO_99999
        CZ_ONETIME_MAX_LAST | CONTRACT_NO_99999
        PL_ONETIME_MAX_LAST | CONTRACT_NO_99999
    }

    def 'should return service order'() {
    expect:
        serviceId(id).orderNo() == orderNo
    where:
        id                  | orderNo
        CZ_NEW              | ORDER_FIRST
        CZ_NEXT             | ORDER_NEXT
        CZ_ONETIME          | ORDER_FIRST
        PL_NEW              | ORDER_FIRST
        PL_NEXT             | ORDER_NEXT
        PL_ONETIME          | ORDER_FIRST
        CZ_MAX_LAST         | ORDER_LAST
        PL_MAX_LAST         | ORDER_LAST
        CZ_ONETIME_MAX_LAST | ORDER_LAST
        PL_ONETIME_MAX_LAST | ORDER_LAST
    }

    def 'should return original id'() {
    expect:
        serviceId(id).id() == id
    where:
        id << [CZ_NEW, CZ_NEXT, CZ_ONETIME, CZ_MAX_LAST, CZ_ONETIME_MAX_LAST, PL_NEW, PL_NEXT, PL_ONETIME, PL_MAX_LAST, PL_ONETIME_MAX_LAST]
    }

    def 'should instantiate first regular service from country and contract no'() {
    expect:
        serviceId.id() == id
    where:
        serviceId                                 | id
        firstServiceId(Country.CZ, CONTRACT_NO_1) | CZ_NEW
        firstServiceId(Country.PL, CONTRACT_NO_1) | PL_NEW
    }

    def 'should instantiate first onetime service from country and contract no'() {
    expect:
        serviceId.id() == id
    where:
        serviceId                                        | id
        firstOnetimeServiceId(Country.CZ, CONTRACT_NO_1) | CZ_ONETIME
        firstOnetimeServiceId(Country.PL, CONTRACT_NO_1) | PL_ONETIME
    }

    def 'should instantiate last regular service from country and contract no'() {
    expect:
        serviceId.id() == id
    where:
        serviceId                                 | id
        lastServiceId(Country.CZ, CONTRACT_NO_99999) | CZ_MAX_LAST
        lastServiceId(Country.PL, CONTRACT_NO_99999) | PL_MAX_LAST
    }

    def 'should instantiate last onetime service from country and contract no'() {
    expect:
        serviceId.id() == id
    where:
        serviceId                                        | id
        lastOnetimeServiceId(Country.CZ, CONTRACT_NO_99999) | CZ_ONETIME_MAX_LAST
        lastOnetimeServiceId(Country.PL, CONTRACT_NO_99999) | PL_ONETIME_MAX_LAST
    }

    def 'should return if it has next service id'() {
    expect:
        serviceId(CZ_NEW).hasNext()
        !serviceId(CZ_MAX_LAST).hasNext()
        serviceId(CZ_ONETIME).hasNext()
        !serviceId(CZ_ONETIME_MAX_LAST).hasNext()
    }

    def 'should return next service id'() {
    expect:
        serviceId(CZ_NEW).next().id() == CZ_NEXT
        serviceId(PL_NEW).next().id() == PL_NEXT
    }

    def 'should return true when the service is first/new'() {
    expect:
        serviceId(CZ_NEW).isFirst()
        !serviceId(CZ_NEXT).isFirst()
        !serviceId(CZ_MAX_LAST).isFirst()
        serviceId(CZ_ONETIME).isFirst()
        !serviceId(CZ_ONETIME_MAX_LAST).isFirst()
    }

    def 'should fail when asking for next service id from the last one'() {
    expect:
        try {
            serviceId.next()
            assert false
        } catch (IllegalStateException) {
            // OK
        }
        true
    where:
        serviceId << [serviceId(CZ_MAX_LAST), serviceId(CZ_ONETIME_MAX_LAST), serviceId(PL_MAX_LAST), serviceId(PL_ONETIME_MAX_LAST)]
    }

    def 'should not instantiate from invalid id value'() {
    expect:
        try {
            serviceId(id)
            assert false
        } catch (IllegalArgumentException) {
            // OK
        }
        true
    where:
        id << [9999999, 10000099, 10000100, 9999999, 20000099, 20000100, 30000000, 30000101, 109999999, 110000099, 110000100, 120000099, 120000100, 130000000, 130000101]
    }

    def 'should not instantiate from country and contract number with invalid service order'() {
    expect:
        try {
            serviceId(Country.CZ, CONTRACT_NO_1, order)
            assert false
        } catch (IllegalArgumentException) {
            // OK
        }
        true
    where:
        order << [0, 100]
    }

    def 'should return string representation of service id'() {
    expect:
        serviceId(CZ_NEW).toString() == '[country=CZ, contract=1, order=1]'
        serviceId(PL_ONETIME_MAX_LAST).toString() == '[onetime, country=PL, contract=99999, order=99]'
    }
}
