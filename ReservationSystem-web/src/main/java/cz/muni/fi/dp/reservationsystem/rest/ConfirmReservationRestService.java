package cz.muni.fi.dp.reservationsystem.rest;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import cz.muni.fi.dp.reservationsystem.dao.Reservation;
import cz.muni.fi.dp.reservationsystem.ejb.ReservationDAOLocal;
import cz.muni.fi.dp.reservationsystem.ejb.ReservationDAOLocal;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

/**
 *
 * @author Andrej
 */
@Path("/confirm")
public class ConfirmReservationRestService {

    private static final Logger log = Logger.getLogger("ConfirmReservationRestService");

    /**
     * Gets users option
     *
     * @param option user's option
     * @param token token
     * @return response
     */
    @GET
    @Path("/")
    public Response printMessage(@QueryParam("option") String option, @QueryParam("token") String token) {
        String result;
        ReservationDAOLocal rdao = null;
        try {
            rdao = (ReservationDAOLocal) new InitialContext().lookup("java:global/ROOT/ReservationSystem-ejb-1.0-SNAPSHOT/ReservationDAO!cz.muni.fi.dp.reservationsystem.ejb.ReservationDAO");
        } catch (NamingException ex) {
        }
        Reservation r = rdao.getReservationByToken(token);
        if (r == null) {
            result = "Invalid token!";
            return Response.status(401).entity(result).build();
        }
        if (option.equals("yes")) {
            r.setLastContacted(new Date());
            r.setToken(null);
            r.setTimesContacted(0);
            rdao.update(r);
            result = "Reservation not modified!";
            return Response.status(204).entity(result).build();
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 0);
            r.setUntil(new Date(cal.getTimeInMillis()));
            r.setToken(null);
            rdao.update(r);
            result = "Your reservation was cancelled!";
            return Response.status(200).entity(result).build();
        }
    }
}
