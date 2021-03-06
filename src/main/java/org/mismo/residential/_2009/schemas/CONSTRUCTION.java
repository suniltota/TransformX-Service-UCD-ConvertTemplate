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
 * <p>Java class for CONSTRUCTION complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CONSTRUCTION">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ConstructionLoanTotalTermMonthsCount" type="{http://www.mismo.org/residential/2009/schemas}MISMOCount" minOccurs="0"/>
 *         &lt;element name="ConstructionLoanType" type="{http://www.mismo.org/residential/2009/schemas}ConstructionLoanEnum" minOccurs="0"/>
 *         &lt;element name="ConstructionPeriodNumberOfMonthsCount" type="{http://www.mismo.org/residential/2009/schemas}MISMOCount" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CONSTRUCTION", propOrder = {
    "constructionLoanTotalTermMonthsCount",
    "constructionLoanType",
    "constructionPeriodNumberOfMonthsCount"
})
public class CONSTRUCTION {

    @XmlElement(name = "ConstructionLoanTotalTermMonthsCount")
    protected MISMOCount constructionLoanTotalTermMonthsCount;
    @XmlElement(name = "ConstructionLoanType")
    protected ConstructionLoanEnum constructionLoanType;
    @XmlElement(name = "ConstructionPeriodNumberOfMonthsCount")
    protected MISMOCount constructionPeriodNumberOfMonthsCount;

    /**
     * Gets the value of the constructionLoanTotalTermMonthsCount property.
     * 
     * @return
     *     possible object is
     *     {@link MISMOCount }
     *     
     */
    public MISMOCount getConstructionLoanTotalTermMonthsCount() {
        return constructionLoanTotalTermMonthsCount;
    }

    /**
     * Sets the value of the constructionLoanTotalTermMonthsCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link MISMOCount }
     *     
     */
    public void setConstructionLoanTotalTermMonthsCount(MISMOCount value) {
        this.constructionLoanTotalTermMonthsCount = value;
    }

    /**
     * Gets the value of the constructionLoanType property.
     * 
     * @return
     *     possible object is
     *     {@link ConstructionLoanEnum }
     *     
     */
    public ConstructionLoanEnum getConstructionLoanType() {
        return constructionLoanType;
    }

    /**
     * Sets the value of the constructionLoanType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ConstructionLoanEnum }
     *     
     */
    public void setConstructionLoanType(ConstructionLoanEnum value) {
        this.constructionLoanType = value;
    }

    /**
     * Gets the value of the constructionPeriodNumberOfMonthsCount property.
     * 
     * @return
     *     possible object is
     *     {@link MISMOCount }
     *     
     */
    public MISMOCount getConstructionPeriodNumberOfMonthsCount() {
        return constructionPeriodNumberOfMonthsCount;
    }

    /**
     * Sets the value of the constructionPeriodNumberOfMonthsCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link MISMOCount }
     *     
     */
    public void setConstructionPeriodNumberOfMonthsCount(MISMOCount value) {
        this.constructionPeriodNumberOfMonthsCount = value;
    }

}
