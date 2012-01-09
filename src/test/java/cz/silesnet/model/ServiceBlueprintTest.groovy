package cz.silesnet.model;


import spock.lang.Specification

/**
 * User: admin
 * Date: 8.1.12
 * Time: 21:50
 */
public class ServiceBlueprintTest extends Specification {

    def "builds service for existing customer"() {
        def blueprint = new ServiceBlueprint()
        blueprint.id = 1020110
        blueprint.customerId = 201
        blueprint.name = "Wireless"
        blueprint.download = 4
        blueprint.upload = 2
        blueprint.periodFrom = new Date()
        blueprint.info = 'INFO'
        def service = blueprint.buildService(20)
    expect:
        service.id == 1020110
        service.customerId == 201
        service.price == 20
        service.name == 'Wireless'
        service.additionalName == null
        service.connectivity.download == 4
        service.connectivity.upload == 2
        service.connectivity.bps == 'M'
        !service.connectivity.isAggregated
        service.connectivity.aggregationId == null
        service.period.from.getTime() <= new Date().getTime()
        service.period.to == null
        service.info == 'INFO'
    }

    def 'cannot build when existing contract and no customer id set'() {
        def blueprint = new ServiceBlueprint()
        blueprint.id = 1020121
        blueprint.customerId = null
    when:
        blueprint.buildService(10)
    then:
        thrown IllegalStateException
    }

    def 'detects new contract for new customer'() {
        def blueprint = new ServiceBlueprint()
        blueprint.id = 1020120
        blueprint.customerId = null
    expect:
        blueprint.isNewContract()
        blueprint.isNewCustomer()
    }

    def 'detects new contract for existing customer'() {
        def blueprint = new ServiceBlueprint()
        blueprint.id = 1020120
        blueprint.customerId = 1
    expect:
        blueprint.isNewContract()
        !blueprint.isNewCustomer()
    }

    def 'detects existing contract for existing customer'() {
        def blueprint = new ServiceBlueprint()
        blueprint.id = 1020121
        blueprint.customerId = 1
    expect:
        !blueprint.isNewContract()
        !blueprint.isNewCustomer()
    }


}
