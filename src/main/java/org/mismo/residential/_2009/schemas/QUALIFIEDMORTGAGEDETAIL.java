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
 * Additional information about the Qualified Mortgage loan eligibility and status.
 * 
 * <p>Java class for QUALIFIED_MORTGAGE_DETAIL complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="QUALIFIED_MORTGAGE_DETAIL">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AbilityToRepayMethodType" type="{http://www.mismo.org/residential/2009/schemas}AbilityToRepayMethodEnum" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QUALIFIED_MORTGAGE_DETAIL", propOrder = {
    "abilityToRepayMethodType"
})
public class QUALIFIEDMORTGAGEDETAIL {

    @XmlElement(name = "AbilityToRepayMethodType")
    protected AbilityToRepayMethodEnum abilityToRepayMethodType;

    /**
     * Gets the value of the abilityToRepayMethodType property.
     * 
     * @return
     *     possible object is
     *     {@link AbilityToRepayMethodEnum }
     *     
     */
    public AbilityToRepayMethodEnum getAbilityToRepayMethodType() {
        return abilityToRepayMethodType;
    }

    /**
     * Sets the value of the abilityToRepayMethodType property.
     * 
     * @param value
     *     allowed object is
     *     {@link AbilityToRepayMethodEnum }
     *     
     */
    public void setAbilityToRepayMethodType(AbilityToRepayMethodEnum value) {
        this.abilityToRepayMethodType = value;
    }

}
