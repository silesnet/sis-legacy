package cz.silesnet.dao;

import cz.silesnet.model.User;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.testng.annotations.Test;

import java.util.Collection;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

public abstract class UserDAOTest extends DaoTestSupport<UserDAO> {

    @Test
    public void testGetUserByLoginName() {
        // have new user
        User u = new User();
        u.setLoginName("user1");
        u.setPassword("pass1");
        u.setName("UserName");

        // save user
        log.debug("Saving user: " + u);

        dao.saveUser(u);

        // get user by login name
        log.debug("Getting previously saved user by login name");

        User u2 = dao.getUserByLoginName(u.getLoginName());

        assertThat(u2, is(not(nullValue())));
        assertThat(u.getId(), is(u2.getId()));

        User u3 = null;

        try {
            u3 = dao.getUserByLoginName("fido");
            assertThat("this user does not exist.", true, is(false));
        } catch (ObjectRetrievalFailureException e) {
            log.debug("caught expected exception: " + e);
        }

        assertThat(u3, is(nullValue()));

        // remove saved user
        log.debug("Removing user: " + u.getId());

        dao.removeUser(Long.valueOf(u.getId()));
    }
    
    @Test
    public void testSaveRemoveUser() {
        // have new user
        User u = new User();
        u.setLoginName("user1");
        u.setPassword("pass1");
        u.setName("UserName");

        // save user
        log.debug("saving user: " + u);

        dao.saveUser(u);

        // remove saved user
        log.debug("removing user: " + u.getId());

        dao.removeUser(Long.valueOf(u.getId()));
    }

    @Test
    public void testUserDetails() {

        User user = new User();
        user.setLoginName("fido");
        user.setPassword("dido");
        user.setRoles("    ROLE_USER  , ROLE_STORE  ");
        user.setName("Fido Dido");

        log.debug("Saving user: " + user);
        dao.saveUser(user);

        user = null;

        user = dao.getUserByLoginName("fido");
        log.debug("Retrieved user: " + user);
        log.debug("Granted authorities: ");

        Collection<GrantedAuthority> authorities = user.getAuthorities();

        for (GrantedAuthority authority : authorities)
            log.debug("Authority: " + authority);

        // once more should be without initialization
        log.debug("Once more without initialization....");
        log.debug("Granted authorities: ");
        authorities = user.getAuthorities();

        for (GrantedAuthority authority : authorities)
            log.debug("Authority: " + authority);

        // test DaoAuthentication
        UserDetails ud = dao.loadUserByUsername("fido");
        log.debug("Retrieved user by auhenticationDao..." + ud);

        dao.removeUser(user.getId());
    }

    @Test
    public void testGetUserByPassword() {
        // have new user
        User u = new User();
        u.setLoginName("user1");
        u.setPassword("pass1");
        u.setName("UserName");

        // save user
        log.debug("Saving user: " + u);

        dao.saveUser(u);

        // get user by login name
        log.debug("Getting previously saved user by password name");

        User u2 = dao.getUserByKey(u.getPassword());

        assertThat(u2, is(not(nullValue())));
        assertThat(u.getId(), is(u2.getId()));

        User u3 = null;

        try {
            u3 = dao.getUserByKey("fido");
            assertThat("this user does not exist.", true, is(false));
        } catch (ObjectRetrievalFailureException e) {
            log.debug("caught expected exception: " + e);
        }

        assertThat(u3, is(nullValue()));

        // remove saved user
        log.debug("Removing user: " + u.getId());

        dao.removeUser(Long.valueOf(u.getId()));
    }


}