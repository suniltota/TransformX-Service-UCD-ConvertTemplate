//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.12.01 at 06:02:48 PM IST 
//


package org.mismo.residential._2009.schemas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * TERM NOT FOUND
 * 
 * <p>Java class for PAID_BY complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PAID_BY">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice minOccurs="0">
 *           &lt;element name="INDIVIDUAL" type="{http://www.mismo.org/residential/2009/schemas}INDIVIDUAL" minOccurs="0"/>
 *           &lt;element name="LEGAL_ENTITY" type="{http://www.mismo.org/residential/2009/schemas}LEGAL_ENTITY" minOccurs="0"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PAID_BY", propOrder = {
    "individual",
    "legalentity"
})
public class PAIDBY {

    @XmlElement(name = "INDIVIDUAL")
    protected INDIVIDUAL individual;
    @XmlElement(name = "LEGAL_ENTITY")
    protected LEGALENTITY legalentity;

    /**
     * Gets the value of the individual property.
     * 
     * @return
     *     possible object is
     *     {@link INDIVIDUAL }
     *     
     */
    public INDIVIDUAL getINDIVIDUAL() {
        return individual;
    }

    /**
     * Sets the value of the individual property.
     * 
     * @param value
     *     allowed object is
     *     {@link INDIVIDUAL }
     *     
     */
    public void setINDIVIDUAL(INDIVIDUAL value) {
        this.individual = value;
    }

    /**
     * Gets the value of the legalentity property.
     * 
     * @return
     *     possible object is
     *     {@link LEGALENTITY }
     *     
     */
    public LEGALENTITY getLEGALENTITY() {
        return legalentity;
    }

    /**
     * Sets the value of the legalentity property.
     * 
     * @param value
     *     allowed object is
     *     {@link LEGALENTITY }
     *     
     */
    public void setLEGALENTITY(LEGALENTITY value) {
        this.legalentity = value;
    }

}
