/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.dp.reservationsystem.ejb;

import cz.muni.fi.dp.reservationsystem.dao.Computer;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Andrej
 */
@Local
public interface ComputerDAOLocal {
    /**
     * Adds new computer
     * @param computer new computer
     */
    public void addNewComputer(Computer computer);
    /**
     * Gets computer by it's id
     *
     * @param id
     * @return computer or null
     */
    public Computer getComputerById(long id);
    /**
     * Gets computer by ip
     * @param ip ip
     * @return computer
     */
    public Computer getComputerByIp(String ip);
    /**
     * Gets all computers
     *
     * @return list of all computers
     */
    public List<Computer> getAllComputers();
    /**
     * Gets computers as list in selected interval
     *
     * @param start start of the interval
     * @param end end of the interval
     * @return list of computers in selected interval
     */
    public List<Computer> getComputersRange(int start, int end);
    /**
     * Gets computers by query
     * @param query query
     * @return computers matching the query
     */
    public List<Computer> getComputersByQuery(String query);
    /**
     * Updates the computer
     * @param computer computer
     */
    public void update(Computer computer);
    /**
     * Gets computers count
     * @return computers count
     */
    public int getComputersCount();
    /**
     * Deletes computer
     * @param computer computer
     */
    public void deleteComputer(Computer computer);
    /**
     * Gets computer by its name
     * @param name name
     * @return computer
     */
    public Computer getComputerByName(String name);
}
