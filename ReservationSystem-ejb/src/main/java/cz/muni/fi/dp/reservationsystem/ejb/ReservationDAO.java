/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.dp.reservationsystem.ejb;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import cz.muni.fi.dp.reservationsystem.dao.Computer;
import cz.muni.fi.dp.reservationsystem.dao.Reservation;
import cz.muni.fi.dp.reservationsystem.dao.User;

/**
 *
 * @author Andrej
 */
@Stateless(name = "ReservationDAO", mappedName = "ReservationDAO")
@LocalBean
public class ReservationDAO implements ReservationDAOLocal {

	@PersistenceContext(unitName = "ReservationSystem-PU")
	private EntityManager em;
	private static final Logger log = Logger.getLogger("ReservationDAOImplLogger");
	@EJB
	private ComputerDAOLocal cdao;

	/**
	 * Get reservation by id
	 *
	 * @param id reservation id
	 * @return reservation with given id or null
	 */
	@Override
	public Reservation getReservationById(long id) {
		final Query q = em.createQuery("select reservation from Reservation reservation where id = ?1");
		q.setParameter(1, id);
		Reservation r = null;
		try {
			r = (Reservation) q.getSingleResult();
		} catch (final Exception ex) {

		}

		return r;
	}

	/**
	 * Get reservations by user id
	 *
	 * @param user 
	 * @return list of user's reservations or null
	 */
	@Override
	public List<Reservation> getReservationsByUser(User user) {
		final Query q = em.createQuery("select reservation from Reservation reservation where user_id = ?1");
		q.setParameter(1, user.getId());
		return q.getResultList();
	}

	/**
	 * Get reservations by computer id
	 *
	 * @param computer 
	 * @return list of computer's reservations or null
	 */
	@Override
	public List<Reservation> getReservationsByComputer(long computer) {
		final Query q = em.createQuery("select reservation from Reservation reservation where computer_id = ?1");
		q.setParameter(1, computer);
		return q.getResultList();
	}

	/**
	 * Get all reservations
	 *
	 * @return list of all reservations or null
	 */
	@Override
	public List<Reservation> getAllReservations() {
		final Query q = em.createQuery("select reservation from Reservation reservation");
		return q.getResultList();
	}

	/**
	 * Add new reservation
	 *
	 * @param user user
	 * @param computer computer
	 * @param since since date
	 * @param until until date
	 */
	@Override
	public void addReservation(User user, Computer computer, Date since, Date until, String comment) {
		final java.sql.Date server_since = new java.sql.Date(since.getTime()); // kvoli openshift db
		final java.sql.Date server_until = new java.sql.Date(until.getTime()); // kvoli openshift db
		final Reservation res = new Reservation();
		res.setUser(user);
		res.setComputer(computer);
		res.setSince(server_since);
		res.setUntil(server_until);
		res.setComment(comment);
		em.persist(res);
		em.flush();

	}

	/**
	 * Check if the date interval is free
	 *
	 * @param comp_id computer id
	 * @param since since date
	 * @param until until date
	 * @return true if the interval is free, false otherwise
	 */
	@Override
	public List<Reservation> checkInterval(long comp_id, Date since, Date until) {
		final java.sql.Date sql_since = new java.sql.Date(since.getTime());
		final java.sql.Date sql_until = new java.sql.Date(until.getTime());
		final Query q = em
				.createQuery("select reservation from Reservation reservation where computer_id = ?1 "
						+ "and ((until between ?2 and ?3) or (since between ?2 and ?3) or "
						+ "(since >= ?2 and until <= ?3) or (since <= ?2 and until >= ?3))");
		q.setParameter(1, comp_id);
		q.setParameter(2, sql_since);
		q.setParameter(3, sql_until);
		return q.getResultList();
	}

	/**
	 * Deletes reservation
	 * @param r reservation
	 */
	@Override
	public void deleteReservation(Reservation r) {
		final Reservation res = em.find(Reservation.class, r.getId());
		em.remove(res);
		em.flush();
	}

	/**
	 * Updates reservation
	 * @param current reservation
	 * @return true if no problem else otherwise
	 */
	@Override
	public boolean update(Reservation current) {
		final List<Reservation> resInInterval = checkInterval(current.getComputer().getId(), current.getSince(),
				current.getUntil());
		if (resInInterval.size() > 1) {
			return false;
		}
		if (resInInterval.size() == 1 && resInInterval.get(0).getId() != current.getId()) {
			return false;
		}
		em.merge(current);
		em.flush();
		return true;
	}

