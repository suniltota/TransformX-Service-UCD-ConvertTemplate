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
 * <p>Java class for BUYDOWN_OCCURRENCES complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BUYDOWN_OCCURRENCES">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="BUYDOWN_OCCURRENCE" type="{http://www.mismo.org/residential/2009/schemas}BUYDOWN_OCCURRENCE" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BUYDOWN_OCCURRENCES", propOrder = {
    "buydownoccurrence"
})
public class BUYDOWNOCCURRENCES {

    @XmlElement(name = "BUYDOWN_OCCURRENCE")
    protected BUYDOWNOCCURRENCE buydownoccurrence;

    /**
     * Gets the value of the buydownoccurrence property.
     * 
     * @return
     *     possible object is
     *     {@link BUYDOWNOCCURRENCE }
     *     
     */
    public BUYDOWNOCCURRENCE getBUYDOWNOCCURRENCE() {
        return buydownoccurrence;
    }

    /**
     * Sets the value of the buydownoccurrence property.
     * 
     * @param value
     *     allowed object is
     *     {@link BUYDOWNOCCURRENCE }
     *     
     */
    public void setBUYDOWNOCCURRENCE(BUYDOWNOCCURRENCE value) {
        this.buydownoccurrence = value;
    }

}
