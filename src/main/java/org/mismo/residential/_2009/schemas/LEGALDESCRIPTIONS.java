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
 * Information about one or more types of legal descriptions. Holds all occurrences of LEGAL_DESCRIPTION.
 * 
 * <p>Java class for LEGAL_DESCRIPTIONS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LEGAL_DESCRIPTIONS">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="LEGAL_DESCRIPTION" type="{http://www.mismo.org/residential/2009/schemas}LEGAL_DESCRIPTION" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LEGAL_DESCRIPTIONS", propOrder = {
    "legaldescription"
})
public class LEGALDESCRIPTIONS {

    @XmlElement(name = "LEGAL_DESCRIPTION")
    protected LEGALDESCRIPTION legaldescription;

    /**
     * Gets the value of the legaldescription property.
     * 
     * @return
     *     possible object is
     *     {@link LEGALDESCRIPTION }
     *     
     */
    public LEGALDESCRIPTION getLEGALDESCRIPTION() {
        return legaldescription;
    }

    /**
     * Sets the value of the legaldescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link LEGALDESCRIPTION }
     *     
     */
    public void setLEGALDESCRIPTION(LEGALDESCRIPTION value) {
        this.legaldescription = value;
    }

}
