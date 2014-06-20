/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.dp.reservationsystem.bean;

import cz.muni.fi.dp.reservationsystem.ejb.UserDAOLocal;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 * Bean for request-scoped methods
 *
 * @author Andrej
 */
public class RequestBean {   
    private static final Logger log = Logger.getLogger("RequestBean");

    /**
     * Creates a new instance of RequestBean
     */
    public RequestBean() {
    }

    /**
     * Gets session expiration time
     *
     * @return session expiration time - 10
     */
    public int getSessionExpiracy() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        return session.getMaxInactiveInterval() - 10;
    }
    
    
}
