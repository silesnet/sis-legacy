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
    private static final long BILLED_TO = new GregorianCalendar(2011, Calendar.DECEMBER, 31).timeInMillis
    private static final int UPLOAD = 2
    private static final int DOWNLOAD = 4
    private static final String NAME = "Wireless"
    private static final int PRICE = 20
    private static final int CUSTOMER_ID = 201
    private static final int SERVICE_ID = 1020110
    private static final int SERVICE_ID2 = 1020111
    private static final int CONTRACT_NO = 102011
    private static final String CONTRACT_STR = '102011'
    private static final String UNIQUE_FOO = '?102011'
    private static final String BPS = 'M'
    private static final String RESPONSIBLE = 'TECH'
    private static final Label RESPONSIBLE_LABEL = new Label();
    private static final String FAKE_CONTRACT_NO = 'X'
    private static final int SERVICE_ID_PL = 1001020110

    def 'initializes new customer from blueprint'() {
        def blueprint = new ServiceBlueprint()
        blueprint.id = SERVICE_ID
        blueprint.periodFrom = new Date(FROM)
        blueprint.info = INFO
        blueprint.customerId = 0
        def customer = blueprint.initializeNewCustomer(RESPONSIBLE_LABEL)
        def address = customer.contact.address
        def billing = customer.billing
    expect:
        customer.id == null
        customer.name == UNIQUE_FOO
        customer.storedContractNo == CONTRACT_STR
        customer.publicId == CONTRACT_STR
        address.street == UNIQUE_FOO
        address.city == UNIQUE_FOO
        address.postalCode == UNIQUE_FOO
        address.country == Country.CZ
        billing.lastlyBilled.time == BILLED_TO
        billing.frequency == Frequency.MONTHLY
        !billing.isBilledAfter
        !billing.deliverByMail
        billing.deliverByEmail
        billing.format == InvoiceFormat.LINK
        !billing.deliverSigned
        billing.responsible == RESPONSIBLE_LABEL
        billing.isActive
        billing.status == BillingStatus.INVOICE
        billing.variableSymbol == CONTRACT_NO
        customer.info == INFO
        customer.insertedOn.time > FROM
        blueprint.isNewCustomerCreated()
    }

    def 'initializes new customer country from blueprint'() {
        def blueprint = new ServiceBlueprint()
        blueprint.id = SERVICE_ID_PL
        blueprint.periodFrom = new Date(FROM)
        blueprint.info = INFO
        def customer = blueprint.initializeNewCustomer(RESPONSIBLE_LABEL)
        def address = customer.contact.address
    expect:
        address.country == Country.PL
    }

    def 'cannot initialize new customer service order is not zero'() {
        def blueprint = new ServiceBlueprint()
        blueprint.id = SERVICE_ID2
    when:
        blueprint.initializeNewCustomer(RESPONSIBLE_LABEL)
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
        !service.connectivity.isAggregated
        service.connectivity.aggregationId == null
        service.period.from.time == FROM
        service.period.to == null
        service.info == INFO
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

    def 'imprinting first contract service on existing customer appends contract no'() {
        def blueprint = blueprintFixture()
        def customer = new Customer()
        customer.id = CUSTOMER_ID;
        blueprint.buildService(customer)
        customer.contractNo = null
        blueprint.imprintNewServiceOn(customer)
        def first = customer.storedContractNo
        blueprint.imprintNewServiceOn(customer)
        def second = customer.storedContractNo
    expect:
        first == CONTRACT_STR
        second == "$CONTRACT_STR, $CONTRACT_STR"
    }

    def 'imprinting first contract service on new customer does not change contract no'() {
        def blueprint = blueprintFixture()
        blueprint.customerId = null;
        def customer = new Customer()
        customer.id = CUSTOMER_ID;
        blueprint.buildService(customer)
        customer.contractNo = FAKE_CONTRACT_NO
        blueprint.imprintNewServiceOn(customer)
        def first = customer.storedContractNo
        blueprint.imprintNewServiceOn(customer)
        def second = customer.storedContractNo
    expect:
        first == FAKE_CONTRACT_NO
        second == FAKE_CONTRACT_NO
    }

    def 'imprinting following contract service on customer does not change contract no'() {
        def blueprint = blueprintFixture()
        blueprint.id = SERVICE_ID2
        def customer = new Customer()
        customer.id = CUSTOMER_ID;
        blueprint.buildService(customer)
        customer.contractNo = FAKE_CONTRACT_NO
        blueprint.imprintNewServiceOn(customer)
        def first = customer.storedContractNo
        blueprint.imprintNewServiceOn(customer)
        def second = customer.storedContractNo
    expect:
        first == FAKE_CONTRACT_NO
        second == FAKE_CONTRACT_NO
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
        blueprint.isNewCustomer()
    }

    def 'detects new contract for existing customer'() {
        def blueprint = new ServiceBlueprint()
        blueprint.id = SERVICE_ID
        blueprint.customerId = CUSTOMER_ID
    expect:
        blueprint.isNewContract()
        !blueprint.isNewCustomer()
    }

    def 'detects existing contract for existing customer'() {
        def blueprint = new ServiceBlueprint()
        blueprint.id = SERVICE_ID2
        blueprint.customerId = CUSTOMER_ID
    expect:
        !blueprint.isNewContract()
        !blueprint.isNewCustomer()
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
