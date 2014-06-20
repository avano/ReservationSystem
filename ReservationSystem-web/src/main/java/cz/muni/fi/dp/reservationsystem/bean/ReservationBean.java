/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.dp.reservationsystem.bean;

import cz.muni.fi.dp.reservationsystem.dao.Computer;
import cz.muni.fi.dp.reservationsystem.ejb.ComputerDAOLocal;
import cz.muni.fi.dp.reservationsystem.dao.Reservation;
import cz.muni.fi.dp.reservationsystem.ejb.ReservationDAOLocal;
import cz.muni.fi.dp.reservationsystem.dao.User;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.servlet.http.HttpServletRequest;

/**
 * Bean that implements all methods with reservations
 *
 * @author Andrej
 */
public class ReservationBean implements Serializable {

    private static final Logger log = Logger.getLogger("ReservationBean");
    @EJB
    private ReservationDAOLocal rdao;
    @EJB
    private ComputerDAOLocal cdao;
    private DataModel reservationsDM;
    private Reservation current = null;
    private long user_id;
    private long comp_id;
    private Date since;
    private Date until;
    private boolean newResAdded = false;
    private boolean badReservation = false;
    private String selected = "";
    private String selectedRes = "";
    private List<String> notReserved = new ArrayList();
    private String sinceDate = "";
    private String untilDate = "";
    private String editSinceDate = "";
    private String editUntilDate = "";
    private String editComputer = "";
    private String error = "";

    /**
     *
     * @return
     */
    public String getError() {
        return error;
    }

    /**
     *
     * @param error
     */
    public void setError(String error) {
        this.error = error;
    }

    /**
     *
     * @return
     */
    public String getEditComputer() {
        return editComputer;
    }

    /**
     *
     * @param editComputer
     */
    public void setEditComputer(String editComputer) {
        this.editComputer = editComputer;
    }

    /**
     *
     * @return
     */
    public String getEditSinceDate() {
        return editSinceDate;
    }

    /**
     *
     * @param editSinceDate
     */
    public void setEditSinceDate(String editSinceDate) {
        this.editSinceDate = editSinceDate;
    }

    /**
     *
     * @return
     */
    public String getEditUntilDate() {
        return editUntilDate;
    }

    /**
     *
     * @param editUntilDate
     */
    public void setEditUntilDate(String editUntilDate) {
        this.editUntilDate = editUntilDate;
    }

    /**
     *
     * @return
     */
    public String getSinceDate() {
        return sinceDate;
    }

    /**
     *
     * @param sinceDate
     */
    public void setSinceDate(String sinceDate) {
        this.sinceDate = sinceDate;
    }

    /**
     *
     * @return
     */
    public String getUntilDate() {
        return untilDate;
    }

    /**
     *
     * @param untilDate
     */
    public void setUntilDate(String untilDate) {
        this.untilDate = untilDate;
    }

    /**
     *
     * @return
     */
    public Reservation getCurrent() {
        return current;
    }

    /**
     *
     * @param current
     */
    public void setCurrent(Reservation current) {
        this.current = current;
    }

    /**
     *
     * @return
     */
    public String getSelectedRes() {
        return selectedRes;
    }

    /**
     *
     * @param selectedRes
     */
    public void setSelectedRes(String selectedRes) {
        this.selectedRes = selectedRes;
    }

    /**
     *
     * @return
     */
    public String getSelected() {
        return selected;
    }

    /**
     *
     * @param selected
     */
    public void setSelected(String selected) {
        this.selected = selected;
    }

    /**
     *
     * @return
     */
    public boolean isBadReservation() {
        return badReservation;
    }

    /**
     *
     * @param badReservation
     */
    public void setBadReservation(boolean badReservation) {
        this.badReservation = badReservation;
    }

    /**
     *
     * @return
     */
    public long getUser_id() {
        return user_id;
    }

    /**
     *
     * @param user_id
     */
    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    /**
     *
     * @return
     */
    public long getComp_id() {
        return comp_id;
    }

    /**
     *
     * @param comp_id
     */
    public void setComp_id(long comp_id) {
        this.comp_id = comp_id;
    }

    /**
     *
     * @return
     */
    public Date getSince() {
        return since;
    }

