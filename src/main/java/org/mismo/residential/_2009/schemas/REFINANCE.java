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
 * <p>Java class for REFINANCE complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="REFINANCE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RefinanceSameLenderIndicator" type="{http://www.mismo.org/residential/2009/schemas}MISMOIndicator" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "REFINANCE", propOrder = {
    "refinanceSameLenderIndicator"
})
public class REFINANCE {

    @XmlElement(name = "RefinanceSameLenderIndicator")
    protected MISMOIndicator refinanceSameLenderIndicator;

    /**
     * Gets the value of the refinanceSameLenderIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link MISMOIndicator }
     *     
     */
    public MISMOIndicator getRefinanceSameLenderIndicator() {
        return refinanceSameLenderIndicator;
    }

    /**
     * Sets the value of the refinanceSameLenderIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link MISMOIndicator }
     *     
     */
    public void setRefinanceSameLenderIndicator(MISMOIndicator value) {
        this.refinanceSameLenderIndicator = value;
    }

}