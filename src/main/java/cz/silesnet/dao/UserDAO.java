package cz.silesnet.dao;

import cz.silesnet.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

/**
 * User DAO class.
 *
 * @author Richard Sikora
 */
public interface UserDAO extends DAO, UserDetailsService {

    // ~ Methods
    // ----------------------------------------------------------------

    public User getUserById(Long userId);

    public User getUserByLoginName(String loginName);

    public User getUserByKey(String key);
    
    public List getUsers();

    public void removeUser(Long userId);

    public void saveUser(User user);
}