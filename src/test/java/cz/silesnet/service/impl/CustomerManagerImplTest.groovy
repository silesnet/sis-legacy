package cz.silesnet.service.impl;


import cz.silesnet.dao.CustomerDAO
import cz.silesnet.dao.LabelDAO
import cz.silesnet.dao.ServiceDAO
import cz.silesnet.model.ServiceBlueprint
import cz.silesnet.service.HistoryManager
import spock.lang.Specification
import cz.silesnet.model.Customer

/**
 * User: admin
 * Date: 12.1.12
 * Time: 20:59
 */
public class CustomerManagerImplTest extends Specification {

    private static final int ID = 1800020120
    private static final String NAME = 'Service from blueprint'
    private static final int PRICE = 200
    private static final String RESPONSIBLE = 'Technik'
    private static final String INFO = 'Blueprint info'
    private static final int CUSTOMER_ID = 1
    private static final int HISTORY_ID = 101

    def 'should add service and customer from blueprint'() {
        def serviceDao = Mock(ServiceDAO)
        def customerDao = Mock(CustomerDAO)
        def labelDao = Mock(LabelDAO)
        def historyManager = Mock(HistoryManager)

        def customerManager = new CustomerManagerImpl()
        customerManager.serviceDAO = serviceDao
        customerManager.customerDAO = customerDao
        customerManager.labelDAO = labelDao
        customerManager.historyManager = historyManager

        def blueprint = new ServiceBlueprint()
        blueprint.id = ID
        blueprint.name = NAME
        blueprint.price = PRICE
        def PERIOD_FROM = new Date()
        blueprint.setPeriodFrom(PERIOD_FROM)
        blueprint.responsible = RESPONSIBLE
        blueprint.info = INFO

        serviceDao.findBlueprint(ID) >> blueprint
        historyManager.getNewHistoryId() >> HISTORY_ID
        customerDao.get(CUSTOMER_ID) >> {
            def customer = new Customer()
            customer.historyId = HISTORY_ID
            customer
        }
    when:
        customerManager.addService(ID)
    then:
        (1..2) * customerDao.save( { customer ->
            customer.id = CUSTOMER_ID
            println "NEW CUSTOMER:\n$customer"
            customer.services.size() == 0
        })

        1 * serviceDao.save( { service ->
            service.id == ID
            println "NEW SERVICE:\n$service"
            service.customerId == CUSTOMER_ID
        })

        1 * customerDao.save( { customer ->
            customer.id = CUSTOMER_ID
            println "UPDATED CUSTOMER:\n$customer"
            customer.services.size() == 1
        })

        1 * serviceDao.saveBlueprint({ bp ->
            println "UPDATED BLUEPRINT:\n$bp"
            bp.customerId == CUSTOMER_ID && bp.billingOn != null
        })

    }
}

