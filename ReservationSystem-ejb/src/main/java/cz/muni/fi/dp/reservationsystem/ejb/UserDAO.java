/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.dp.reservationsystem.ejb;

import cz.muni.fi.dp.reservationsystem.dao.User;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 *
 * @author Andrej
 */
@Stateless(name="UserDAO", mappedName="UserDAO")
@LocalBean
public class UserDAO implements UserDAOLocal {

    @PersistenceContext(unitName = "ReservationSystem-PU")
    private EntityManager em;
    private static final Logger log = Logger.getLogger("UserDAO");
    @EJB
    private ReservationDAOLocal rdao;
    /**
     *
     */
    public UserDAO() {
    }

    /**
     * Checks if the user with password is in database
     *
     * @param email user's email
     * @param password user's password
     * @return user or null
     */
    @Override
    public User checkUser(String email, String password) {
        email = email.trim();
        Query q = em.createQuery("SELECT user FROM User user where email = ?1");
        q.setParameter(1, email);
        User user = null;
        try {
            user = (User)q.getSingleResult();
        }
        catch (Exception ex) {
            
        }
        
        if (user == null) {
            log.log(Level.WARNING, "Login failed: e-mail {0} not found", email);
            return null;
        }

        byte[] passwordDB = null;
        try {
            passwordDB = base64ToByte(user.getPassword());
        } catch (IOException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        byte[] salt = null;
        try {
            salt = base64ToByte(user.getSalt());
        } catch (IOException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        byte[] passwordHash = getHash(password, salt);

        if (Arrays.equals(passwordHash, passwordDB)) {
            return user;
        } else {
            log.log(Level.WARNING, "Login failed: e-mail {0} wrong password", email);
            return null;
        }
    }

    /**
     * Sets cookie value to user
     *
     * @param user user
     * @param cookie cookie value
     */
    @Override
    public void setCookieToUser(User user, String cookie) {
        user.setCookie(cookie);
        em.merge(user);    
        em.flush();
    }

    /**
     * Gets user's cookie value
     *
     * @param cookie cookie
     * @return user or null
     */
    @Override
    public User getUserByCookie(String cookie) {
        Query q = em.createQuery("select user from User user where cookie = ?1");
        q.setParameter(1, cookie);
        User user = null;
        try {
            user = (User)q.getSingleResult();
        }
        catch (Exception ex) {
            
        }
        return user;
    }

    /**
     * Registers new user
     * @param email email
     * @param name name
     * @param admin admin rights
     */
    @Override
    public void registerUser(String email, String name, boolean admin) {
        
        User user = new User();
        user.setEmail(email.trim());
        user.setUserName(name.trim());

        
        user.setPassword(null);
        user.setSalt(null);
        user.setAdmin(admin);

        em.persist(user);
        em.flush();
    }

    /**
     * Base64 to byte array
     * @param data data
     * @return byte array with data
     * @throws IOException ioexception
     */
    public static byte[] base64ToByte(String data) throws IOException {
        BASE64Decoder decoder = new BASE64Decoder();
        return decoder.decodeBuffer(data);
    }

    /**
     * From a byte[] returns a base 64 representation
     *
     * @param data byte[]
     * @return String
     */
    public static String byteToBase64(byte[] data) {
        BASE64Encoder endecoder = new BASE64Encoder();
        return endecoder.encode(data);
    }

    /**
     * Gets hash from password and salt
     * @param password password
     * @param salt salt
     * @return byte array with hash
     */
    public static byte[] getHash(String password, byte[] salt) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        digest.reset();
        digest.update(salt);
        byte[] input = null;
        try {
            input = digest.digest(password.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return input;
    }

    /**
     * Gets user by id
     * @param user_id user id
     * @return user
     */
    @Override
    public User getUserById(long user_id) {
        Query q = em.createQuery("select user from User as user where id = ?1");
        q.setParameter(1, user_id);
        User user = null;
        try {
            user = (User)q.getSingleResult();
        }
        catch (Exception ex) {
            
        }
        return user;
    }

    /**
     * Updates user
     * @param user user
     */
    @Override
    public void update(User user) {
        em.merge(user);
        em.flush();
    }

    /**
     * Gets all users
     * @return list with all users
     */
    @Override
    public List<User> getAllUsers() {
        Query q = em.createQuery("SELECT user from User user ORDER BY id ASC");
        return q.getResultList();
    }

    /**
     * Checks if is email already registered
     * @param email email
     * @return true if its not, false otherwise
     */
    @Override
    public boolean checkEmail(String email) {
        Query q = em.createQuery("select user from User user where email = ?1");
        q.setParameter(1, email.trim());
        return q.getResultList().isEmpty();
    }

    /**
     * Deletes user
     * @param user user
     */
    @Override
    public void deleteUser(User user) {
        rdao.deleteAllReservationsByUser(user);
        User u = em.find(User.class, user.getId());
        em.remove(u);
        em.flush();
    }

    /**
     * Gets user by email
     * @param email email
     * @return user
     */
    @Override
    public User getUserByEmail(String email) {
        Query q = em.createQuery("select user from User user where email = ?1");
        q.setParameter(1, email.trim());
        User user = null;
        try {
            user = (User)q.getSingleResult();
        }
        catch (Exception ex) {
            
        }
        return user;
    }

    @Override
    public void newPassword(User u, String password) {
        SecureRandom random = null;
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        byte[] bSalt = new byte[8];
        random.nextBytes(bSalt);

        byte[] bPassword = getHash(password, bSalt);

        String passwordDB = byteToBase64(bPassword);
        String salt = byteToBase64(bSalt);

        u.setPassword(passwordDB);
        u.setSalt(salt);

        em.merge(u);
        em.flush();
    }
    
}
