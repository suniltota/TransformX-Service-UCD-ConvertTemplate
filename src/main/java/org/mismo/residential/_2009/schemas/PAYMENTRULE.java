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
 * <p>Java class for PAYMENT_RULE complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PAYMENT_RULE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FullyIndexedInitialPrincipalAndInterestPaymentAmount" type="{http://www.mismo.org/residential/2009/schemas}MISMOAmount" minOccurs="0"/>
 *         &lt;element name="InitialPrincipalAndInterestPaymentAmount" type="{http://www.mismo.org/residential/2009/schemas}MISMOAmount" minOccurs="0"/>
 *         &lt;element name="PartialPaymentAllowedIndicator" type="{http://www.mismo.org/residential/2009/schemas}MISMOIndicator" minOccurs="0"/>
 *         &lt;element name="PaymentFrequencyType" type="{http://www.mismo.org/residential/2009/schemas}PaymentFrequencyEnum" minOccurs="0"/>
 *         &lt;element name="PaymentOptionIndicator" type="{http://www.mismo.org/residential/2009/schemas}MISMOIndicator" minOccurs="0"/>
 *         &lt;element name="SeasonalPaymentPeriodEndMonth" type="{http://www.mismo.org/residential/2009/schemas}MISMOMonth" minOccurs="0"/>
 *         &lt;element name="SeasonalPaymentPeriodStartMonth" type="{http://www.mismo.org/residential/2009/schemas}MISMOMonth" minOccurs="0"/>
 *         &lt;element name="EXTENSION" type="{http://www.mismo.org/residential/2009/schemas}PAYMENT_RULE_EXTENSION" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PAYMENT_RULE", propOrder = {
    "fullyIndexedInitialPrincipalAndInterestPaymentAmount",
    "initialPrincipalAndInterestPaymentAmount",
    "partialPaymentAllowedIndicator",
    "paymentFrequencyType",
    "paymentOptionIndicator",
    "seasonalPaymentPeriodEndMonth",
    "seasonalPaymentPeriodStartMonth",
    "extension"
})
public class PAYMENTRULE {

    @XmlElement(name = "FullyIndexedInitialPrincipalAndInterestPaymentAmount")
    protected MISMOAmount fullyIndexedInitialPrincipalAndInterestPaymentAmount;
    @XmlElement(name = "InitialPrincipalAndInterestPaymentAmount")
    protected MISMOAmount initialPrincipalAndInterestPaymentAmount;
    @XmlElement(name = "PartialPaymentAllowedIndicator")
    protected MISMOIndicator partialPaymentAllowedIndicator;
    @XmlElement(name = "PaymentFrequencyType")
    protected PaymentFrequencyEnum paymentFrequencyType;
    @XmlElement(name = "PaymentOptionIndicator")
    protected MISMOIndicator paymentOptionIndicator;
    @XmlElement(name = "SeasonalPaymentPeriodEndMonth")
    protected MISMOMonth seasonalPaymentPeriodEndMonth;
    @XmlElement(name = "SeasonalPaymentPeriodStartMonth")
    protected MISMOMonth seasonalPaymentPeriodStartMonth;
    @XmlElement(name = "EXTENSION")
    protected PAYMENTRULEEXTENSION extension;

    /**
     * Gets the value of the fullyIndexedInitialPrincipalAndInterestPaymentAmount property.
     * 
     * @return
     *     possible object is
     *     {@link MISMOAmount }
     *     
     */
    public MISMOAmount getFullyIndexedInitialPrincipalAndInterestPaymentAmount() {
        return fullyIndexedInitialPrincipalAndInterestPaymentAmount;
    }

    /**
     * Sets the value of the fullyIndexedInitialPrincipalAndInterestPaymentAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link MISMOAmount }
     *     
     */
    public void setFullyIndexedInitialPrincipalAndInterestPaymentAmount(MISMOAmount value) {
        this.fullyIndexedInitialPrincipalAndInterestPaymentAmount = value;
    }

    /**
     * Gets the value of the initialPrincipalAndInterestPaymentAmount property.
     * 
     * @return
     *     possible object is
     *     {@link MISMOAmount }
     *     
     */
    public MISMOAmount getInitialPrincipalAndInterestPaymentAmount() {
        return initialPrincipalAndInterestPaymentAmount;
    }

    /**
     * Sets the value of the initialPrincipalAndInterestPaymentAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link MISMOAmount }
     *     
     */
    public void setInitialPrincipalAndInterestPaymentAmount(MISMOAmount value) {
        this.initialPrincipalAndInterestPaymentAmount = value;
    }

    /**
     * Gets the value of the partialPaymentAllowedIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link MISMOIndicator }
     *     
     */
    public MISMOIndicator getPartialPaymentAllowedIndicator() {
        return partialPaymentAllowedIndicator;
    }

    /**
     * Sets the value of the partialPaymentAllowedIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link MISMOIndicator }
     *     
     */
    public void setPartialPaymentAllowedIndicator(MISMOIndicator value) {
        this.partialPaymentAllowedIndicator = value;
    }

    /**
     * Gets the value of the paymentFrequencyType property.
     * 
     * @return
     *     possible object is
     *     {@link PaymentFrequencyEnum }
     *     
     */
    public PaymentFrequencyEnum getPaymentFrequencyType() {
        return paymentFrequencyType;
    }

    /**
     * Sets the value of the paymentFrequencyType property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentFrequencyEnum }
     *     
     */
    public void setPaymentFrequencyType(PaymentFrequencyEnum value) {
        this.paymentFrequencyType = value;
    }

    /**
     * Gets the value of the paymentOptionIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link MISMOIndicator }
     *     
     */
    public MISMOIndicator getPaymentOptionIndicator() {
        return paymentOptionIndicator;
    }

    /**
     * Sets the value of the paymentOptionIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link MISMOIndicator }
     *     
     */
    public void setPaymentOptionIndicator(MISMOIndicator value) {
        this.paymentOptionIndicator = value;
    }

    /**
     * Gets the value of the seasonalPaymentPeriodEndMonth property.
     * 
     * @return
     *     possible object is
     *     {@link MISMOMonth }
     *     
     */
    public MISMOMonth getSeasonalPaymentPeriodEndMonth() {
        return seasonalPaymentPeriodEndMonth;
    }

    /**
     * Sets the value of the seasonalPaymentPeriodEndMonth property.
     * 
     * @param value
     *     allowed object is
     *     {@link MISMOMonth }
     *     
     */
    public void setSeasonalPaymentPeriodEndMonth(MISMOMonth value) {
        this.seasonalPaymentPeriodEndMonth = value;
    }

    /**
     * Gets the value of the seasonalPaymentPeriodStartMonth property.
     * 
     * @return
     *     possible object is
     *     {@link MISMOMonth }
     *     
     */
    public MISMOMonth getSeasonalPaymentPeriodStartMonth() {
        return seasonalPaymentPeriodStartMonth;
    }

    /**
     * Sets the value of the seasonalPaymentPeriodStartMonth property.
     * 
     * @param value
     *     allowed object is
     *     {@link MISMOMonth }
     *     
     */
    public void setSeasonalPaymentPeriodStartMonth(MISMOMonth value) {
        this.seasonalPaymentPeriodStartMonth = value;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link PAYMENTRULEEXTENSION }
     *     
     */
    public PAYMENTRULEEXTENSION getEXTENSION() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link PAYMENTRULEEXTENSION }
     *     
     */
    public void setEXTENSION(PAYMENTRULEEXTENSION value) {
        this.extension = value;
    }

}
