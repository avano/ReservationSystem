package cz.muni.fi.dp.reservationsystem.rest;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import org.jboss.resteasy.specimpl.ResponseBuilderImpl;
import org.jboss.resteasy.util.Base64;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.muni.fi.dp.reservationsystem.dao.Computer;
import cz.muni.fi.dp.reservationsystem.dao.Reservation;
import cz.muni.fi.dp.reservationsystem.dao.User;
import cz.muni.fi.dp.reservationsystem.ejb.ComputerDAOLocal;
import cz.muni.fi.dp.reservationsystem.ejb.ReservationDAOLocal;
import cz.muni.fi.dp.reservationsystem.ejb.UserDAOLocal;

/**
 *
 * @author Andrej
 */
@Path("/reservation")
public class ReservationRestService {

	private static final Logger log = Logger.getLogger("ReservationRestService");

	/**
	 * REST add reservation
	 *
	 * @param auth auth header
	 * @param request post request
	 * @return response
	 */
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/")
	public Response addReservation(@HeaderParam("Authorization") String auth, String request) {
		String result;
		final ResponseBuilder rb = new ResponseBuilderImpl();

		rb.type(MediaType.TEXT_PLAIN);
		if (auth == null) {
			result = "No authentication header found";
			rb.status(Response.Status.UNAUTHORIZED);
			rb.entity(result);
			return rb.build();
		}
		auth = auth.substring(6);
		String userNamePassword = null;
		try {
			userNamePassword = new String(Base64.decode(auth));
		} catch (final Exception ex) {
			log.log(Level.SEVERE, "decoding error" + ex.toString());
		}
		final String[] credentials = userNamePassword.split(":");
		if (credentials.length != 2) {
			result = "Bad authentication header format";
			rb.status(Response.Status.UNAUTHORIZED);
			rb.entity(result);
			return rb.build();
		}
		UserDAOLocal udao = null;
		try {
			udao = (UserDAOLocal) new InitialContext()
					.lookup("java:global/ROOT/ReservationSystem-ejb-1.0-SNAPSHOT/UserDAO!cz.muni.fi.dp.reservationsystem.ejb.UserDAO");
		} catch (final NamingException ex) {
		}
		final User u = udao.checkUser(credentials[0], credentials[1]);
		if (u == null) {
			result = "User not found!";
			rb.status(Response.Status.UNAUTHORIZED);
			rb.entity(result);
			return rb.build();
		}
		//since=12.1.2013 8:00&until=21.1.2013 12:00
		final String[] req = request.split("&");
		if (req.length < 2 || req.length > 3) {
			result = "Bad request format, correct format: \"since=12.1.2013 8:00&until=21.1.2013 12:00[&computer=java01]\"";
			//return Response.status(304).entity(result).build();
			rb.status(Response.Status.BAD_REQUEST);
			rb.entity(result);
			return rb.build();
		}
		final String since = req[0].substring(6);
		final String until = req[1].substring(6);
		String computer = null;
		if (req.length == 3) {
			computer = req[2].substring(9);
		}
		Date sinceDate = null;
		Date untilDate = null;
		Date untilTemp = null;
		if (since.equals("now")) {
			final Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			sinceDate = new Date(cal.getTimeInMillis());
		} else {
			try {
				sinceDate = new SimpleDateFormat("d.M.yyyy H:mm").parse(since);
			} catch (final ParseException ex) {
				result = "Bad since date format";
				rb.status(Response.Status.BAD_REQUEST);
				rb.entity(result);
				return rb.build();
			}
		}
		if (sinceDate.getTime() < new Date().getTime()) {
			result = "No past reservations allowed (since)";
			rb.status(Response.Status.BAD_REQUEST);
			rb.entity(result);
			return rb.build();
		}
		try {
			untilTemp = new SimpleDateFormat("d.M.yyyy H:mm").parse(until);
			untilDate = new Date(untilTemp.getTime() + 59999);
		} catch (final ParseException ex) {
			result = "Bad until date format";
			rb.status(Response.Status.BAD_REQUEST);
			rb.entity(result);
			return rb.build();
		}
		if (untilDate.getTime() < new Date().getTime()) {
			result = "No past reservations allowed (until)";
			rb.status(Response.Status.BAD_REQUEST);
			rb.entity(result);
			return rb.build();
		}
		if (sinceDate.getTime() > untilDate.getTime()) {
			result = "Until date is earlier than since date";
			rb.status(Response.Status.BAD_REQUEST);
			rb.entity(result);
			return rb.build();
		}
		ComputerDAOLocal cdao = null;
		try {
			cdao = (ComputerDAOLocal) new InitialContext()
					.lookup("java:global/ROOT/ReservationSystem-ejb-1.0-SNAPSHOT/ComputerDAO!cz.muni.fi.dp.reservationsystem.ejb.ComputerDAO");
		} catch (final NamingException ex) {
		}
		ReservationDAOLocal rdao = null;
		try {
			rdao = (ReservationDAOLocal) new InitialContext()
					.lookup("java:global/ROOT/ReservationSystem-ejb-1.0-SNAPSHOT/ReservationDAO!cz.muni.fi.dp.reservationsystem.ejb.ReservationDAO");
		} catch (final NamingException ex) {
		}
		if (computer != null) {
			final Computer c = cdao.getComputerByName(computer);
			if (c == null) {
				result = "Computer with this name was not found";
				rb.status(Response.Status.BAD_REQUEST);
				rb.entity(result);
				return rb.build();
			}
			if (!rdao.checkInterval(c.getId(), sinceDate, untilDate).isEmpty()) {
				result = "This computer is reserved in given interval";
				rb.status(Response.Status.CONFLICT);
				rb.entity(result);
				return rb.build();
			} else {
				rdao.addReservation(u, c, sinceDate, untilDate, "");
				result = "Reservation successful!";
				rb.status(Response.Status.CREATED);
				rb.entity(result);
				return rb.build();
			}
		} else {
			final List<Computer> computers = cdao.getAllComputers();
			boolean ok = false;
			String computerName = null;
			for (final Computer c : computers) {
				if (rdao.checkInterval(c.getId(), sinceDate, untilDate).isEmpty()) {
					rdao.addReservation(u, c, sinceDate, untilDate, "");
					computerName = c.getName();
					ok = true;
					break;
				}
			}
			if (!ok) {
				result = "No suitable computer found";
				rb.status(Response.Status.CONFLICT);
				rb.entity(result);
				return rb.build();
			}
			result = "Reservation successful! Computer name: " + computerName;
			rb.status(Response.Status.CREATED);
			rb.entity(result);
			return rb.build();
		}
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/getAllReservations")
	public Response getAllReservations() {
		String result = "";
		final ResponseBuilder rb = new ResponseBuilderImpl();
		ReservationDAOLocal rdao = null;
		try {
			rdao = (ReservationDAOLocal) new InitialContext()
					.lookup("java:global/ROOT/ReservationSystem-ejb-1.0-SNAPSHOT/ReservationDAO!cz.muni.fi.dp.reservationsystem.ejb.ReservationDAO");
		} catch (final NamingException ex) {
		}
		final List<Reservation> reservations = rdao.getAllReservations();
		for (final Reservation r : reservations) {
			result += r.getComputer().getName() + "," + r.getUser().getUserName() + "," + r.getSince() + ","
					+ r.getUntil() + "\n";
		}
		rb.status(Response.Status.OK);
		rb.entity(result);
		return rb.build();
	}
}
