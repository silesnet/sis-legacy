package cz.silesnet.service;

//import org.acegisecurity.Authentication;
//import org.acegisecurity.AuthenticationManager;
import org.acegisecurity.providers.TestingAuthenticationToken;
import org.testng.annotations.Test;

public class AcegiAuthenticateTest extends BaseServiceTestCase {

    @Test
	public void testAcegiAuthenticate() {
		// AuthenticationManager am = (AuthenticationManager)
		// ctx.getBean("authenticationManager");
		// Authentication user =
		new TestingAuthenticationToken("leon", "rsi", null);

		// Authentication user2 = am.authenticate(user);
		// log.debug(user2);
		// assertNotNull(user2.getAuthorities());
	}
}