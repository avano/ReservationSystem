/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.dp.reservationsystem.ejb;

import cz.muni.fi.dp.reservationsystem.dao.ComputerReport;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Andrej
 */
@Local
public interface ReportManagerLocal {
    /**
     * Gets computer statistics
     * @return list of computer reports
     */
    public List<ComputerReport> getStatistics();
}
