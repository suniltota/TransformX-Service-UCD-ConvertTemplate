//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.12.01 at 06:02:48 PM IST 
//


package org.mismo.residential._2009.schemas;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 *  collection of objects that describe the liabilities of borrower parties that were examined to determine credit worthiness
 * 
 * <p>Java class for LIABILITIES complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LIABILITIES">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="LIABILITY" type="{http://www.mismo.org/residential/2009/schemas}LIABILITY" maxOccurs="23" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LIABILITIES", propOrder = {
    "liability"
})
public class LIABILITIES {

    @XmlElement(name = "LIABILITY")
    protected List<LIABILITY> liability;

    /**
     * Gets the value of the liability property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the liability property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLIABILITY().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LIABILITY }
     * 
     * 
     */
    public List<LIABILITY> getLIABILITY() {
        if (liability == null) {
            liability = new ArrayList<LIABILITY>();
        }
        return this.liability;
    }

}