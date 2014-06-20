/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.dp.reservationsystem.ejb;

import cz.muni.fi.dp.reservationsystem.dao.Computer;
import cz.muni.fi.dp.reservationsystem.dao.ComputerReport;
import cz.muni.fi.dp.reservationsystem.dao.Reservation;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author Andrej
 */
@Stateless(name="ReportManager", mappedName="ReportManager")
public class ReportManager implements ReportManagerLocal {
    @EJB
    private ComputerDAOLocal cdao;
    @EJB
    private ReservationDAOLocal rdao;
    
    /**
     * Gets computer statistics
     * @return list of computer reports
     */
    @Override
    public List<ComputerReport> getStatistics() {
        List<Computer> computers = cdao.getAllComputers();
        List<Reservation> reservations = rdao.getLastMonthReservations();
        if (computers == null) {
            return null;
        }
        if (reservations == null) {
            return null;
        }
        int numberOfComputers = (int) computers.get(computers.size() - 1).getId();
        List<ComputerReport> computerReports = new ArrayList<ComputerReport>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_MONTH, -29);
        Date d = new Date(cal.getTimeInMillis());
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 0);
        Date today = new Date(cal.getTimeInMillis());
        for (int i = 0; i < numberOfComputers; i++) {
            computerReports.add(null);
        }
        for (Computer c : computers) {
            ComputerReport cr = new ComputerReport();
            cr.setC(c);
            cr.setName(c.getName());
            cr.setTotalHours(720);
            cr.setReservedHours(0);
            cr.setOutput(cr.getReservedHours() + "/" + cr.getTotalHours());
            computerReports.add((int) (c.getId() - 1), cr);
        }

        for (Reservation r : reservations) {
            int minusSince = 0;
            int minusUntil = 0;
            ComputerReport cr = computerReports.get((int) (r.getComputer().getId() - 1));
            int hours = cr.getReservedHours();
            if (r.getSince().getTime() < d.getTime()) {
                minusSince = (int) ((d.getTime() - r.getSince().getTime()) / (1000 * 60 * 60));
            }
            if (r.getUntil().getTime() > today.getTime()) {
                minusUntil = (int) ((r.getUntil().getTime() - today.getTime()) / (1000 * 60 * 60));
            }
            hours += ((int) ((r.getUntil().getTime() - r.getSince().getTime() + 1000) / 3600000)) - (minusSince) - (minusUntil);
            cr.setReservedHours(hours);
            cr.setOutput(cr.getReservedHours() + "/" + cr.getTotalHours());
        }
        for (int i = computerReports.size() - 1; i >= 0; i--) {
            if (computerReports.get(i) == null) {
                computerReports.remove(i);
            }
        }
        Collections.sort(computerReports, new Comparator<ComputerReport>() {
            @Override
            public int compare(ComputerReport cr1, ComputerReport cr2) {
                return cr2.getReservedHours() - cr1.getReservedHours();
            }
        });
        return Collections.unmodifiableList(computerReports);
    }
}
