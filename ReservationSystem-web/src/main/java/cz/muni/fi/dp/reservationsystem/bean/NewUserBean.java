/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.dp.reservationsystem.bean;

import cz.muni.fi.dp.reservationsystem.dao.User;
import cz.muni.fi.dp.reservationsystem.ejb.TimerSessionBean;
import cz.muni.fi.dp.reservationsystem.ejb.UserDAOLocal;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Andrej
 */
public class NewUserBean implements Serializable {

    private static final Logger log = Logger.getLogger("NewUserBean");
    @EJB
    private UserDAOLocal udao;
    private String email;
    private String password;
    private String name;
    private String admin;
    private String importString;
    private boolean importing = false;
    @Resource(lookup = "java:jboss/mail/Default")
    private Session mailSession;

    public String getImportString() {
        return importString;
    }

    public void setImportString(String importString) {
        this.importString = importString;
    }

    /**
     * Creates a new instance of NewUserBean
     */
    public NewUserBean() {
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
     *
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
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
     * Register a new user
     *
     * @return null
     */
    public String register() {
        ResourceBundle msg = ResourceBundle.getBundle("messages", new Locale(LanguageBean.getLocaleFromCookie()));
        if (!udao.checkEmail(email.toLowerCase())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg.getString("duplicateEmail") + ": " + email.toLowerCase(),msg.getString("duplicateEmail") + ": " + email.toLowerCase()));
            return null;
        }
        udao.registerUser(email.toLowerCase(), name, Boolean.parseBoolean(admin));
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        User userLogged = (User) req.getSession().getAttribute("user");

        log.log(Level.INFO, "New user: " + email.toLowerCase() + " (reg. by: " + userLogged.getEmail() + ")");
        if (sendNewPassword()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg.getString("successfulRegistration") + ": " + email.toLowerCase(), msg.getString("successfulRegistration") + ": " + email.toLowerCase()));
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg.getString("sentPassword") + "! E-mail: " + email, msg.getString("sentPassword") + "! E-mail: " + email));
        };
        

        name = "";
        email = "";
        return null;
    }

    public boolean sendNewPassword() {
        ResourceBundle msg = ResourceBundle.getBundle("messages", new Locale(LanguageBean.getLocaleFromCookie()));
        User u = udao.getUserByEmail(email);
        if (u == null) {
            FacesContext.getCurrentInstance().addMessage("dtdForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, msg.getString("emailNotFound") + "!", msg.getString("emailNotFound") + "!"));
            return false;
        }
        String newPass = UUID.randomUUID().toString();
        newPass = newPass.substring(0, 7);
        udao.newPassword(u, newPass);

        try {
            Date cancelDate = new Date();
            Message message = new MimeMessage(mailSession);
            MimeMultipart mpart = new MimeMultipart();
            MimeBodyPart bp = new MimeBodyPart();
            message.setHeader("Content-Type", "text/html");
            try {
                message.setFrom(new InternetAddress("noreply@reservationsystem", "Reservation System"));
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(TimerSessionBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(email));
            message.setSubject("New password");
            bp.setText("Dear " + u.getUserName() + ","
                    + "<br/> your new password is: " + newPass, "UTF-8", "html");
            mpart.addBodyPart(bp);
            message.setContent(mpart);
            Transport.send(message);
            log.log(Level.INFO, "Sent new password, user: " + email);
            if (!importing) {
                return true;
            }
            email = "";
            return false;
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Imports multiple values at once
     */
    public void importValues() {
        ResourceBundle msg = ResourceBundle.getBundle("messages", new Locale(LanguageBean.getLocaleFromCookie()));
        String[] lines = importString.split(System.getProperty("line.separator"));
        String infoString = "";
        boolean error = false;
        String errorString = "";
        importing = true;

        for (int i = 0; i < lines.length; i++) {
            String[] line = lines[i].split(",");
            //userName;
            //password;
            //email;
            //admin;
            if (line.length != 3) {
                error = true;
                errorString += (i + 1) + " (" + (msg.getString("missingColumn")) + "), ";
                continue;
            }
            if (line[0].equals("") || line[1].equals("") || line[2].equals("")) {
                error = true;
                errorString += (i + 1) + " (" + (msg.getString("emptyColumn")) + "), ";
                continue;
            }
            Pattern pattern = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
            if (!pattern.matcher(line[1].trim()).matches()) {
                errorString += (i + 1) + " (" + (msg.getString("badEmail")) + "), ";
                continue;
            }
            HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            User user = (User) req.getSession().getAttribute("user");
            if (udao.checkEmail(line[1].trim())) {
                udao.registerUser(line[1].trim(), line[0].trim(), Boolean.parseBoolean(line[2].trim()));
                email = line[1].trim();
                sendNewPassword();
                infoString += line[1].trim() + ", ";
                log.log(Level.INFO, "New user imported: " + line[1].trim() + " (reg. by: " + user.getEmail() + ")");
            } else {
                error = true;
                errorString += (i + 1) + " (" + (msg.getString("duplicateEmail")) + "), ";
            }

        }
        if (error) {
            errorString = errorString.substring(0, errorString.length() - 1);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg.getString("skipping") + ": " + errorString, null));
        }
        if (!infoString.equals("")) {
            infoString = infoString.substring(0, infoString.length() - 1);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg.getString("newUsers") + ": " + infoString, infoString));
        }
        importing = false;
    }
}
