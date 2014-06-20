/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.dp.reservationsystem.ejb;

import cz.muni.fi.dp.reservationsystem.dao.User;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Andrej
 */
@Local
public interface UserDAOLocal {

    /**
     * Checks if the user with password is in database
     *
     * @param email user's email
     * @param password user's password
     * @return user or null
     */
    public User checkUser(String userName, String password);
    /**
     * Sets cookie value to user
     *
     * @param user user
     * @param cookie cookie value
     */
    public void setCookieToUser(User user, String cookie);
    /**
     * Gets user's cookie value
     *
     * @param cookie cookie
     * @return user or null
     */
    public User getUserByCookie(String cookie);
    /**
     * Registers new user
     * @param email email
     * @param name name
     * @param admin admin rights
     */
    public void registerUser(String email, String name, boolean admin);
    /**
     * Gets user by id
     * @param user_id user id
     * @return user
     */
    public User getUserById(long user_id);
    /**
     * Gets user by email
     * @param email email
     * @return user
     */
    public User getUserByEmail(String email);
    /**
     * Deletes user
     * @param user user
     */
    public void deleteUser(User user); 
    /**
     * Updates user
     * @param user user
     */
    public void update(User user);
    /**
     * Checks if is email already registered
     * @param email email
     * @return true if its not, false otherwise
     */
    public boolean checkEmail(String email);
    /**
     * Gets all users
     * @return list with all users
     */
    public List<User> getAllUsers();
    /**
     * Adds a new password to user
     * @param u User
     * @param password new password
     */
    public void newPassword(User u, String password);
}
