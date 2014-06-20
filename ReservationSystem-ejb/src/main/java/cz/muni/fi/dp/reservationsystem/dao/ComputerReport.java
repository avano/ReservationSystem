/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.dp.reservationsystem.dao;

/**
 * Class that represents computer report
 * @author Andrej
 */
public class ComputerReport {
    private Computer c;
    private String name = "";
    private int reservedHours;
    private int totalHours;
    private String mostReservations = "";
    private String output;

    public Computer getC() {
        return c;
    }

    public void setC(Computer c) {
        this.c = c;
    }
    
    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getReservedHours() {
        return reservedHours;
    }

    public void setReservedHours(int reservedHours) {
        this.reservedHours = reservedHours;
    }

    public int getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(int totalHours) {
        this.totalHours = totalHours;
    }

    public String getMostReservations() {
        return mostReservations;
    }

    public void setMostReservations(String mostReservations) {
        this.mostReservations = mostReservations;
    }
    
    
}
