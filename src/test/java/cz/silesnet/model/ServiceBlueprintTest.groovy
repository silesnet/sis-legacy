package cz.silesnet.model;


import cz.silesnet.model.enums.BillingStatus
import cz.silesnet.model.enums.Country
import cz.silesnet.model.enums.Frequency
import cz.silesnet.model.enums.InvoiceFormat
import spock.lang.Specification

/**
 * User: admin
 * Date: 8.1.12
 * Time: 21:50
 */
public class ServiceBlueprintTest extends Specification {

    private static final String INFO = 'INFO'
    private static final long FROM = new GregorianCalendar(2012, Calendar.JANUARY, 12).timeInMillis
    private static final long BILLED_TO = new GregorianCalendar(2012, Calendar.JANUARY, 31).timeInMillis
    private static final int UPLOAD = 2
    private static final int DOWNLOAD = 4
    private static final String NAME = "Wireless"
    private static final int PRICE = 20
    private static final int CUSTOMER_ID = 201
    private static final int SERVICE_ID = 10001001
    private static final int SERVICE_ID2 = 10001002
    private static final int CONTRACT_NO = 10
    private static final String CONTRACT_STR = '10'
    private static final String BPS = 'M'
    private static final String RESPONSIBLE = 'TECH'
    private static final String FAKE_CONTRACT_NO = 'X'
    private static final int SERVICE_ID_PL = 20001001

    def 'initializes new customer from blueprint'() {
        def blueprint = new ServiceBlueprint()
        blueprint.id = SERVICE_ID
        blueprint.periodFrom = new Date(FROM)
        blueprint.info = INFO
        blueprint.customerId = 0
        def customer = blueprint.createNewCustomer()
        def address = customer.contact.address
        def billing = customer.billing
    expect:
        customer.id == null
        customer.name == INFO
        customer.publicId == CONTRACT_STR
        address.street == null
        address.city == null
        address.postalCode == null
        address.country == Country.CZ
        billing.lastlyBilled.time == BILLED_TO
        billing.frequency == Frequency.MONTHLY
        !billing.isBilledAfter
        !billing.deliverByMail
        billing.deliverByEmail
        billing.format == InvoiceFormat.LINK
        !billing.deliverSigned
        billing.isActive
        billing.status == BillingStatus.INVOICE
        billing.variableSymbol == CONTRACT_NO
        customer.info == null
        customer.insertedOn.time > FROM
        blueprint.isNewCustomerCreated()
    }

    def 'initializes new customer country from blueprint'() {
        def blueprint = new ServiceBlueprint()
        blueprint.id = SERVICE_ID_PL
        blueprint.periodFrom = new Date(FROM)
        blueprint.info = INFO
        def customer = blueprint.createNewCustomer()
        def address = customer.contact.address
    expect:
        address.country == Country.PL
    }

    def 'cannot initialize new customer service order is not zero'() {
        def blueprint = new ServiceBlueprint()
        blueprint.id = SERVICE_ID2
    when:
        blueprint.createNewCustomer()
    then:
        thrown IllegalStateException
    }

    def "builds service for new customer"() {
        def blueprint = blueprintFixture()
        blueprint.customerId = 0
        def customer = new Customer()
        customer.id = CUSTOMER_ID;
        def service = blueprint.buildService(customer)
    expect:
        service.id == SERVICE_ID
        service.customerId == CUSTOMER_ID
        service.price == PRICE
        service.name == NAME
        service.additionalName == null
        service.connectivity.download == DOWNLOAD
        service.connectivity.upload == UPLOAD
        service.connectivity.bps == BPS

        service.period.from.time == FROM
        service.period.to == null
        service.info == null
        service.frequency == Frequency.MONTHLY;
    }

    def 'cannot build service when country mismatch'() {
        def blueprint = blueprintFixture()
        blueprint.id = SERVICE_ID_PL
        def customer = new Customer()
        customer.id = CUSTOMER_ID;
        customer.contact.address.country = Country.CZ
    when:
        blueprint.buildService(customer)
    then:
        thrown IllegalStateException
    }

    def 'builds service for existing customer'() {
        def blueprint = blueprintFixture()
        def customer = new Customer()
        customer.id = CUSTOMER_ID;
        def service = blueprint.buildService(customer)
    expect:
        service.customerId == CUSTOMER_ID
    }

    def 'imprinting new service on customer activates customer'() {
        def blueprint = blueprintFixture()
        def customer = new Customer()
        customer.id = CUSTOMER_ID;
        customer.billing.isActive = false
        blueprint.buildService(customer)
        assert !customer.billing.isActive
        blueprint.imprintNewServiceOn(customer)
    expect:
        customer.billing.isActive
    }

    def 'detects new contract for new customer'() {
        def blueprint = new ServiceBlueprint()
        blueprint.id = SERVICE_ID
        blueprint.customerId = 0
    expect:
        blueprint.isNewContract()
        blueprint.shouldCreateNewCustomer()
    }

    def 'detects new contract for existing customer'() {
        def blueprint = new ServiceBlueprint()
        blueprint.id = SERVICE_ID
        blueprint.customerId = CUSTOMER_ID
    expect:
        blueprint.isNewContract()
        !blueprint.shouldCreateNewCustomer()
    }

    def 'detects existing contract for existing customer'() {
        def blueprint = new ServiceBlueprint()
        blueprint.id = SERVICE_ID2
        blueprint.customerId = CUSTOMER_ID
    expect:
        !blueprint.isNewContract()
        !blueprint.shouldCreateNewCustomer()
    }

    def 'should set connectivity kbps for magic value 512'() {
        def blueprint = blueprintFixture()
        blueprint.download = 512
        blueprint.upload = 512
        def customer = new Customer()
        customer.id = CUSTOMER_ID;
        def service = blueprint.buildService(customer)
    expect:
        service.connectivity.bps == 'k'
    }

    def static blueprintFixture() {
        def blueprint = new ServiceBlueprint()
        blueprint.id = SERVICE_ID
        blueprint.customerId = CUSTOMER_ID
        blueprint.price = PRICE
        blueprint.name = NAME
        blueprint.download = DOWNLOAD
        blueprint.upload = UPLOAD
        blueprint.responsible = RESPONSIBLE
        blueprint.periodFrom = new Date(FROM)
        blueprint.info = INFO
        blueprint
    }
}
