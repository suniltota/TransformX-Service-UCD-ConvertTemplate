//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.12.01 at 06:02:48 PM IST 
//


package org.mismo.residential._2009.schemas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Contains information about a projected loan payment or a component of a projected loan payment.
 * 
 * <p>Java class for PROJECTED_PAYMENT complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PROJECTED_PAYMENT">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PaymentFrequencyType" type="{http://www.mismo.org/residential/2009/schemas}PaymentFrequencyEnum" minOccurs="0"/>
 *         &lt;element name="ProjectedPaymentCalculationPeriodEndNumber" type="{http://www.mismo.org/residential/2009/schemas}MISMONumeric" minOccurs="0"/>
 *         &lt;element name="ProjectedPaymentCalculationPeriodStartNumber" type="{http://www.mismo.org/residential/2009/schemas}MISMONumeric" minOccurs="0"/>
 *         &lt;element name="ProjectedPaymentCalculationPeriodTermType" type="{http://www.mismo.org/residential/2009/schemas}ProjectedPaymentCalculationPeriodTermEnum" minOccurs="0"/>
 *         &lt;element name="ProjectedPaymentCalculationPeriodTermTypeOtherDescription" type="{http://www.mismo.org/residential/2009/schemas}MISMOString" minOccurs="0"/>
 *         &lt;element name="ProjectedPaymentEstimatedEscrowPaymentAmount" type="{http://www.mismo.org/residential/2009/schemas}MISMOAmount" minOccurs="0"/>
 *         &lt;element name="ProjectedPaymentEstimatedTotalMaximumPaymentAmount" type="{http://www.mismo.org/residential/2009/schemas}MISMOAmount" minOccurs="0"/>
 *         &lt;element name="ProjectedPaymentEstimatedTotalMinimumPaymentAmount" type="{http://www.mismo.org/residential/2009/schemas}MISMOAmount" minOccurs="0"/>
 *         &lt;element name="ProjectedPaymentMIPaymentAmount" type="{http://www.mismo.org/residential/2009/schemas}MISMOAmount" minOccurs="0"/>
 *         &lt;element name="ProjectedPaymentPrincipalAndInterestMaximumPaymentAmount" type="{http://www.mismo.org/residential/2009/schemas}MISMOAmount" minOccurs="0"/>
 *         &lt;element name="ProjectedPaymentPrincipalAndInterestMinimumPaymentAmount" type="{http://www.mismo.org/residential/2009/schemas}MISMOAmount" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="SequenceNumber" type="{http://www.mismo.org/residential/2009/schemas}MISMOSequenceNumber_Base" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PROJECTED_PAYMENT", propOrder = {
    "paymentFrequencyType",
    "projectedPaymentCalculationPeriodEndNumber",
    "projectedPaymentCalculationPeriodStartNumber",
    "projectedPaymentCalculationPeriodTermType",
    "projectedPaymentCalculationPeriodTermTypeOtherDescription",
    "projectedPaymentEstimatedEscrowPaymentAmount",
    "projectedPaymentEstimatedTotalMaximumPaymentAmount",
    "projectedPaymentEstimatedTotalMinimumPaymentAmount",
    "projectedPaymentMIPaymentAmount",
    "projectedPaymentPrincipalAndInterestMaximumPaymentAmount",
    "projectedPaymentPrincipalAndInterestMinimumPaymentAmount"
})
public class PROJECTEDPAYMENT {

