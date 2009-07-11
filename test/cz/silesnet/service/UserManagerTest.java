package cz.silesnet.service;

import cz.silesnet.model.User;

import java.util.ArrayList;

/**
 * User Manager tests. Testing Loggin in.
 * 
 * @author Richard Sikora
 * 
 */
public class UserManagerTest extends BaseServiceTestCase {

	// ~ Instance fields
	// --------------------------------------------------------

	private UserManager umgr = null;

	// ~ Methods
	// ----------------------------------------------------------------

	public void testGetUsers() {
		ArrayList<User> users = (ArrayList<User>) umgr.getUsers();

		for (User u : users)
			log.debug(u);
	}

	protected void setUp() throws Exception {
		// get user manager class instantiated by spring
		// according to applicationContext
		umgr = (UserManager) ctx.getBean("userManager");
		assertNotNull(umgr);
	}
}