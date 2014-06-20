/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.dp.reservationsystem.ejb;

import cz.muni.fi.dp.reservationsystem.dao.Computer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Class that is accessing computers in db
 *
 * @author Andrej
 */
@Stateless(name = "ComputerDAO", mappedName = "ComputerDAO")
@LocalBean
public class ComputerDAO implements ComputerDAOLocal {

    @EJB
    private ReservationDAOLocal rdao;
    @PersistenceContext(unitName = "ReservationSystem-PU")
    private EntityManager em;
    private static final Logger log = Logger.getLogger("ComputerDAOImplLogger");

    /**
     * Constructor
     */
    public ComputerDAO() {
    }

    /**
     * Gets computer by it's id
     *
     * @param id
     * @return computer or null
     */
    @Override
    public Computer getComputerById(long id) {

        Query q = em.createQuery("select computer from Computer computer where id = ?1");
        q.setParameter(1, id);
        Computer c = null;
        try {
            c = (Computer) q.getSingleResult();
        } catch (Exception ex) {
        }
        return c;
    }

    /**
     * Gets all computers
     *
     * @return list of all computers
     */
    @Override
    public List<Computer> getAllComputers() {
        Query q = em.createQuery("select computer from Computer computer ORDER BY id ASC");
        return q.getResultList();
    }

    /**
     * Gets computers as list in selected interval
     *
     * @param start start of the interval
     * @param end end of the interval
     * @return list of computers in selected interval
     */
    @Override
    public List<Computer> getComputersRange(int start, int end) {
        Query q = em.createQuery("SELECT computer from Computer computer where computer.id between ?1 and ?2 ORDER BY id ASC");
        q.setParameter(1, start);
        q.setParameter(2, end);
        return q.getResultList();
    }

    /**
     * Gets computers count
     *
     * @return computers count
     */
    @Override
    public int getComputersCount() {
        Query q = em.createQuery("SELECT computer from Computer computer");
        return q.getResultList().size();
    }

    /**
     * Gets computers by query
     *
     * @param query query
     * @return computers matching the query
     */
    @Override
    public List<Computer> getComputersByQuery(String query) {
        if (query.contains("||")) {
            Set<Computer> tempSet = new HashSet<Computer>();
            String[] or = query.split("\\|\\|");
            for (int i = 0; i < or.length; i++) {
                Query q = em.createQuery("from Computer as computer where (name LIKE :q) or (ip LIKE :q) or (compGroup LIKE :q) or (description LIKE :q)");
                q.setParameter("q", "%" + or[i].trim() + "%");
                tempSet.addAll(q.getResultList());
            }
            List<Computer> retList = new ArrayList<Computer>();
            retList.addAll(tempSet);
            Collections.sort(retList);
            return retList;
        }
        else if (query.contains("&&")) {
            List<Computer> retList = new ArrayList<Computer>();
            String[] and = query.split("&&");
            Query q = em.createQuery("from Computer as computer where ((name LIKE :q) or (ip LIKE :q) or (compGroup LIKE :q) or (description LIKE :q)) and computer in (from Computer as computer2 where (name LIKE :p) or (ip LIKE :p) or (compGroup LIKE :p) or (description LIKE :p))");
            q.setParameter("q", "%" + and[0].trim() + "%");
            q.setParameter("p", "%" + and[1].trim() + "%");
            return q.getResultList();
        }
        
        Query q = em.createQuery("from Computer as computer where (name LIKE :q) or (ip LIKE :q) or (compGroup LIKE :q) or (description LIKE :q)");
        q.setParameter("q", "%" + query + "%");
        return q.getResultList();
        
    }

    /**
     * Updates the computer
     *
     * @param computer computer
     */
    @Override
    public void update(Computer computer) {
        em.merge(computer);
        em.flush();
    }

    /**
     * Adds new computer
     *
     * @param computer new computer
     */
    @Override
    public void addNewComputer(Computer computer) {
        em.persist(computer);
        em.flush();
    }

    /**
     * Deletes computer
     *
     * @param computer computer
     */
    @Override
    public void deleteComputer(Computer computer) {
        rdao.deleteAllReservationsByComputer(computer);
        Computer c = em.find(Computer.class, computer.getId());
        em.remove(c);
        em.flush();
    }

    /**
     * Gets computer by its name
     *
     * @param name name
     * @return computer
     */
    @Override
    public Computer getComputerByName(String name) {
        Query q = em.createQuery("select computer from Computer computer where name = ?1");
        q.setParameter(1, name);
        Computer c = null;
        try {
            c = (Computer) q.getSingleResult();
        } catch (Exception ex) {
        }
        return c;
    }

    /**
     * Gets computer by ip
     *
     * @param ip ip
     * @return computer
     */
    @Override
    public Computer getComputerByIp(String ip) {
        Query q = em.createQuery("select computer from Computer computer where ip = ?1");
        q.setParameter(1, ip);
        Computer c = null;
        try {
            c = (Computer) q.getSingleResult();
        } catch (Exception ex) {
        }
        return c;
    }
}
