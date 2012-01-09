package cz.silesnet.model;


import spock.lang.Specification

/**
 * User: admin
 * Date: 8.1.12
 * Time: 21:50
 */
public class ServiceBlueprintTest extends Specification {

    def "builds service with price"() {
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
    }

}
