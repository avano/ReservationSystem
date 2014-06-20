/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.dp.reservationsystem.bean;

import cz.muni.fi.dp.reservationsystem.dao.User;
import cz.muni.fi.dp.reservationsystem.ejb.UserDAOLocal;
import cz.muni.fi.dp.reservationsystem.ejb.UserDAO;
import cz.muni.fi.dp.reservationsystem.other.CookieManager;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Bean that implements all methods with users
 *
 * @author Andrej
 */
public class UserBean implements Serializable {

    @EJB
    private UserDAOLocal udao;
    private static final Logger log = Logger.getLogger("UserBean");
    private boolean badLogin = false;
    private DataModel usersDM;
    private boolean remember;
    //private UserDAOLocal udao;
    private User current;
    private int selectedItemIndex;
    private String email;
    private String password;
    private String name;
    private String admin;
    private String daysToDisplay;
    private String importString;
    private String filterQuery;
    private String confirmPassword;
    private String newPassword;
    private String calendarType = "null";
    private String method = "current";

    /**
     *
     * @return
     */
    public String getMethod() {
        return method;
    }

    /**
     *
     * @return
     */
    public User getCurrent() {
        return current;
    }

    /**
     *
     * @param current
     */
    public void setCurrent(User current) {
        this.current = current;
    }

    /**
     *
     * @param method
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     *
     * @return
     */
    public boolean isAll() {
        return "all".equals(method);
    }

    /**
     *
     */
    public void changeMethod() {
        if ("current".equals(method)) {
            method = "all";
        } else {
            method = "current";
        }
    }

    /**
     *
     * @return
     */
    public String getCalendarType() {
        return calendarType;
    }

    /**
     *
     * @param calendarType
     */
    public void setCalendarType(String calendarType) {
        this.calendarType = calendarType;
    }

    /**
     *
     * @return
     */
    public boolean isDays() {
        return calendarType.equals("null");
    }

    /**
     *
     * @return
     */
    public String getConfirmPassword() {
        return confirmPassword;
    }

    /**
     *
     * @param confirmPassword
     */
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    /**
     *
     * @return
     */
    public String getNewPassword() {
        return newPassword;
    }

    /**
     *
     * @param newPassword
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    /**
     *
     * @return
     */
    public String getAdmin() {
        return admin;
    }

    /**
     *
     * @param admin
     */
    public void setAdmin(String admin) {
        this.admin = admin;
    }

    /**
     *
     * @return
     */
    public String getFilterQuery() {
        return filterQuery;
    }

    /**
     *
     * @param filterQuery
     */
    public void setFilterQuery(String filterQuery) {
        this.filterQuery = filterQuery;
    }

    /**
     *
     * @return
     */
    public String getImportString() {
        return importString;
    }

    /**
     *
     * @param importString
     */
    public void setImportString(String importString) {
        this.importString = importString;
    }

    /**
     *
     * @return
     */
    public String getDaysToDisplay() {
        return daysToDisplay;
    }

