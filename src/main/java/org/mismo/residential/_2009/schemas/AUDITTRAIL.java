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
 * <p>Java class for AUDIT_TRAIL complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AUDIT_TRAIL">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AUDIT_TRAIL_ENTRIES" type="{http://www.mismo.org/residential/2009/schemas}AUDIT_TRAIL_ENTRIES" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AUDIT_TRAIL", propOrder = {
    "audittrailentries"
})
public class AUDITTRAIL {

    @XmlElement(name = "AUDIT_TRAIL_ENTRIES")
    protected AUDITTRAILENTRIES audittrailentries;

    /**
     * Gets the value of the audittrailentries property.
     * 
     * @return
     *     possible object is
     *     {@link AUDITTRAILENTRIES }
     *     
     */
    public AUDITTRAILENTRIES getAUDITTRAILENTRIES() {
        return audittrailentries;
    }

    /**
     * Sets the value of the audittrailentries property.
     * 
     * @param value
     *     allowed object is
     *     {@link AUDITTRAILENTRIES }
     *     
     */
    public void setAUDITTRAILENTRIES(AUDITTRAILENTRIES value) {
        this.audittrailentries = value;
    }

}