    /**
     *
     * @param since
     */
    public void setSince(Date since) {
        this.since = since;
    }

    /**
     *
     * @return
     */
    public Date getUntil() {
        return until;
    }

    /**
     *
     * @param until
     */
    public void setUntil(Date until) {
        this.until = until;
    }

    /**
     *
     * @return
     */
    public boolean isNewResAdded() {
        return newResAdded;
    }

    /**
     *
     * @param newResAdded
     */
    public void setNewResAdded(boolean newResAdded) {
        this.newResAdded = newResAdded;
    }

    /**
     * Creates a new instance of ReservationBean
     */
    public ReservationBean() {
    }

    /**
     * Add new reservation if possible
     */
    public void addNewReservation() {
        if (!selectedRes.equals("")) {
            notReserved.clear();
            newResAdded = true;
            badReservation = false;
            error = "";
            ResourceBundle msg = ResourceBundle.getBundle("messages", new Locale(LanguageBean.getLocaleFromCookie()));
            String[] reservations = selectedRes.split(",");
            for (int i = 0; i < reservations.length; i++) {
                String[] currentRes = reservations[i].split(";");
                int id = Integer.parseInt(currentRes[0]);
                Date sinceDate = new Date(Long.parseLong(currentRes[1]));
                Date untilDate = new Date(Long.parseLong(currentRes[2]));
                Computer c = cdao.getComputerById(id);
                if (rdao.checkInterval(id, sinceDate, untilDate).isEmpty()) {
                    HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
                    User user = (User) req.getSession().getAttribute("user");

                    rdao.addReservation(user, c, sinceDate, untilDate);
                    log.log(Level.INFO, "New reservation: " + user.getEmail() + ", " + c.getIp() + ", " + getDateFormat(sinceDate) + ", " + getDateFormat(untilDate));
                } else {
                    newResAdded = false;
                    notReserved.add(reservations[i]);
                    error += c.getName() + ": " + getDateFormat(sinceDate) + " - " + getDateFormat(untilDate) + "<br/>";
                }
            }
            if (!newResAdded) {
                badReservation = true;
                //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(msg.getString("somereserved")));
            }

            this.selected = "";
            this.selectedRes = "";
        }
    }

    /**
     * Returns a new ListDataModel with all reservations by computer_id
     *
     * @param comp_id computer id
     * @param method 
     * @return new ListDataModel with all reservations by computer_id
     */
    public DataModel getReservationsByComp(long comp_id, String method) {
        if ("all".equals(method)) {
            reservationsDM = new ListDataModel(rdao.getReservationsByComputer(comp_id));
        } else {
            reservationsDM = new ListDataModel(rdao.getCurrentReservationsByComputer(comp_id));
        }
        return reservationsDM;
    }

    /**
     * Checks if computer has reservations
     * @param comp_id computer id
     * @param method method - whether just current or all reservations
     * @return true if comp has reservations else otherwise
     */
    public boolean compHasReservations(long comp_id, String method) {
        if ("all".equals(method)) {
            return rdao.getReservationsByComputer(comp_id).size() > 0;
        } else {
            return rdao.getCurrentReservationsByComputer(comp_id).size() > 0;
        }
    }

    /**
     * Checks if it should render buttons in table
     * @param user user
     * @param r reservation
     * @return true if it should render (it is not past reservation) else otherwise
     */
    public boolean renderEditDeleteButtons(User user, Reservation r) {
        if (r.getUntil().getTime() < new Date().getTime()) {
            return false;
        }
        if (user.isAdmin()) {
            return true;
        }
        if (r.getUser().equals(user)) {
            return true;
        }
        return false;
    }

    /**
     * Checks if user has reservations
     * @param user user
     * @param method method (all/current)
     * @return true if user has reservations false otherwise
     */
    public boolean userHasReservations(User user, String method) {
        if ("all".equals(method)) {
            return rdao.getReservationsByUser(user).size() > 0;
        } else {
            return rdao.getCurrentReservationsByUser(user).size() > 0;
        }
    }

    /**
     * Gets all user reservations into table model
     * @param user user
     * @param method method (all/current)
     * @return datamodel with user reservations
     */
    public DataModel getReservationsByUser(User user, String method) {
        if ("all".equals(method)) {
            reservationsDM = new ListDataModel(rdao.getReservationsByUser(user));
        } else {
            reservationsDM = new ListDataModel(rdao.getCurrentReservationsByUser(user));
        }
        return reservationsDM;
    }