	/**
	 * Gets current reservations by user
	 * @param user user
	 * @return list of user's reservations
	 */
	@Override
	public List<Reservation> getCurrentReservationsByUser(User user) {
		final java.sql.Date d = new java.sql.Date(new Date().getTime());
		final Query q = em
				.createQuery("select reservation from Reservation reservation where user_id = ?1 and ((since > ?2) or (since <= ?2 and until >= ?2))");
		q.setParameter(1, user.getId());
		q.setParameter(2, d);
		return q.getResultList();
	}

	/**
	 * Gets current reservations by computer
	 * @param comp_id computer id
	 * @return return list of computer's reservations
	 */
	@Override
	public List getCurrentReservationsByComputer(long comp_id) {
		final java.sql.Date d = new java.sql.Date(new Date().getTime());
		final Query q = em
				.createQuery("select reservation from Reservation reservation where computer_id = ?1 and ((since > ?2) or (since <= ?2 and until >= ?2))");
		q.setParameter(1, comp_id);
		q.setParameter(2, d);
		return q.getResultList();
	}

	/**
	 * Deletes all reservations by user
	 * @param user user
	 */
	@Override
	public void deleteAllReservationsByUser(User user) {
		final Query q = em.createQuery("select reservation from Reservation reservation where user_id = ?1");
		q.setParameter(1, user.getId());
		final List<Reservation> ret = q.getResultList();
		for (final Reservation r : ret) {
			final Reservation res = em.find(Reservation.class, r.getId());
			em.remove(res);
		}
		em.flush();
	}

	/**
	 * Deletes all reservations by computer
	 * @param computer computer
	 */
	@Override
	public void deleteAllReservationsByComputer(Computer computer) {
		final Query q = em.createQuery("select reservation from Reservation reservation where computer_id = ?1");
		q.setParameter(1, computer.getId());
		final List<Reservation> ret = q.getResultList();
		for (final Reservation r : ret) {
			final Reservation res = em.find(Reservation.class, r.getId());
			em.remove(res);
		}
		em.flush();
	}

	/**
	 * Gets last months reservations by computer
	 * @param computerId computer id
	 * @return list of last months reservations by computer
	 */
	@Override
	public List<Reservation> getLastMonthReservationsByComputer(long computerId) {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.DAY_OF_MONTH, -30);
		final java.sql.Date d = new java.sql.Date(cal.getTimeInMillis());
		cal.setTime(new Date());
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		final java.sql.Date today = new java.sql.Date(cal.getTimeInMillis());
		final Query q = em
				.createQuery("select reservation from Reservation reservation where computer_id = ?1 and ((since >= ?2) or (until <= ?3))");
		q.setParameter(3, today);
		q.setParameter(2, d);
		q.setParameter(1, computerId);

		return q.getResultList();
	}

	/**
	 * Gets last months reservations
	 * @return list of last months reservations
	 */
	@Override
	public List<Reservation> getLastMonthReservations() {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.DAY_OF_MONTH, -30);
		final java.sql.Date d = new java.sql.Date(cal.getTimeInMillis());
		cal.setTime(new Date());
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		final java.sql.Date today = new java.sql.Date(cal.getTimeInMillis());
		final Query q = em
				.createQuery("select reservation from Reservation reservation where ((until > ?1 and until <= ?2) or (since <= ?1 and until >= ?2) or ((since between ?1 and ?2) and until >= ?2))");
		q.setParameter(1, d);
		q.setParameter(2, today);
		return q.getResultList();
	}

	/**
	 * Gets current reservations
	 * @return list of current reservations
	 */
	@Override
	public List getCurrentReservations() {
		final java.sql.Timestamp d = new java.sql.Timestamp(new Date().getTime());
		final Query q = em
				.createQuery("select reservation from Reservation reservation where ((since > ?1) or (since <= ?1 and until > ?1)) order by id asc");
		q.setParameter(1, d);
		return q.getResultList();
	}

	/**
	 * Gets reservation by token
	 * @param token token
	 * @return reservation
	 */
	@Override
	public Reservation getReservationByToken(String token) {
		final Query q = em.createQuery("select reservation from Reservation reservation where token = ?1");
		q.setParameter(1, token);
		Reservation r = null;
		try {
			r = (Reservation) q.getSingleResult();
		} catch (final Exception ex) {

		}

		return r;
	}

}