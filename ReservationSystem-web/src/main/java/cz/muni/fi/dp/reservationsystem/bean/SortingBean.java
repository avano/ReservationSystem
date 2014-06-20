/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.dp.reservationsystem.bean;

import java.io.Serializable;
import org.richfaces.component.SortOrder;

/**
 *
 * @author Andrej
 */
public class SortingBean implements Serializable {
    private SortOrder nameOrder = SortOrder.unsorted;
    private SortOrder sinceOrder = SortOrder.unsorted;
    private SortOrder untilOrder = SortOrder.unsorted;
    private SortOrder secondOrder = SortOrder.unsorted;
    private SortOrder thirdOrder = SortOrder.unsorted;
    private SortOrder fourthOrder = SortOrder.unsorted;
    
    /**
     *
     * @return
     */
    public SortOrder getNameOrder() {
        return nameOrder;
    }

    /**
     *
     * @param nameOrder
     */
    public void setNameOrder(SortOrder nameOrder) {
        this.nameOrder = nameOrder;
    }

    /**
     *
     * @return
     */
    public SortOrder getSinceOrder() {
        return sinceOrder;
    }

    /**
     *
     * @param dateOrder
     */
    public void setSinceOrder(SortOrder dateOrder) {
        this.sinceOrder = dateOrder;
    }

    /**
     *
     * @return
     */
    public SortOrder getUntilOrder() {
        return untilOrder;
    }

    /**
     *
     * @param untilOrder
     */
    public void setUntilOrder(SortOrder untilOrder) {
        this.untilOrder = untilOrder;
    }

    /**
     *
     * @return
     */
    public SortOrder getSecondOrder() {
        return secondOrder;
    }

    /**
     *
     * @param ipOrder
     */
    public void setSecondOrder(SortOrder ipOrder) {
        this.secondOrder = ipOrder;
    }

    /**
     *
     * @return
     */
    public SortOrder getThirdOrder() {
        return thirdOrder;
    }

    /**
     *
     * @param groupOrder
     */
    public void setThirdOrder(SortOrder groupOrder) {
        this.thirdOrder = groupOrder;
    }

    /**
     *
     * @return
     */
    public SortOrder getFourthOrder() {
        return fourthOrder;
    }

    /**
     *
     * @param fourthOrder
     */
    public void setFourthOrder(SortOrder fourthOrder) {
        this.fourthOrder = fourthOrder;
    }
    
    /**
     * Sorts the table by name
     */
    public void sortByName() {
        sinceOrder = SortOrder.unsorted;
        untilOrder = SortOrder.unsorted;
        secondOrder = SortOrder.unsorted;
        thirdOrder = SortOrder.unsorted;
        fourthOrder = SortOrder.unsorted;
        if (nameOrder.equals(SortOrder.ascending)) {
            setNameOrder(SortOrder.descending);
        }
        else {
            setNameOrder(SortOrder.ascending);
        }
    }
    
    /**
     * Sorts the table by since
     */
    public void sortBySince() {
        nameOrder = SortOrder.unsorted;
        untilOrder = SortOrder.unsorted;
        secondOrder = SortOrder.unsorted;
        thirdOrder = SortOrder.unsorted;
        fourthOrder = SortOrder.unsorted;
        if (sinceOrder.equals(SortOrder.ascending)) {
            setSinceOrder(SortOrder.descending);
        }
        else {
            setSinceOrder(SortOrder.ascending);
        }
    }
    
    /**
     * Sorts the table by until
     */
    public void sortByUntil() {
        nameOrder = SortOrder.unsorted;
        sinceOrder = SortOrder.unsorted;
        secondOrder = SortOrder.unsorted;
        thirdOrder = SortOrder.unsorted;
        fourthOrder = SortOrder.unsorted;
        if (untilOrder.equals(SortOrder.ascending)) {
            setUntilOrder(SortOrder.descending);
        }
        else {
            setUntilOrder(SortOrder.ascending);
        }
    }
    
    /**
     * Sorts the table by another condition
     */
    public void secondSort() {
        nameOrder = SortOrder.unsorted;
        sinceOrder = SortOrder.unsorted;
        untilOrder = SortOrder.unsorted;
        thirdOrder = SortOrder.unsorted;
        fourthOrder = SortOrder.unsorted;
        if (secondOrder.equals(SortOrder.ascending)) {
            setSecondOrder(SortOrder.descending);
        }
        else {
            setSecondOrder(SortOrder.ascending);
        }
    }
    
    /**
     * Sorts the table by another condition
     */
    public void thirdSort() {
        nameOrder = SortOrder.unsorted;
        sinceOrder = SortOrder.unsorted;
        untilOrder = SortOrder.unsorted;
        secondOrder = SortOrder.unsorted;
        fourthOrder = SortOrder.unsorted;
        if (thirdOrder.equals(SortOrder.ascending)) {
            setThirdOrder(SortOrder.descending);
        }
        else {
            setThirdOrder(SortOrder.ascending);
        }
    }
    
    /**
     * Sorts the table by another condition
     */
    public void fourthSort() {
        nameOrder = SortOrder.unsorted;
        sinceOrder = SortOrder.unsorted;
        untilOrder = SortOrder.unsorted;
        secondOrder = SortOrder.unsorted;
        thirdOrder = SortOrder.unsorted;
        if (fourthOrder.equals(SortOrder.ascending)) {
            setFourthOrder(SortOrder.descending);
        }
        else {
            setFourthOrder(SortOrder.ascending);
        }
    }
    
}