    /**
     *
     * @param daysToDisplay
     */
    public void setDaysToDisplay(String daysToDisplay) {
        String oldValue = this.daysToDisplay;
        ResourceBundle msg = ResourceBundle.getBundle("messages", new Locale(LanguageBean.getLocaleFromCookie()));
        try {
            int i = Integer.parseInt(daysToDisplay);
            if (i <= 0) {
                FacesContext.getCurrentInstance().addMessage("dtdForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, msg.getString("enterNumber") + "!", msg.getString("enterNumber") + "!"));
                return;
            }

            CookieManager.addCookie((HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse(), "daysToDisplay", daysToDisplay, 2592000);
            this.daysToDisplay = daysToDisplay;
        } catch (NumberFormatException ex) {

            FacesContext.getCurrentInstance().addMessage("dtdForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, msg.getString("enterNumber") + "!", msg.getString("enterNumber") + "!"));
            this.daysToDisplay = oldValue;
        }

    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }
    private String redirectTo = "calendar.xhtml";
    private boolean newUser = false;

    /**
     *
     * @return
     */
    public boolean isNewUser() {
        return newUser;
    }

    /**
     *
     * @param newUser
     */
    public void setNewUser(boolean newUser) {
        this.newUser = newUser;
    }

    /**
     *
     * @return
     */
    public boolean isRemember() {
        return remember;
    }

    /**
     *
     * @return
     */
    public boolean isBadLogin() {
        return badLogin;
    }

    /**
     *
     * @param badLogin
     */
    public void setBadLogin(boolean badLogin) {
        this.badLogin = badLogin;
    }

    /**
     *
     * @param remember
     */
    public void setRemember(boolean remember) {
        this.remember = remember;
    }

    /**
     *
     * @return
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     *
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets currently logged in user's email
     *
     * @return
     */
    public String getEmailSes() {
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        User user = (User) req.getSession().getAttribute("user");
        return user.getEmail();
    }

    /**
     *
     * @return
     */
    public User getUserSes() {
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        User user = (User) req.getSession().getAttribute("user");
        return user;
    }

    /**
     * Gets currently logged in user's name
     *
     * @return
     */
    public String getUserSesName() {
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        User user = (User) req.getSession().getAttribute("user");
        return user.getUserName();
    }

    /**
     *
     * @return
     */
    public String getUserSesEmail() {
        return getEmailSes();
    }

    /**
     * Gets currently logged in user's id
     *
     * @return
     */
    public long getIdSes() {
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        User user = (User) req.getSession().getAttribute("user");
        return user.getId();
    }

    /**
     *
     * @return
     */
    public String getNameSes() {
        return getUserSesName();
    }

    /**
     *
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Creates a new instance of UserBean
     */
    public UserBean() {
    }

    @PostConstruct
    private void init() {
        String dtd = CookieManager.getCookieValue((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest(), "daysToDisplay");
        if (dtd == null) {
            this.daysToDisplay = "7";
        } else {
            this.daysToDisplay = dtd;
        }
    }

    /**
     * Logs in the user, stores user in session and sets cookie value if
     * necessary
     *
     * @return url of the next page
     */
    public String login() {
        User user = udao.checkUser(email.toLowerCase(), password);
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpServletResponse res = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();

        if (user != null) { //successful login
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("user", user); //insert user into session
            if (remember) { //if it should remember user, generate new random value and add this value into db and into cookie
                String uuid = UUID.randomUUID().toString();
                udao.setCookieToUser(user, uuid);
                CookieManager.addCookie(res, "remember_id", uuid, 2592000);
            } else {
                CookieManager.addCookie(res, "remember_id", null, 0);  //if it shouldn't remember user, remove the remember cookie
            }
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("user", user);
            badLogin = false;

            String redirectFromCookie = CookieManager.getCookieValue(req, "redirectTo"); //determines from cookie if the user was redirected from other page
            if (redirectFromCookie != null) { //if user was redirected, redirect him back after login
                redirectTo = redirectFromCookie;
                CookieManager.removeCookie(res, "redirectTo");
            }
            current = new User();
            current.setId(user.getId());
            current.setUserName(user.getUserName());
            current.setEmail(user.getEmail());
            current.setPassword(user.getPassword());
            current.setSalt(user.getSalt());
            current.setAdmin(user.isAdmin());
            current.setCookie(user.getCookie());
            log.log(Level.INFO, "User logged in: " + user.getEmail());
            return redirectTo + "?faces-redirect=true";
        } else {
            //badLogin = true;
            ResourceBundle msg = ResourceBundle.getBundle("messages", new Locale(LanguageBean.getLocaleFromCookie()));

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg.getString("badLogin"), msg.getString("badLogin")));

            return "";
        }
    }

    /**
     * Logs off the user, invalidates session and remove cookies
     *
     * @return location string
     */
    public String logout() {
        User user = (User) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("user");
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        CookieManager.removeCookie((HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse(), "remember_id");
        //CookieManager.addCookie((HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse(), "remember_id", null, 0);
        CookieManager.removeCookie((HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse(), "calDate");
        udao.setCookieToUser(user, null);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("user");
        return "index.xhtml?faces-redirect=true";
    }

    

    /**
     * Updates current user
     *
     * @return empty string
     */
    public String update() {
        ResourceBundle msg = ResourceBundle.getBundle("messages", new Locale(LanguageBean.getLocaleFromCookie()));
        String email = getEmailSes();
        User u = udao.getUserByEmail(current.getEmail());
        if (u != null) {
            if (getUserSes().getId() != u.getId()) {
                FacesContext.getCurrentInstance().addMessage("newPasswordForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, msg.getString("duplicateEmail") + ": " + u.getEmail(), msg.getString("duplicateEmail") + ": " + u.getEmail()));
                current.setEmail(email);
                return "";
            }
        }
        if (newPassword == null || confirmPassword == null) {
            FacesContext.getCurrentInstance().addMessage("newPasswordForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, msg.getString("passwordNull"), msg.getString("passwordNull")));
            return "";
        }
        if (!newPassword.equals(confirmPassword)) {
            FacesContext.getCurrentInstance().addMessage("newPasswordForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, msg.getString("passwordMismatch"), msg.getString("passwordMismatch")));
            return "";
        }
        boolean passwordChanged = false;
        if (!"".equals(newPassword) && !"".equals(confirmPassword)) {
            SecureRandom random = null;
            try {
                random = SecureRandom.getInstance("SHA1PRNG");
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
            byte[] bSalt = new byte[8];
            random.nextBytes(bSalt);

            byte[] bPassword = UserDAO.getHash(newPassword, bSalt);

            String passwordDB = UserDAO.byteToBase64(bPassword);
            String salt = UserDAO.byteToBase64(bSalt);

            current.setPassword(passwordDB);
            current.setSalt(salt);
            passwordChanged = true;
        }
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("user", current); //insert user into session

        udao.update(current);


        FacesContext.getCurrentInstance().addMessage("newPasswordForm", new FacesMessage(FacesMessage.SEVERITY_INFO, msg.getString("successfulUpdate"), msg.getString("successfulUpdate")));
        return "";

    }

    /**
     *
     * @return
     */
    public DataModel getUsers() {
        //if (usersDM == null) {
        return usersDM = new ListDataModel(udao.getAllUsers());
        //}
        //return usersDM; 
    }

    /**
     *
     * @return
     */
    public User getSelected() {
        if (current == null) {
            current = new User();
            selectedItemIndex = -1;
        }
        return current;
    }

    /**
     *
     * @return
     */
    public DataModel getCurrentUsers() {
        return usersDM;
    }

    /**
     * View details of user
     *
     * @param userSession
     * @return location string
     */
    public String viewDetails(String userSession) {
        if ("current".equals(userSession)) {
            current = new User();
            current.setId(getUserSes().getId());
            current.setUserName(getUserSes().getUserName());
            current.setEmail(getUserSes().getEmail());
            current.setPassword(getUserSes().getPassword());
            current.setSalt(getUserSes().getSalt());
            current.setAdmin(getUserSes().isAdmin());
            current.setCookie(getUserSes().getCookie());
        } else {
            current = (User) getCurrentUsers().getRowData();
        }
        //current = udao.getUserById(userId);
        return "userDetails.xhtml?faces-redirect=true";

    }

    /**
     *
     */
    public void getCurrentUser() {
        current = (User) getCurrentUsers().getRowData();
    }

    /**
     * Deletes current user
     *
     * @return empty string
     */
    public String deleteUser() {
        udao.deleteUser(current);
        return "";
    }

    public void grantRevoke() {
        if (current.isAdmin()) {
            current.setAdmin(false);
        } else {
            current.setAdmin(true);
        }
        System.out.println(getUserSes().getId());
        System.out.println(getUserSes().isAdmin());
        System.out.println(current.getId());
        System.out.println(current.isAdmin());
        if (getUserSes().getId() == current.getId()) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("user");
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("user", current);
        }
        System.out.println(getUserSes().getId());
        System.out.println(getUserSes().isAdmin());
        udao.update(current);


        if (getUserSes().getId() == current.getId()) {
        }
    }
}
