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
 * <p>Java class for PREPAYMENT_PENALTY_LIFETIME_RULE complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PREPAYMENT_PENALTY_LIFETIME_RULE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PrepaymentPenaltyExpirationMonthsCount" type="{http://www.mismo.org/residential/2009/schemas}MISMOCount" minOccurs="0"/>
 *         &lt;element name="PrepaymentPenaltyMaximumLifeOfLoanAmount" type="{http://www.mismo.org/residential/2009/schemas}MISMOAmount" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PREPAYMENT_PENALTY_LIFETIME_RULE", propOrder = {
    "prepaymentPenaltyExpirationMonthsCount",
    "prepaymentPenaltyMaximumLifeOfLoanAmount"
})
public class PREPAYMENTPENALTYLIFETIMERULE {

    @XmlElement(name = "PrepaymentPenaltyExpirationMonthsCount")
    protected MISMOCount prepaymentPenaltyExpirationMonthsCount;
    @XmlElement(name = "PrepaymentPenaltyMaximumLifeOfLoanAmount")
    protected MISMOAmount prepaymentPenaltyMaximumLifeOfLoanAmount;

    /**
     * Gets the value of the prepaymentPenaltyExpirationMonthsCount property.
     * 
     * @return
     *     possible object is
     *     {@link MISMOCount }
     *     
     */
    public MISMOCount getPrepaymentPenaltyExpirationMonthsCount() {
        return prepaymentPenaltyExpirationMonthsCount;
    }

    /**
     * Sets the value of the prepaymentPenaltyExpirationMonthsCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link MISMOCount }
     *     
     */
    public void setPrepaymentPenaltyExpirationMonthsCount(MISMOCount value) {
        this.prepaymentPenaltyExpirationMonthsCount = value;
    }

    /**
     * Gets the value of the prepaymentPenaltyMaximumLifeOfLoanAmount property.
     * 
     * @return
     *     possible object is
     *     {@link MISMOAmount }
     *     
     */
    public MISMOAmount getPrepaymentPenaltyMaximumLifeOfLoanAmount() {
        return prepaymentPenaltyMaximumLifeOfLoanAmount;
    }

    /**
     * Sets the value of the prepaymentPenaltyMaximumLifeOfLoanAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link MISMOAmount }
     *     
     */
    public void setPrepaymentPenaltyMaximumLifeOfLoanAmount(MISMOAmount value) {
        this.prepaymentPenaltyMaximumLifeOfLoanAmount = value;
    }

}
