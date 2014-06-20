/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.dp.reservationsystem.bean;

import cz.muni.fi.dp.reservationsystem.ejb.ReportManagerLocal;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

/**
 *
 * @author Andrej
 */
public class ReportBean {
    @EJB
    private ReportManagerLocal rm;
    private static final Logger log = Logger.getLogger("ReportBean");

    /**
     * Creates a new instance of ReportBean
     */
    public ReportBean() {
    }

    

    /**
     * Creates a report and writes it to the outputstream
     * @param out outputstream
     * @param obj object
     */
    public void createReport(OutputStream out, Object obj) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        ExternalContext ext = ctx.getExternalContext();
        HttpServletResponse response = (HttpServletResponse) ext.getResponse();

        response.setContentType("application/pdf");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FileInputStream fis = null;
        BufferedInputStream bufferedInputStream;
        try {
            String reportLocation = ext.getRealPath("WEB-INF");
            try {
                JasperCompileManager.compileReportToFile(reportLocation + "/jasper_template.jrxml", reportLocation + "/jasper_template.jasper");
            } catch (JRException ex) {
                log.log(Level.WARNING, ex.toString());
            }

            try {
                fis = new FileInputStream(reportLocation + "/jasper_template.jasper");
            } catch (FileNotFoundException ex) {
                log.log(Level.SEVERE, ex.toString());
            }
            bufferedInputStream = new BufferedInputStream(fis);

            JRBeanCollectionDataSource jrbcds = new JRBeanCollectionDataSource(rm.getStatistics());
            try {
                JasperReport jasperReport = (JasperReport) JRLoader.loadObject(bufferedInputStream);
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap(), jrbcds);
                JasperExportManager.exportReportToPdfStream(jasperPrint, baos);
            } catch (JRException ex) {
                log.log(Level.WARNING, ex.toString());
            }
            response.setContentLength(baos.size());
            try {
                baos.writeTo(out);

                fis.close();

                bufferedInputStream.close();
            } catch (IOException ex) {
                log.log(Level.SEVERE, ex.toString());
            }
        } finally {
            try {
                baos.close();
            } catch (IOException ex) {
                log.log(Level.SEVERE, ex.toString());
            }
        }
    }
}
