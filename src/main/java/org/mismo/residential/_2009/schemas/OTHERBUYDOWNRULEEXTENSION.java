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
 * <p>Java class for OTHER_BUYDOWN_RULE_EXTENSION complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OTHER_BUYDOWN_RULE_EXTENSION">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.datamodelextension.org}BuydownReflectedInNoteIndicator" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OTHER_BUYDOWN_RULE_EXTENSION", propOrder = {
    "buydownReflectedInNoteIndicator"
})
public class OTHERBUYDOWNRULEEXTENSION {

    @XmlElement(name = "BuydownReflectedInNoteIndicator", namespace = "http://www.datamodelextension.org")
    protected Boolean buydownReflectedInNoteIndicator;

    /**
     * Gets the value of the buydownReflectedInNoteIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isBuydownReflectedInNoteIndicator() {
        return buydownReflectedInNoteIndicator;
    }

    /**
     * Sets the value of the buydownReflectedInNoteIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setBuydownReflectedInNoteIndicator(Boolean value) {
        this.buydownReflectedInNoteIndicator = value;
    }

}