    @XmlElement(name = "PaymentFrequencyType")
    protected PaymentFrequencyEnum paymentFrequencyType;
    @XmlElement(name = "ProjectedPaymentCalculationPeriodEndNumber")
    protected MISMONumeric projectedPaymentCalculationPeriodEndNumber;
    @XmlElement(name = "ProjectedPaymentCalculationPeriodStartNumber")
    protected MISMONumeric projectedPaymentCalculationPeriodStartNumber;
    @XmlElement(name = "ProjectedPaymentCalculationPeriodTermType")
    protected ProjectedPaymentCalculationPeriodTermEnum projectedPaymentCalculationPeriodTermType;
    @XmlElement(name = "ProjectedPaymentCalculationPeriodTermTypeOtherDescription")
    protected MISMOString projectedPaymentCalculationPeriodTermTypeOtherDescription;
    @XmlElement(name = "ProjectedPaymentEstimatedEscrowPaymentAmount")
    protected MISMOAmount projectedPaymentEstimatedEscrowPaymentAmount;
    @XmlElement(name = "ProjectedPaymentEstimatedTotalMaximumPaymentAmount")
    protected MISMOAmount projectedPaymentEstimatedTotalMaximumPaymentAmount;
    @XmlElement(name = "ProjectedPaymentEstimatedTotalMinimumPaymentAmount")
    protected MISMOAmount projectedPaymentEstimatedTotalMinimumPaymentAmount;
    @XmlElement(name = "ProjectedPaymentMIPaymentAmount")
    protected MISMOAmount projectedPaymentMIPaymentAmount;
    @XmlElement(name = "ProjectedPaymentPrincipalAndInterestMaximumPaymentAmount")
    protected MISMOAmount projectedPaymentPrincipalAndInterestMaximumPaymentAmount;
    @XmlElement(name = "ProjectedPaymentPrincipalAndInterestMinimumPaymentAmount")
    protected MISMOAmount projectedPaymentPrincipalAndInterestMinimumPaymentAmount;
    @XmlAttribute(name = "SequenceNumber")
    protected Integer sequenceNumber;

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
     * Gets the value of the projectedPaymentCalculationPeriodEndNumber property.
     * 
     * @return
     *     possible object is
     *     {@link MISMONumeric }
     *     
     */
    public MISMONumeric getProjectedPaymentCalculationPeriodEndNumber() {
        return projectedPaymentCalculationPeriodEndNumber;
    }

    /**
     * Sets the value of the projectedPaymentCalculationPeriodEndNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link MISMONumeric }
     *     
     */
    public void setProjectedPaymentCalculationPeriodEndNumber(MISMONumeric value) {
        this.projectedPaymentCalculationPeriodEndNumber = value;
    }

    /**
     * Gets the value of the projectedPaymentCalculationPeriodStartNumber property.
     * 
     * @return
     *     possible object is
     *     {@link MISMONumeric }
     *     
     */
    public MISMONumeric getProjectedPaymentCalculationPeriodStartNumber() {
        return projectedPaymentCalculationPeriodStartNumber;
    }

    /**
     * Sets the value of the projectedPaymentCalculationPeriodStartNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link MISMONumeric }
     *     
     */
    public void setProjectedPaymentCalculationPeriodStartNumber(MISMONumeric value) {
        this.projectedPaymentCalculationPeriodStartNumber = value;
    }

    /**
     * Gets the value of the projectedPaymentCalculationPeriodTermType property.
     * 
     * @return
     *     possible object is
     *     {@link ProjectedPaymentCalculationPeriodTermEnum }
     *     
     */
    public ProjectedPaymentCalculationPeriodTermEnum getProjectedPaymentCalculationPeriodTermType() {
        return projectedPaymentCalculationPeriodTermType;
    }

    /**
     * Sets the value of the projectedPaymentCalculationPeriodTermType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProjectedPaymentCalculationPeriodTermEnum }
     *     
     */
    public void setProjectedPaymentCalculationPeriodTermType(ProjectedPaymentCalculationPeriodTermEnum value) {
        this.projectedPaymentCalculationPeriodTermType = value;
    }

    /**
     * Gets the value of the projectedPaymentCalculationPeriodTermTypeOtherDescription property.
     * 
     * @return
     *     possible object is
     *     {@link MISMOString }
     *     
     */
    public MISMOString getProjectedPaymentCalculationPeriodTermTypeOtherDescription() {
        return projectedPaymentCalculationPeriodTermTypeOtherDescription;
    }

    /**
     * Sets the value of the projectedPaymentCalculationPeriodTermTypeOtherDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link MISMOString }
     *     
     */
    public void setProjectedPaymentCalculationPeriodTermTypeOtherDescription(MISMOString value) {
        this.projectedPaymentCalculationPeriodTermTypeOtherDescription = value;
    }

    /**
     * Gets the value of the projectedPaymentEstimatedEscrowPaymentAmount property.
     * 
     * @return
     *     possible object is
     *     {@link MISMOAmount }
     *     
     */
    public MISMOAmount getProjectedPaymentEstimatedEscrowPaymentAmount() {
        return projectedPaymentEstimatedEscrowPaymentAmount;
    }

