/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.dp.reservationsystem.dao;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Class that represents computer
 *
 * @author Andrej
 */

@Entity
@Table(name="computers")
public class Computer implements Serializable, Comparable {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;
    
    @Column(name="name", nullable=false, length=50)
    private String name;
    
    @Column(name="ip", nullable=false, length=50)
    private String ip;
    
    @Column(name="description", nullable=true, length=255)
    private String description;
    
    @Column(name="compGroup", nullable=true, length=30)
    private String compGroup;

 
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCompGroup() {
        return compGroup;
    }

    public void setCompGroup(String compGroup) {
        this.compGroup = compGroup;
    }

    public long getId() {
        return id;
    }

    private void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(Object o) {
        Computer c = (Computer) o;
        return (int) (this.id - c.getId());
    }

}
