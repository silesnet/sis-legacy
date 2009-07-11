package cz.silesnet;

import junit.framework.Test;
import junit.framework.TestSuite;
import cz.silesnet.dao.BillDAOTest;
import cz.silesnet.dao.CustomerDAOTest;
import cz.silesnet.dao.HistoryItemDAOTest;
import cz.silesnet.dao.LabelDAOTest;
import cz.silesnet.dao.NodeDAOTest;
import cz.silesnet.dao.ServiceDAOTest;
import cz.silesnet.dao.SettingDAOTest;
import cz.silesnet.dao.UserDAOTest;
import cz.silesnet.model.CustomerDeactivateTest;
import cz.silesnet.model.PeriodTest;
import cz.silesnet.model.ServiceTest;
import cz.silesnet.model.WirelessTest;
import cz.silesnet.service.AcegiAuthenticateTest;
import cz.silesnet.service.BillingManagerBillsTest;
import cz.silesnet.service.BillingManagerTest;
import cz.silesnet.service.CustomerManagerTest;
import cz.silesnet.service.HistoryManagerTest;
import cz.silesnet.service.NodeManagerTest;
import cz.silesnet.service.SettingManagerTest;
import cz.silesnet.service.UserManagerTest;
import cz.silesnet.utils.DiffUtilsTest;
import cz.silesnet.utils.HistoricToStringTest;
import cz.silesnet.utils.MessagesUtilsTest;
import cz.silesnet.utils.NavigationUtilsTest;
import cz.silesnet.utils.NodeRegexpTest;
import cz.silesnet.utils.SearchUtilsTest;

/**
 * Run all tests at once.
 * 
 * @author Richard Sikora
 */
public class AllTests {

	// ~ Methods
	// ----------------------------------------------------------------

	public static Test suite() {
		TestSuite suite = new TestSuite("Tests for cz.silesnet");
		// $JUnit-BEGIN$

		// cz.silesnet.dao tests
		suite.addTestSuite(BillDAOTest.class);
		suite.addTestSuite(CustomerDAOTest.class);
		suite.addTestSuite(HistoryItemDAOTest.class);
		suite.addTestSuite(LabelDAOTest.class);
		suite.addTestSuite(NodeDAOTest.class);
		suite.addTestSuite(ServiceDAOTest.class);
		suite.addTestSuite(SettingDAOTest.class);
		suite.addTestSuite(UserDAOTest.class);

		// cz.silesnet.model tests
		suite.addTestSuite(CustomerDeactivateTest.class);
		suite.addTestSuite(PeriodTest.class);
		suite.addTestSuite(ServiceTest.class);
		suite.addTestSuite(WirelessTest.class);

		// cz.silesnet.service tests
		suite.addTestSuite(AcegiAuthenticateTest.class);
		suite.addTestSuite(BillingManagerBillsTest.class);
		suite.addTestSuite(BillingManagerTest.class);
		suite.addTestSuite(CustomerManagerTest.class);
		// AspectJ is turned OFF so this test is not performed
		// suite.addTestSuite(CustomerControllerTest.class);
		suite.addTestSuite(HistoryManagerTest.class);
		suite.addTestSuite(NodeManagerTest.class);
		suite.addTestSuite(SettingManagerTest.class);
		suite.addTestSuite(UserManagerTest.class);

		// cz.silesnet.utils tests
		suite.addTestSuite(DiffUtilsTest.class);
		suite.addTestSuite(HistoricToStringTest.class);
		suite.addTestSuite(MessagesUtilsTest.class);
		suite.addTestSuite(NavigationUtilsTest.class);
		suite.addTestSuite(NodeRegexpTest.class);
		suite.addTestSuite(SearchUtilsTest.class);

		// $JUnit-END$
		return suite;
	}
}