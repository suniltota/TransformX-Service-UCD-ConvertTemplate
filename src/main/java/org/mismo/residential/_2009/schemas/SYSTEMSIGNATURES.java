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
 * <p>Java class for SYSTEM_SIGNATURES complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SYSTEM_SIGNATURES">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SYSTEM_SIGNATURE" type="{http://www.mismo.org/residential/2009/schemas}SYSTEM_SIGNATURE" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SYSTEM_SIGNATURES", propOrder = {
    "systemsignature"
})
public class SYSTEMSIGNATURES {

    @XmlElement(name = "SYSTEM_SIGNATURE")
    protected SYSTEMSIGNATURE systemsignature;

    /**
     * Gets the value of the systemsignature property.
     * 
     * @return
     *     possible object is
     *     {@link SYSTEMSIGNATURE }
     *     
     */
    public SYSTEMSIGNATURE getSYSTEMSIGNATURE() {
        return systemsignature;
    }

    /**
     * Sets the value of the systemsignature property.
     * 
     * @param value
     *     allowed object is
     *     {@link SYSTEMSIGNATURE }
     *     
     */
    public void setSYSTEMSIGNATURE(SYSTEMSIGNATURE value) {
        this.systemsignature = value;
    }

}
