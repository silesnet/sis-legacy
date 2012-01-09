package cz.silesnet.model;


import cz.silesnet.model.enums.Country
import spock.lang.Specification
import static cz.silesnet.model.ContractNo.contractNo
import static cz.silesnet.model.ServiceId.serviceId

/**
 * User: admin
 * Date: 9.1.12
 * Time: 23:20
 */
public class ServiceIdTest extends Specification {

    def 'instantiates from integer'() {
        def id = serviceId(1020100)
    expect:
        id.id() == 1020100
    }

    def 'cannot instantiate from too small value'() {
        when: serviceId(99999)
        then: thrown IllegalArgumentException
    }

    def 'cannot instantiate from too big value'() {
        when: serviceId(2000000000)
        then: thrown IllegalArgumentException
    }

    def 'recognizes country'() {
    expect:
        serviceId(id).country() == country
    where:
        id | country
        1020120 | Country.CZ
        1001020120 | Country.PL
    }

    def 'recognizes contract'() {
    expect:
        serviceId(id).contractNo() == contractNo
    where:
        id | contractNo
        1020120 | contractNo(102012)
        1001020120 | contractNo(102012)
    }

    def 'recognizes order'() {
    expect:
        serviceId(id).orderNo() == orderNo
    where:
        id | orderNo
        1020120 | 0
        1001020120 | 0
        1020129 | 9
        1001020129 | 9
    }

    def 'has original id'() {
    expect:
        serviceId(id).id() == id
    where:
        id << [1020120, 1001020120, 1020129, 1001020129]
    }
}