    /**
     * Sets the value of the projectedPaymentEstimatedEscrowPaymentAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link MISMOAmount }
     *     
     */
    public void setProjectedPaymentEstimatedEscrowPaymentAmount(MISMOAmount value) {
        this.projectedPaymentEstimatedEscrowPaymentAmount = value;
    }

    /**
     * Gets the value of the projectedPaymentEstimatedTotalMaximumPaymentAmount property.
     * 
     * @return
     *     possible object is
     *     {@link MISMOAmount }
     *     
     */
    public MISMOAmount getProjectedPaymentEstimatedTotalMaximumPaymentAmount() {
        return projectedPaymentEstimatedTotalMaximumPaymentAmount;
    }

    /**
     * Sets the value of the projectedPaymentEstimatedTotalMaximumPaymentAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link MISMOAmount }
     *     
     */
    public void setProjectedPaymentEstimatedTotalMaximumPaymentAmount(MISMOAmount value) {
        this.projectedPaymentEstimatedTotalMaximumPaymentAmount = value;
    }

    /**
     * Gets the value of the projectedPaymentEstimatedTotalMinimumPaymentAmount property.
     * 
     * @return
     *     possible object is
     *     {@link MISMOAmount }
     *     
     */
    public MISMOAmount getProjectedPaymentEstimatedTotalMinimumPaymentAmount() {
        return projectedPaymentEstimatedTotalMinimumPaymentAmount;
    }

    /**
     * Sets the value of the projectedPaymentEstimatedTotalMinimumPaymentAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link MISMOAmount }
     *     
     */
    public void setProjectedPaymentEstimatedTotalMinimumPaymentAmount(MISMOAmount value) {
        this.projectedPaymentEstimatedTotalMinimumPaymentAmount = value;
    }

    /**
     * Gets the value of the projectedPaymentMIPaymentAmount property.
     * 
     * @return
     *     possible object is
     *     {@link MISMOAmount }
     *     
     */
    public MISMOAmount getProjectedPaymentMIPaymentAmount() {
        return projectedPaymentMIPaymentAmount;
    }

    /**
     * Sets the value of the projectedPaymentMIPaymentAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link MISMOAmount }
     *     
     */
    public void setProjectedPaymentMIPaymentAmount(MISMOAmount value) {
        this.projectedPaymentMIPaymentAmount = value;
    }

    /**
     * Gets the value of the projectedPaymentPrincipalAndInterestMaximumPaymentAmount property.
     * 
     * @return
     *     possible object is
     *     {@link MISMOAmount }
     *     
     */
    public MISMOAmount getProjectedPaymentPrincipalAndInterestMaximumPaymentAmount() {
        return projectedPaymentPrincipalAndInterestMaximumPaymentAmount;
    }

    /**
     * Sets the value of the projectedPaymentPrincipalAndInterestMaximumPaymentAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link MISMOAmount }
     *     
     */
    public void setProjectedPaymentPrincipalAndInterestMaximumPaymentAmount(MISMOAmount value) {
        this.projectedPaymentPrincipalAndInterestMaximumPaymentAmount = value;
    }

    /**
     * Gets the value of the projectedPaymentPrincipalAndInterestMinimumPaymentAmount property.
     * 
     * @return
     *     possible object is
     *     {@link MISMOAmount }
     *     
     */
    public MISMOAmount getProjectedPaymentPrincipalAndInterestMinimumPaymentAmount() {
        return projectedPaymentPrincipalAndInterestMinimumPaymentAmount;
    }

    /**
     * Sets the value of the projectedPaymentPrincipalAndInterestMinimumPaymentAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link MISMOAmount }
     *     
     */
    public void setProjectedPaymentPrincipalAndInterestMinimumPaymentAmount(MISMOAmount value) {
        this.projectedPaymentPrincipalAndInterestMinimumPaymentAmount = value;
    }

    /**
     * Gets the value of the sequenceNumber property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * Sets the value of the sequenceNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSequenceNumber(Integer value) {
        this.sequenceNumber = value;
    }

}
