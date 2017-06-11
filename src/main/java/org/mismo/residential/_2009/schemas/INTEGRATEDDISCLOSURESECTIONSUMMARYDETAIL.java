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
 * Additional information about the integrated disclosure summary.
 * 
 * <p>Java class for INTEGRATED_DISCLOSURE_SECTION_SUMMARY_DETAIL complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="INTEGRATED_DISCLOSURE_SECTION_SUMMARY_DETAIL">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="IntegratedDisclosureSectionTotalAmount" type="{http://www.mismo.org/residential/2009/schemas}MISMOAmount" minOccurs="0"/>
 *         &lt;element name="IntegratedDisclosureSectionType" type="{http://www.mismo.org/residential/2009/schemas}IntegratedDisclosureSectionEnum" minOccurs="0"/>
 *         &lt;element name="IntegratedDisclosureSubsectionType" type="{http://www.mismo.org/residential/2009/schemas}IntegratedDisclosureSubsectionEnum" minOccurs="0"/>
 *         &lt;element name="IntegratedDisclosureSubsectionTypeOtherDescription" type="{http://www.mismo.org/residential/2009/schemas}MISMOString" minOccurs="0"/>
 *         &lt;element name="LenderCreditToleranceCureAmount" type="{http://www.mismo.org/residential/2009/schemas}MISMOAmount" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "INTEGRATED_DISCLOSURE_SECTION_SUMMARY_DETAIL", propOrder = {
    "integratedDisclosureSectionTotalAmount",
    "integratedDisclosureSectionType",
    "integratedDisclosureSubsectionType",
    "integratedDisclosureSubsectionTypeOtherDescription",
    "lenderCreditToleranceCureAmount"
})
public class INTEGRATEDDISCLOSURESECTIONSUMMARYDETAIL {

    @XmlElement(name = "IntegratedDisclosureSectionTotalAmount")
    protected MISMOAmount integratedDisclosureSectionTotalAmount;
    @XmlElement(name = "IntegratedDisclosureSectionType")
    protected IntegratedDisclosureSectionEnum integratedDisclosureSectionType;
    @XmlElement(name = "IntegratedDisclosureSubsectionType")
    protected IntegratedDisclosureSubsectionEnum integratedDisclosureSubsectionType;
    @XmlElement(name = "IntegratedDisclosureSubsectionTypeOtherDescription")
    protected MISMOString integratedDisclosureSubsectionTypeOtherDescription;
    @XmlElement(name = "LenderCreditToleranceCureAmount")
    protected MISMOAmount lenderCreditToleranceCureAmount;

    /**
     * Gets the value of the integratedDisclosureSectionTotalAmount property.
     * 
     * @return
     *     possible object is
     *     {@link MISMOAmount }
     *     
     */
    public MISMOAmount getIntegratedDisclosureSectionTotalAmount() {
        return integratedDisclosureSectionTotalAmount;
    }

    /**
     * Sets the value of the integratedDisclosureSectionTotalAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link MISMOAmount }
     *     
     */
    public void setIntegratedDisclosureSectionTotalAmount(MISMOAmount value) {
        this.integratedDisclosureSectionTotalAmount = value;
    }

    /**
     * Gets the value of the integratedDisclosureSectionType property.
     * 
     * @return
     *     possible object is
     *     {@link IntegratedDisclosureSectionEnum }
     *     
     */
    public IntegratedDisclosureSectionEnum getIntegratedDisclosureSectionType() {
        return integratedDisclosureSectionType;
    }

    /**
     * Sets the value of the integratedDisclosureSectionType property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntegratedDisclosureSectionEnum }
     *     
     */
    public void setIntegratedDisclosureSectionType(IntegratedDisclosureSectionEnum value) {
        this.integratedDisclosureSectionType = value;
    }

    /**
     * Gets the value of the integratedDisclosureSubsectionType property.
     * 
     * @return
     *     possible object is
     *     {@link IntegratedDisclosureSubsectionEnum }
     *     
     */
    public IntegratedDisclosureSubsectionEnum getIntegratedDisclosureSubsectionType() {
        return integratedDisclosureSubsectionType;
    }

    /**
     * Sets the value of the integratedDisclosureSubsectionType property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntegratedDisclosureSubsectionEnum }
     *     
     */
    public void setIntegratedDisclosureSubsectionType(IntegratedDisclosureSubsectionEnum value) {
        this.integratedDisclosureSubsectionType = value;
    }

    /**
     * Gets the value of the integratedDisclosureSubsectionTypeOtherDescription property.
     * 
     * @return
     *     possible object is
     *     {@link MISMOString }
     *     
     */
    public MISMOString getIntegratedDisclosureSubsectionTypeOtherDescription() {
        return integratedDisclosureSubsectionTypeOtherDescription;
    }

    /**
     * Sets the value of the integratedDisclosureSubsectionTypeOtherDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link MISMOString }
     *     
     */
    public void setIntegratedDisclosureSubsectionTypeOtherDescription(MISMOString value) {
        this.integratedDisclosureSubsectionTypeOtherDescription = value;
    }

    /**
     * Gets the value of the lenderCreditToleranceCureAmount property.
     * 
     * @return
     *     possible object is
     *     {@link MISMOAmount }
     *     
     */
    public MISMOAmount getLenderCreditToleranceCureAmount() {
        return lenderCreditToleranceCureAmount;
    }

    /**
     * Sets the value of the lenderCreditToleranceCureAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link MISMOAmount }
     *     
     */
    public void setLenderCreditToleranceCureAmount(MISMOAmount value) {
        this.lenderCreditToleranceCureAmount = value;
    }

}