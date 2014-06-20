/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.dp.reservationsystem.bean;

import cz.muni.fi.dp.reservationsystem.dao.Computer;
import cz.muni.fi.dp.reservationsystem.dao.User;
import cz.muni.fi.dp.reservationsystem.ejb.ComputerDAOLocal;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

/**
 * Bean that implements all methods with computers
 *
 * @author Andrej
 */
public class ComputerBean implements Serializable {

    @EJB
    private ComputerDAOLocal cdao;
    private static final Logger log = Logger.getLogger("ComputerBean");
    private DataModel computersDM;
    private Computer current;
    private int selectedItemIndex;
    private int startId = 1;
    private int endId = 5;
    private int pageSize = 5;
    private int compCount = 0;
    private String ip;
    private String name;
    private String group;
    private String description;
    private String computerId;

    /**
     *
     * @return
     */
    public String getComputerId() {
        return computerId;
    }

    /**
     *
     * @param computerId
     */
    public void setComputerId(String computerId) {
        this.computerId = computerId;
    }

    /**
     *
     * @return
     */
    public String getIp() {
        return ip;
    }

    /**
     *
     * @param ip
     */
    public void setIp(String ip) {
        this.ip = ip;
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
    public String getGroup() {
        return group;
    }

    /**
     *
     * @param group
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Adds new reservation
     */
    public void addNewComputer() {
        ResourceBundle msg = ResourceBundle.getBundle("messages", new Locale(LanguageBean.getLocaleFromCookie()));
        Computer c = new Computer();
        c.setIp(ip);
        c.setName(name);
        c.setCompGroup(group);
        c.setDescription(description);
        ip = "";
        name = "";
        group = "";
        description = "";
        if (cdao.getComputerByName(c.getName()) != null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg.getString("duplicateName") + ": " + c.getName(), null));
            return;
        }
        cdao.addNewComputer(c);
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        User userLogged = (User) req.getSession().getAttribute("user");

        log.log(Level.INFO, "New computer: " + c.getName() + " (added. by: " + userLogged.getEmail() + ")");

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(msg.getString("successfulAdded") + ": " + c.getName()));

    }

    /**
     * Creates a new instance of ComputerBean
     */
    public ComputerBean() {
    }

    @PostConstruct
    private void init() {
        compCount = cdao.getAllComputers().size();
    }

    /**
     * Gets current computer in the datamodel
     *
     * @return current selected computer
     */
    public Computer getSelected() {
        if (current == null) {
            current = new Computer();
            selectedItemIndex = -1;
        }
        return current;
    }

    /**
     *
     * @return
     */
    public int getCompCount() {
        return compCount;
    }

    /**
     *
     * @param compCount
     */
    public void setCompCount(int compCount) {
        this.compCount = compCount;
    }

    /**
     * Gets computer by id
     *
     * @param id
     * @return computer by id or null
     */
    public Computer searchComputerById(long id) {
        return cdao.getComputerById(id);
    }

    /**
     * Return new computers datamodel if previous was null
     *
     * @return computersDM computers datamodel
     */
    public DataModel getComputers() {
        //if (computersDM == null) {
        return computersDM = new ListDataModel(cdao.getAllComputers());
        //}
        //return computersDM;
    }

    /**
     * Sets computers datamodel to null
     */
    public void recreateModel() {
        computersDM = null;
    }

    /**
     * Redirects to the page with computer details
     *
     * @return url to page with computer details
     */
    public String viewDetails() {
        current = (Computer) getCurrentComputers().getRowData();
        return "computerDetails.xhtml?faces-redirect=true";
    }

    /**
     * Gets current computer and redirects to its details
     *
     * @return location string
     */
    public String redirect() {
        Long longId = null;
        try {
            longId = Long.parseLong(computerId);
        } catch (NumberFormatException ex) {
            return null;
        }
        current = cdao.getComputerById(longId);
        return "computerDetails.xhtml?faces-redirect=true";
    }

    /**
     *
     * @return
     */
    public DataModel getCurrentComputers() {
        return computersDM;
    }

    /**
     * Checks if there is next page
     *
     * @return true if there is next page, false otherwise
     */
    public boolean hasNextPage() {
        if (endId < compCount) {
            return true;
        }
        return false;
    }

    /**
     * Check if there is a previous page
     *
     * @return true if there is previous page, false otherwise
     */
    public boolean hasPreviousPage() {
        if (startId - pageSize > 0) {
            return true;
        }
        return false;
    }

    /**
     * Redirects to the same page and recreates datamodel
     *
     * @return url to this page
     */
    public String next() {
        startId = endId + 1;
        endId = endId + pageSize;
        recreateModel();
        return "allComputers.xhtml?faces-redirect=true";
    }

    /**
     * Redirects to the same page and recreates datamodel
     *
     * @return url to this page
     */
    public String previous() {
        startId = startId - pageSize;
        endId = endId - pageSize;
        recreateModel();
        return "allComputers.xhtml?faces-redirect=true";
    }

    /**
     * Returns page size
     *
     * @return pagesize page size
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * Redirects to this page and sets all computers into datamodel
     *
     * @return url to this page
     */
    public String getAllComputers() {
        startId = 1;
        endId = cdao.getAllComputers().size();
        recreateModel();
        return "allComputers.xhtml?faces-redirect=true";
    }

    /**
     * Redirects to this page and sets first 'pagesize' computers into datamodel
     *
     * @return url to this page
     */
    public String getListedComputers() {
        startId = 1;
        endId = pageSize;
        recreateModel();
        return "allComputers.xhtml?faces-redirect=true";
    }

    /**
     * Updates current computer
     */
    public void update() {
        ResourceBundle msg = ResourceBundle.getBundle("messages", new Locale(LanguageBean.getLocaleFromCookie()));
        Computer c = cdao.getComputerByName(current.getName());
        if (c != null) {
            if (c.getId() != current.getId()) {
                FacesContext.getCurrentInstance().addMessage("udaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, msg.getString("duplicateName") + ": " + current.getName(), msg.getString("duplicateName") + ": " + current.getName()));
                return;
            }
        }

        cdao.update(current);
        FacesContext.getCurrentInstance().addMessage("udaje", new FacesMessage(msg.getString("successfulUpdate")));
    }

    /**
     * Gets computer name
     *
     * @param computer_id computer id
     * @return computer name
     */
    public String getComputerName(long computer_id) {
        return cdao.getComputerById(computer_id).getName();
    }

    /**
     * Gets current computer from table
     */
    public void getCurrentComputer() {
        current = (Computer) getCurrentComputers().getRowData();
    }

    /**
     *
     * @return
     */
    public Computer getCurrent() {
        return current;
    }

    /**
     *
     * @param current
     */
    public void setCurrent(Computer current) {
        this.current = current;
    }

    /**
     * Deletes current computer
     *
     * @return empty string
     */
    public String deleteComputer() {
        cdao.deleteComputer(current);
        return "";
    }

    /**
     * Gets list of computers
     *
     * @return list of computers
     */
    public List getComputersArray() {
        List<SelectItem> list = new ArrayList<SelectItem>();
        List<Computer> clist = cdao.getAllComputers();
        for (Computer c : clist) {
            list.add(new SelectItem(c.getId(), c.getName()));
        }
        return list;
    }
}
