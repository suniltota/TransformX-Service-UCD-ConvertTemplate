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
 * Defines the calculation of late charges that apply when a borrower does not make timely payments.
 * 
 * <p>Java class for LATE_CHARGE complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LATE_CHARGE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="EXTENSION" type="{http://www.mismo.org/residential/2009/schemas}LATE_CHARGE_EXTENSION"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LATE_CHARGE", propOrder = {
    "extension"
})
public class LATECHARGE {

    @XmlElement(name = "EXTENSION", required = true)
    protected LATECHARGEEXTENSION extension;

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link LATECHARGEEXTENSION }
     *     
     */
    public LATECHARGEEXTENSION getEXTENSION() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link LATECHARGEEXTENSION }
     *     
     */
    public void setEXTENSION(LATECHARGEEXTENSION value) {
        this.extension = value;
    }

}