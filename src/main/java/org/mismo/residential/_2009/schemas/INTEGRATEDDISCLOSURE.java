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
 * Contains information specific to the integrated disclosure documents.
 * 
 * <p>Java class for INTEGRATED_DISCLOSURE complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="INTEGRATED_DISCLOSURE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CASH_TO_CLOSE_ITEMS" type="{http://www.mismo.org/residential/2009/schemas}CASH_TO_CLOSE_ITEMS" minOccurs="0"/>
 *         &lt;element name="ESTIMATED_PROPERTY_COST" type="{http://www.mismo.org/residential/2009/schemas}ESTIMATED_PROPERTY_COST" minOccurs="0"/>
 *         &lt;element name="INTEGRATED_DISCLOSURE_DETAIL" type="{http://www.mismo.org/residential/2009/schemas}INTEGRATED_DISCLOSURE_DETAIL"/>
 *         &lt;element name="INTEGRATED_DISCLOSURE_SECTION_SUMMARIES" type="{http://www.mismo.org/residential/2009/schemas}INTEGRATED_DISCLOSURE_SECTION_SUMMARIES"/>
 *         &lt;element name="PROJECTED_PAYMENTS" type="{http://www.mismo.org/residential/2009/schemas}PROJECTED_PAYMENTS" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "INTEGRATED_DISCLOSURE", propOrder = {
    "cashtocloseitems",
    "estimatedpropertycost",
    "integrateddisclosuredetail",
    "integrateddisclosuresectionsummaries",
    "projectedpayments"
})
public class INTEGRATEDDISCLOSURE {

    @XmlElement(name = "CASH_TO_CLOSE_ITEMS")
    protected CASHTOCLOSEITEMS cashtocloseitems;
    @XmlElement(name = "ESTIMATED_PROPERTY_COST")
    protected ESTIMATEDPROPERTYCOST estimatedpropertycost;
    @XmlElement(name = "INTEGRATED_DISCLOSURE_DETAIL", required = true)
    protected INTEGRATEDDISCLOSUREDETAIL integrateddisclosuredetail;
    @XmlElement(name = "INTEGRATED_DISCLOSURE_SECTION_SUMMARIES", required = true)
    protected INTEGRATEDDISCLOSURESECTIONSUMMARIES integrateddisclosuresectionsummaries;
    @XmlElement(name = "PROJECTED_PAYMENTS")
    protected PROJECTEDPAYMENTS projectedpayments;

    /**
     * Gets the value of the cashtocloseitems property.
     * 
     * @return
     *     possible object is
     *     {@link CASHTOCLOSEITEMS }
     *     
     */
    public CASHTOCLOSEITEMS getCASHTOCLOSEITEMS() {
        return cashtocloseitems;
    }

    /**
     * Sets the value of the cashtocloseitems property.
     * 
     * @param value
     *     allowed object is
     *     {@link CASHTOCLOSEITEMS }
     *     
     */
    public void setCASHTOCLOSEITEMS(CASHTOCLOSEITEMS value) {
        this.cashtocloseitems = value;
    }

    /**
     * Gets the value of the estimatedpropertycost property.
     * 
     * @return
     *     possible object is
     *     {@link ESTIMATEDPROPERTYCOST }
     *     
     */
    public ESTIMATEDPROPERTYCOST getESTIMATEDPROPERTYCOST() {
        return estimatedpropertycost;
    }

    /**
     * Sets the value of the estimatedpropertycost property.
     * 
     * @param value
     *     allowed object is
     *     {@link ESTIMATEDPROPERTYCOST }
     *     
     */
    public void setESTIMATEDPROPERTYCOST(ESTIMATEDPROPERTYCOST value) {
        this.estimatedpropertycost = value;
    }

    /**
     * Gets the value of the integrateddisclosuredetail property.
     * 
     * @return
     *     possible object is
     *     {@link INTEGRATEDDISCLOSUREDETAIL }
     *     
     */
    public INTEGRATEDDISCLOSUREDETAIL getINTEGRATEDDISCLOSUREDETAIL() {
        return integrateddisclosuredetail;
    }

    /**
     * Sets the value of the integrateddisclosuredetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link INTEGRATEDDISCLOSUREDETAIL }
     *     
     */
    public void setINTEGRATEDDISCLOSUREDETAIL(INTEGRATEDDISCLOSUREDETAIL value) {
        this.integrateddisclosuredetail = value;
    }

    /**
     * Gets the value of the integrateddisclosuresectionsummaries property.
     * 
     * @return
     *     possible object is
     *     {@link INTEGRATEDDISCLOSURESECTIONSUMMARIES }
     *     
     */
    public INTEGRATEDDISCLOSURESECTIONSUMMARIES getINTEGRATEDDISCLOSURESECTIONSUMMARIES() {
        return integrateddisclosuresectionsummaries;
    }

    /**
     * Sets the value of the integrateddisclosuresectionsummaries property.
     * 
     * @param value
     *     allowed object is
     *     {@link INTEGRATEDDISCLOSURESECTIONSUMMARIES }
     *     
     */
    public void setINTEGRATEDDISCLOSURESECTIONSUMMARIES(INTEGRATEDDISCLOSURESECTIONSUMMARIES value) {
        this.integrateddisclosuresectionsummaries = value;
    }

    /**
     * Gets the value of the projectedpayments property.
     * 
     * @return
     *     possible object is
     *     {@link PROJECTEDPAYMENTS }
     *     
     */
    public PROJECTEDPAYMENTS getPROJECTEDPAYMENTS() {
        return projectedpayments;
    }

    /**
     * Sets the value of the projectedpayments property.
     * 
     * @param value
     *     allowed object is
     *     {@link PROJECTEDPAYMENTS }
     *     
     */
    public void setPROJECTEDPAYMENTS(PROJECTEDPAYMENTS value) {
        this.projectedpayments = value;
    }

}