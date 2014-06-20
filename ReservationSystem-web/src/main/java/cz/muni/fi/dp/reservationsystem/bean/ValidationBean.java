/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.dp.reservationsystem.bean;

import cz.muni.fi.dp.reservationsystem.ejb.ComputerDAOLocal;
import cz.muni.fi.dp.reservationsystem.ejb.ComputerDAO;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

/**
 * Bean for forms validation
 *
 * @author Andrej
 */
@ManagedBean
@RequestScoped
public class ValidationBean implements Serializable {

    /**
     * Creates a new instance of ValidationBean
     */
    public ValidationBean() {
    }

    /**
     * Checks if the input value has desired format
     *
     * @param context faces context
     * @param component component which needs validation
     * @param value input value
     *
     * @throws ValidatorException if the email doesn't have desired format
     */
    public void checkEmail(FacesContext context, UIComponent component, Object value) {
        Pattern pattern = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        if (!pattern.matcher(value.toString()).matches()) {
            if (!value.toString().equals("admin")) {
                if (!value.toString().equals("user")) {
                    throw new ValidatorException(new FacesMessage());
                }
            }
        }
    }
        /**
         * Checks if the password is empty
         *
         * @param context faces context
         * @param component component which needs validation
         * @param value input value
         *
         * @throws ValidatorException if the password is empty
         */
    

    public void checkPassword(FacesContext context, UIComponent component, Object value) {
        if (value.toString().isEmpty()) {
            throw new ValidatorException(new FacesMessage());
        }
    }

    /**
     *
     * @param context faces context
     * @param component component which needs validation
     * @param value input value
     */
    public void checkNumber(FacesContext context, UIComponent component, Object value) {
        int number = -1;
        try {
            number = Integer.parseInt(value.toString());
            ComputerDAOLocal cdao = new ComputerDAO();
            if (number > cdao.getComputersCount()) {
                throw new ValidatorException(new FacesMessage());
            }
        } catch (NumberFormatException ex) {
            throw new ValidatorException(new FacesMessage());
        }
    }

    /**
     *
     * @param context faces context
     * @param component component which needs validation
     * @param value input value
     */
    public void checkDates(FacesContext context, UIComponent component, Object value) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date currentDate = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        Date date = null;
        try {
            date = sdf.parse(value.toString());
        } catch (ParseException ex) {
            Logger.getLogger(ValidationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (date.before(currentDate)) {
            throw new ValidatorException(new FacesMessage());
        }
    }
}
