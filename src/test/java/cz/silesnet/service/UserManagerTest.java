package cz.silesnet.service;

import cz.silesnet.model.User;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static org.testng.Assert.assertNotNull;

/**
 * User Manager tests. Testing Loggin in.
 *
 * @author Richard Sikora
 */
public class UserManagerTest extends BaseServiceTestCase {

    // ~ Instance fields
    // --------------------------------------------------------

    private UserManager umgr = null;

    // ~ Methods
    // ----------------------------------------------------------------

    @Test
    public void testGetUsers() {
        ArrayList<User> users = (ArrayList<User>) umgr.getUsers();

        for (User u : users)
            log.debug(u);
    }

    @BeforeTest
    protected void setUp() throws Exception {
        // get user manager class instantiated by spring
        // according to applicationContext
        umgr = (UserManager) ctx.getBean("userManager");
        assertNotNull(umgr);
    }
}