/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.dp.reservationsystem.ejb;

import cz.muni.fi.dp.reservationsystem.dao.Computer;
import cz.muni.fi.dp.reservationsystem.dao.ComputerReport;
import cz.muni.fi.dp.reservationsystem.dao.Reservation;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

/**
 *
 * @author Andrej
 */
@Stateless
@LocalBean
public class TimerSessionBean {

    @PersistenceUnit(unitName = "ReservationSystem-PU")
    private EntityManagerFactory factory;
    private static final Logger log = Logger.getLogger("ApplicationBean");
    @EJB
    private ReservationDAOLocal rdao;
    @EJB
    private ReportManagerLocal rm;
    private SimpleDateFormat format;
    @Resource(lookup = "java:jboss/mail/Default")
    private Session mailSession;
    private ResourceBundle rb = ResourceBundle.getBundle("application");

    /**
     *
     */
    public TimerSessionBean() {
        format = new SimpleDateFormat("MMM d, yyyy h:mm:ss a");

    }

    /**
     * Timer for CEP
     */
    @Schedule(minute = "0", second = "0", dayOfMonth = "*", month = "*", year = "*", hour = "19", dayOfWeek = "*", persistent = false)
    public void myTimer() {
        System.out.println("CEP Check " + new Date());
        cep();

    }

    /**
     * CEP
     */
    public void cep() {
        long oneDay = 86400000;
        Date todayDate = new Date();
        List<Reservation> reservations = rdao.getCurrentReservations();
        List<ComputerReport> reports = rm.getStatistics();
        if (reservations == null) {
            return;
        }

        //Check for no answers
        for (Reservation r : reservations) {
            if (r.getTimesContacted() >= 2) {
                sendCancelEmail(r);
            }
        }
        //Send prompt emails if requirements are met
        reservations = rdao.getCurrentReservations();
        for (Reservation r : reservations) {
            if (todayDate.getTime() - r.getSince().getTime() >= 7 * oneDay) {
                int interval = findComputerInList(r.getComputer(), reports);
                long lastContact = 0;
                if (r.getLastContacted() != null) {
                    lastContact = r.getLastContacted().getTime();
                }
                if (todayDate.getTime() - lastContact >= interval * oneDay) {
                    sendPromptEmail(r);
                }
            }
        }
    }

    /**
     * Sends prompt email to user
     *
     * @param r reservation
     */
    public void sendPromptEmail(Reservation r) {
        try {
            String uuid = UUID.randomUUID().toString();
            Message message = new MimeMessage(mailSession);
            MimeMultipart mpart = new MimeMultipart();
            MimeBodyPart bp = new MimeBodyPart();
            message.setHeader("Content-Type", "text/html");
            try {
                message.setFrom(new InternetAddress("noreply@reservationsystem", "Reservation System"));
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(TimerSessionBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(r.getUser().getEmail()));
            message.setSubject("Reservation of " + r.getComputer().getName());

            bp.setText("Dear " + r.getUser().getUserName() + ","
                    + "<br/> we would like to know whether you use your reserved computer."
                    + "<br/><br/> Details of your reservation:"
                    + "<br/> User name: " + r.getUser().getUserName()
                    + "<br/> Computer name: " + r.getComputer().getName()
                    + "<br/> Since: " + format.format(r.getSince())
                    + "<br/> Until:" + format.format(r.getUntil())
                    + "<br/><br/>"
                    + "Do you want to keep this reservation? "
                    + "<a href=\"" + rb.getString("path") + "/rest/confirm?option=yes&token=" + uuid + "\">Yes</a> - "
                    + "<a href=\"" + rb.getString("path") + "/rest/confirm?option=no&token=" + uuid + "\">No</a>"
                    + "<br/><br/>"
                    + "Thank you for your time.", "UTF-8", "html");
            mpart.addBodyPart(bp);
            message.setContent(mpart);
            Transport.send(message);
            r.setLastContacted(new Date());
            r.setToken(uuid);
            r.setTimesContacted(r.getTimesContacted() + 1);
            rdao.update(r);
            log.log(Level.WARNING, "Sent prompt email, user: " + r.getUser().getUserName() + ", reservation: " + r.getId());
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends cancel email to user
     *
     * @param r reservation
     */
    public void sendCancelEmail(Reservation r) {
        try {
            Date cancelDate = new Date();
            Message message = new MimeMessage(mailSession);
            MimeMultipart mpart = new MimeMultipart();
            MimeBodyPart bp = new MimeBodyPart();
            message.setHeader("Content-Type", "text/html");
            try {
                message.setFrom(new InternetAddress("noreply@reservationsystem", "Reservation System"));
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(TimerSessionBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(r.getUser().getEmail()));
            message.setSubject("Reservation cancelled");
            bp.setText("Dear " + r.getUser().getUserName() + ","
                    + "<br/> your reservation was cancelled due to your inactivity."
                    + "<br/><br/> Details of your reservation:"
                    + "<br/> User name: " + r.getUser().getUserName()
                    + "<br/> Computer name: " + r.getComputer().getName()
                    + "<br/> Since: " + format.format(r.getSince())
                    + "<br/> Until:" + format.format(r.getUntil()), "UTF-8", "html");
            mpart.addBodyPart(bp);
            message.setContent(mpart);
            Transport.send(message);
            r.setUntil(cancelDate);
            rdao.update(r);
            log.log(Level.WARNING, "Sent cancel email, user: " + r.getUser().getUserName() + ", reservation: " + r.getId());
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Finds computer's interval in list
     *
     * @param c computer
     * @param l list
     * @return interval number
     */
    public int findComputerInList(Computer c, List l) {
        int listSize = l.size();
        if (listSize < 3) {
            return 1;
        }
        int beginInterval = 0;
        int endInterval = listSize / 3;
        for (int k = 3; k > 0; k--) {
            for (int i = beginInterval; i < endInterval; i++) {
                if (l.get(i).equals(c)) {
                    return k;
                }
            }
            beginInterval = endInterval;
            if (k == 2) {
                endInterval += listSize - beginInterval;
            } else {
                endInterval += listSize / 3;
            }

        }
        return 0;
    }
}
