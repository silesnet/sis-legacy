package cz.silesnet.dao.hibernate;

import cz.silesnet.dao.UserDAO;
import cz.silesnet.model.User;
import cz.silesnet.util.DiffUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of UserDAO interface using Hibernate.
 *
 * @author Richard Sikora
 */
public class UserDAOHibernate extends HibernateDaoSupport implements UserDAO {

    // ~ Static fields/initializers
    // ---------------------------------------------

    protected static final Log log = LogFactory.getLog(DiffUtils.class);

    // ~ Methods
    // ----------------------------------------------------------------

    public User getUserById(Long userId) {
        User user = (User) getHibernateTemplate().get(User.class, userId);

        if (user == null)
            throw new ObjectRetrievalFailureException(User.class, userId);

        return user;
    }

    public User getUserByLoginName(String loginName) {
        ArrayList result = (ArrayList) getHibernateTemplate().find(
                "from cz.silesnet.model.User as user where user.loginName=?",
                loginName);

        // login name is unique man
        if ((result.size() == 0) || (result.size() > 1))
            throw new ObjectRetrievalFailureException(User.class, loginName);

        return (User) result.get(0);
    }

    public User getUserByKey(final String key) {
        final List result = getHibernateTemplate().find("from cz.silesnet.model.User as user where user.key=?", key);
        if ((result.size() == 0) || (result.size() > 1))
            throw new ObjectRetrievalFailureException(User.class, key);
        return (User) result.get(0);
    }

    public List getUsers() {
        return getHibernateTemplate().find("from cz.silesnet.model.User");
    }

    public UserDetails loadUserByUsername(String userName)
            throws UsernameNotFoundException, DataAccessException {
        log.debug("Loading user by userName (AuthenticationDao).");

        ArrayList<User> result = (ArrayList<User>) getHibernateTemplate().find(
                "from cz.silesnet.model.User as user where user.loginName=?",
                userName);

        // login name is unique man
        if ((result.size() == 0) || (result.size() > 1))
            throw new UsernameNotFoundException(userName);

        User user = result.get(0);

        if (user.getAuthorities().size() == 0)
            new UsernameNotFoundException(userName);

        return (UserDetails) user;
    }

    public void removeUser(Long userId) {
        getHibernateTemplate().delete(getUserById(userId));
    }

    public void saveUser(User user) {
        getHibernateTemplate().saveOrUpdate(user);
    }
}