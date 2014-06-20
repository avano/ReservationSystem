/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.dp.reservationsystem.security;

import cz.muni.fi.dp.reservationsystem.dao.User;
import cz.muni.fi.dp.reservationsystem.ejb.UserDAOLocal;
import cz.muni.fi.dp.reservationsystem.ejb.UserDAO;
import cz.muni.fi.dp.reservationsystem.other.CookieManager;
import java.io.IOException;
import javax.ejb.EJB;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Filter for unauthorized access
 *
 * @author Andrej
 */
public class AutologinFilter implements Filter {
    @EJB
    private UserDAOLocal udao;
    private FilterConfig fc;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.fc = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        User user = (User) req.getSession().getAttribute("user"); //gets user from session
        String uuid = null;
        if (user == null) { //if there is no user in session
            uuid = CookieManager.getCookieValue(req, "remember_id"); //gets cookie value
            if (uuid != null) {
                user = udao.getUserByCookie(uuid); //checks in db if there is user with this cookie
                if (user != null) {
                    req.getSession().setAttribute("user", user); //adds user in session
                    CookieManager.addCookie(res, "remember_id", uuid, 2592000); //extends age of the cookie
                } else {
                    CookieManager.removeCookie(res, "remember_id"); //if there is a cookie but no user, delete the cookie
                }
            }
        }
        String[] redirectTo = req.getRequestURI().split("/");
        if (user != null && redirectTo.length == 0) {
            res.sendRedirect("calendar.xhtml");
            return;
        }
        if (user != null && redirectTo[1].equals("index.xhtml")) {
            res.sendRedirect("calendar.xhtml");
            return;
        }
        if (user == null && redirectTo.length == 0) {
            chain.doFilter(req, res);
            return;
        }
        if (user == null && redirectTo[1].equals("index.xhtml")) {
            chain.doFilter(req, res);
            return;
        }
        if (user == null) { //if the user wasn't logged, add this page to cookie and redirect him to login page
            if (redirectTo.length > 1) {
                CookieManager.addCookie(res, "redirectTo", redirectTo[1], 2592000);
            }
            res.sendRedirect("index.xhtml");
        } else {
            chain.doFilter(req, res);
        }
    }

    @Override
    public void destroy() {
    }
}
