/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.dp.reservationsystem.ejb;

import cz.muni.fi.dp.reservationsystem.dao.*;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 * Interface for working with DB and reservations
 * @author Andrej
 */
@Local
public interface ReservationDAOLocal {
    /**
     * Get reservation by id
     *
     * @param id reservation id
     * @return reservation with given id or null
     */
    public Reservation getReservationById(long id);
    /**
     * Get reservations by user id
     *
     * @param user 
     * @return list of user's reservations or null
     */
    public List<Reservation> getReservationsByUser(User user);
    /**
     * Get reservations by computer id
     *
     * @param computer 
     * @return list of computer's reservations or null
     */
    public List<Reservation> getReservationsByComputer(long computer);
    /**
     * Get all reservations
     *
     * @return list of all reservations or null
     */
    public List<Reservation> getAllReservations();
    /**
     * Add new reservation
     *
     * @param user user
     * @param computer computer
     * @param since since date
     * @param until until date
     */
    public void addReservation(User user, Computer computer, Date since, Date until);
    /**
     * Check if the date interval is free
     *
     * @param comp_id computer id
     * @param since since date
     * @param until until date
     * @return true if the interval is free, false otherwise
     */
    public List<Reservation> checkInterval(long comp_id, Date since, Date until);
    /**
     * Deletes reservation
     * @param r reservation
     */
    public void deleteReservation(Reservation r);
    /**
     * Gets current reservations by user
     * @param user user
     * @return list of user's reservations
     */
    public List<Reservation> getCurrentReservationsByUser(User user);
    /**
     * Updates reservation
     * @param current reservation
     * @return true if no problem else otherwise
     */
    public boolean update(Reservation current);
    /**
     * Gets current reservations
     * @return list of current reservations
     */
    public List getCurrentReservations();
    /**
     * Gets current reservations by computer
     * @param comp_id computer id
     * @return return list of computer's reservations
     */
    public List getCurrentReservationsByComputer(long comp_id);
    /**
     * Deletes all reservations by user
     * @param user user
     */
    public void deleteAllReservationsByUser(User user);
    /**
     * Deletes all reservations by computer
     * @param computer computer
     */
    public void deleteAllReservationsByComputer(Computer computer);
    /**
     * Gets last months reservations by computer
     * @param computerId computer id
     * @return list of last months reservations by computer
     */
    public List<Reservation> getLastMonthReservationsByComputer(long computerId);
    /**
     * Gets last months reservations
     * @return list of last months reservations
     */
    public List<Reservation> getLastMonthReservations();
    /**
     * Gets reservation by token
     * @param token token
     * @return reservation
     */
    public Reservation getReservationByToken(String token);
}
