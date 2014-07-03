/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.dp.reservationsystem.servlet;

import org.richfaces.json.JSONArray;
import org.richfaces.json.JSONException;
import org.richfaces.json.JSONObject;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
public class JSONServlet extends HttpServlet {

	@EJB
	private ComputerDAOLocal cdao;
	@EJB
	private ReservationDAOLocal rdao;
	@EJB
	private UserDAOLocal udao;
	private static final Logger log = Logger.getLogger("JSONServlet");

	/**
	 * Processes requests for both HTTP
	 * <code>GET</code> and
	 * <code>POST</code> methods.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		final String query = request.getParameter("query");
		final String userId = request.getParameter("userId");
		final String computerId = request.getParameter("computerId");

		final JSONArray retArray = new JSONArray();
		List<Reservation> reservations = null;
		boolean oneComputer = false;
		if (!"null".equals(computerId)) {
			oneComputer = true;
		}
		if ("null".equals(userId)) {
			if (!oneComputer) {
				reservations = rdao.getAllReservations();
			} else {
				reservations = rdao.getReservationsByComputer(Long.parseLong(computerId));
			}
		} else {
			reservations = rdao.getReservationsByUser(udao.getUserById(Long.parseLong(userId)));
		}
		List<Computer> allComps = new ArrayList<Computer>();

		if ("".equals(query) || query == null || "null".equals(query)) {
			allComps = cdao.getAllComputers();
		} else {
			allComps = cdao.getComputersByQuery(query);
		}
		if (oneComputer) {
			allComps.clear();
			allComps.add(cdao.getComputerById(Long.parseLong(computerId)));
		}
		for (final Computer c : allComps) {
			final JSONArray resArray = new JSONArray();
			try {
				final List<Reservation> toRemove = new ArrayList<Reservation>();
				for (final Reservation r : reservations) {
					if (c.getId() == r.getComputer().getId()) {
						final JSONObject tempObj = new JSONObject();
						tempObj.put("since", r.getSince().getTime());
						tempObj.put("until", r.getUntil().getTime());
						final User user = r.getUser();
						tempObj.put("user", user.getUserName());
						tempObj.put("comment", r.getComment());
						resArray.put(tempObj);
						toRemove.add(r);
					}
				}
				reservations.removeAll(toRemove);
				final JSONObject ret = new JSONObject();
				ret.put("name", c.getName());
				ret.put("id", c.getId());
				if (resArray.length() != 0) {
					ret.put("reservations", resArray);
				}
				ret.put("compName", c.getName());
				retArray.put(ret);
			} catch (final JSONException ex) {
				Logger.getLogger(JSONServlet.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		response.setCharacterEncoding(
				"UTF-8");
		response.setContentType(
				"application/json");
		response.getWriter()
				.write(retArray.toString());
		final PrintWriter out = response.getWriter();
	}

	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

	/**
	 * Handles the HTTP
	 * <code>GET</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Handles the HTTP
	 * <code>POST</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Returns a short description of the servlet.
	 *
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Short description";
	}// </editor-fold>
}
