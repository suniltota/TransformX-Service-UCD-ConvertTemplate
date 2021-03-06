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
 * Information about one or more items disclosed in the Calculating Cash to Close section of the integrated disclosure document. 
 * 
 * <p>Java class for CASH_TO_CLOSE_ITEM complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CASH_TO_CLOSE_ITEM">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="IntegratedDisclosureCashToCloseItemAmountChangedIndicator" type="{http://www.mismo.org/residential/2009/schemas}MISMOIndicator" minOccurs="0"/>
 *         &lt;element name="IntegratedDisclosureCashToCloseItemChangeDescription" type="{http://www.mismo.org/residential/2009/schemas}MISMOString" minOccurs="0"/>
 *         &lt;element name="IntegratedDisclosureCashToCloseItemEstimatedAmount" type="{http://www.mismo.org/residential/2009/schemas}MISMOAmount" minOccurs="0"/>
 *         &lt;element name="IntegratedDisclosureCashToCloseItemFinalAmount" type="{http://www.mismo.org/residential/2009/schemas}MISMOAmount" minOccurs="0"/>
 *         &lt;element name="IntegratedDisclosureCashToCloseItemPaymentType" type="{http://www.mismo.org/residential/2009/schemas}IntegratedDisclosureCashToCloseItemPaymentEnum" minOccurs="0"/>
 *         &lt;element name="IntegratedDisclosureCashToCloseItemType" type="{http://www.mismo.org/residential/2009/schemas}IntegratedDisclosureCashToCloseItemEnum" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CASH_TO_CLOSE_ITEM", propOrder = {
    "integratedDisclosureCashToCloseItemAmountChangedIndicator",
    "integratedDisclosureCashToCloseItemChangeDescription",
    "integratedDisclosureCashToCloseItemEstimatedAmount",
    "integratedDisclosureCashToCloseItemFinalAmount",
    "integratedDisclosureCashToCloseItemPaymentType",
    "integratedDisclosureCashToCloseItemType"
})
public class CASHTOCLOSEITEM {

    @XmlElement(name = "IntegratedDisclosureCashToCloseItemAmountChangedIndicator")
    protected MISMOIndicator integratedDisclosureCashToCloseItemAmountChangedIndicator;
    @XmlElement(name = "IntegratedDisclosureCashToCloseItemChangeDescription")
    protected MISMOString integratedDisclosureCashToCloseItemChangeDescription;
    @XmlElement(name = "IntegratedDisclosureCashToCloseItemEstimatedAmount")
    protected MISMOAmount integratedDisclosureCashToCloseItemEstimatedAmount;
    @XmlElement(name = "IntegratedDisclosureCashToCloseItemFinalAmount")
    protected MISMOAmount integratedDisclosureCashToCloseItemFinalAmount;
    @XmlElement(name = "IntegratedDisclosureCashToCloseItemPaymentType")
    protected IntegratedDisclosureCashToCloseItemPaymentEnum integratedDisclosureCashToCloseItemPaymentType;
    @XmlElement(name = "IntegratedDisclosureCashToCloseItemType")
    protected IntegratedDisclosureCashToCloseItemEnum integratedDisclosureCashToCloseItemType;

    /**
     * Gets the value of the integratedDisclosureCashToCloseItemAmountChangedIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link MISMOIndicator }
     *     
     */
    public MISMOIndicator getIntegratedDisclosureCashToCloseItemAmountChangedIndicator() {
        return integratedDisclosureCashToCloseItemAmountChangedIndicator;
    }

    /**
     * Sets the value of the integratedDisclosureCashToCloseItemAmountChangedIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link MISMOIndicator }
     *     
     */
    public void setIntegratedDisclosureCashToCloseItemAmountChangedIndicator(MISMOIndicator value) {
        this.integratedDisclosureCashToCloseItemAmountChangedIndicator = value;
    }

    /**
     * Gets the value of the integratedDisclosureCashToCloseItemChangeDescription property.
     * 
     * @return
     *     possible object is
     *     {@link MISMOString }
     *     
     */
    public MISMOString getIntegratedDisclosureCashToCloseItemChangeDescription() {
        return integratedDisclosureCashToCloseItemChangeDescription;
    }

    /**
     * Sets the value of the integratedDisclosureCashToCloseItemChangeDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link MISMOString }
     *     
     */
    public void setIntegratedDisclosureCashToCloseItemChangeDescription(MISMOString value) {
        this.integratedDisclosureCashToCloseItemChangeDescription = value;
    }

    /**
     * Gets the value of the integratedDisclosureCashToCloseItemEstimatedAmount property.
     * 
     * @return
     *     possible object is
     *     {@link MISMOAmount }
     *     
     */
    public MISMOAmount getIntegratedDisclosureCashToCloseItemEstimatedAmount() {
        return integratedDisclosureCashToCloseItemEstimatedAmount;
    }

    /**
     * Sets the value of the integratedDisclosureCashToCloseItemEstimatedAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link MISMOAmount }
     *     
     */
    public void setIntegratedDisclosureCashToCloseItemEstimatedAmount(MISMOAmount value) {
        this.integratedDisclosureCashToCloseItemEstimatedAmount = value;
    }

    /**
     * Gets the value of the integratedDisclosureCashToCloseItemFinalAmount property.
     * 
     * @return
     *     possible object is
     *     {@link MISMOAmount }
     *     
     */
    public MISMOAmount getIntegratedDisclosureCashToCloseItemFinalAmount() {
        return integratedDisclosureCashToCloseItemFinalAmount;
    }

    /**
     * Sets the value of the integratedDisclosureCashToCloseItemFinalAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link MISMOAmount }
     *     
     */
    public void setIntegratedDisclosureCashToCloseItemFinalAmount(MISMOAmount value) {
        this.integratedDisclosureCashToCloseItemFinalAmount = value;
    }

    /**
     * Gets the value of the integratedDisclosureCashToCloseItemPaymentType property.
     * 
     * @return
     *     possible object is
     *     {@link IntegratedDisclosureCashToCloseItemPaymentEnum }
     *     
     */
    public IntegratedDisclosureCashToCloseItemPaymentEnum getIntegratedDisclosureCashToCloseItemPaymentType() {
        return integratedDisclosureCashToCloseItemPaymentType;
    }

    /**
     * Sets the value of the integratedDisclosureCashToCloseItemPaymentType property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntegratedDisclosureCashToCloseItemPaymentEnum }
     *     
     */
    public void setIntegratedDisclosureCashToCloseItemPaymentType(IntegratedDisclosureCashToCloseItemPaymentEnum value) {
        this.integratedDisclosureCashToCloseItemPaymentType = value;
    }

    /**
     * Gets the value of the integratedDisclosureCashToCloseItemType property.
     * 
     * @return
     *     possible object is
     *     {@link IntegratedDisclosureCashToCloseItemEnum }
     *     
     */
    public IntegratedDisclosureCashToCloseItemEnum getIntegratedDisclosureCashToCloseItemType() {
        return integratedDisclosureCashToCloseItemType;
    }

    /**
     * Sets the value of the integratedDisclosureCashToCloseItemType property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntegratedDisclosureCashToCloseItemEnum }
     *     
     */
    public void setIntegratedDisclosureCashToCloseItemType(IntegratedDisclosureCashToCloseItemEnum value) {
        this.integratedDisclosureCashToCloseItemType = value;
    }

}