    /**
     *
     * @return
     */
    public DataModel getCurrentReservations() {
        return reservationsDM;
    }

    /**
     * Deletes current reservation
     * @return empty string
     */
    public String deleteReservation() {
        editSinceDate = "";
        editUntilDate = "";
        rdao.deleteReservation(current);
        return "";
    }

    /**
     * Ends current reservation
     * @return empty string
     */
    public String cancelReservation() {
        editSinceDate = "";
        editUntilDate = "";
        if (current.getSince().after(new Date())) {
            rdao.deleteReservation(current);
            return "";
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 0);

        current.setUntil(new Date(cal.getTimeInMillis()));
        rdao.update(current);
        return "";
    }

    /**
     * Gets current reservation
     */
    public void getCurrentReservation() {
        current = (Reservation) getCurrentReservations().getRowData();
        editSinceDate = getDateFormat(current.getSince());
        editUntilDate = getDateFormat(current.getUntil());
        editComputer = current.getComputer().getId() + "";
    }

    /**
     * Updates current reservation
     * @return null
     */
    public String update() {
        ResourceBundle msg = ResourceBundle.getBundle("messages", new Locale(LanguageBean.getLocaleFromCookie()));

        Locale l = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, l);
        Date newSinceDate = null, newUntilDate = null;
        try {
            newSinceDate = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH).parse(editSinceDate);
            newUntilDate = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH).parse(editUntilDate);
        } catch (ParseException ex) {
            Logger.getLogger(ReservationBean.class.getName()).log(Level.SEVERE, null, ex);
            editSinceDate = "";
            editUntilDate = "";
        }
        if (newSinceDate.after(newUntilDate)) {
            editSinceDate = "";
            editUntilDate = "";
            FacesContext.getCurrentInstance().addMessage("frm:test", new FacesMessage(FacesMessage.SEVERITY_ERROR, msg.getString("badDates"), msg.getString("badDates")));
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date today = new Date(cal.getTimeInMillis());
        if (newSinceDate.before(today)) {
            editSinceDate = "";
            editUntilDate = "";
            FacesContext.getCurrentInstance().addMessage("frm:test", new FacesMessage(FacesMessage.SEVERITY_ERROR, msg.getString("pastDate"), msg.getString("pastDate")));
            return null;
        }
        Computer c = cdao.getComputerById(Integer.parseInt(editComputer));
        List<Reservation> resInInterval = rdao.checkInterval(c.getId(), newSinceDate, newUntilDate);
        if (!resInInterval.isEmpty()) {
            if (resInInterval.size() == 1 && resInInterval.get(0).getId() == current.getId()) {
            } else {
                editSinceDate = "";
                editUntilDate = "";
                FacesContext.getCurrentInstance().addMessage("frm:test", new FacesMessage(FacesMessage.SEVERITY_ERROR, msg.getString("reserved"), msg.getString("reserved")));
                return null;
            }
        }
        current.setComputer(c);
        current.setSince(newSinceDate);
        current.setUntil(newUntilDate);
        editSinceDate = "";
        editUntilDate = "";
        if (!rdao.update(current)) {
            FacesContext.getCurrentInstance().addMessage("frm:test", new FacesMessage(FacesMessage.SEVERITY_ERROR, msg.getString("reserved"), msg.getString("reserved")));
            return null;
        } else {
            return "";
        }
    }

    /**
     * Erases localised strings for dates
     */
    public void eraseDates() {
        editSinceDate = "";
        editUntilDate = "";
    }

    /**
     * Gets localised date string
     * @param d date
     * @return localised date string
     */
    public String getDateFormat(Date d) {
        Locale l = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        //DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, l);
        SimpleDateFormat format = null;
        if (l == null || l.equals(Locale.ENGLISH)) {
            format = new SimpleDateFormat("MMM d, yyyy h:mm:ss a");
        } else {
            format = new SimpleDateFormat("d.M.yyyy H:mm:ss");
        }
        String ret = "";
        try {
            ret = format.format(d);
        } catch (Exception ex) {
        }
        return ret;
    }
}
