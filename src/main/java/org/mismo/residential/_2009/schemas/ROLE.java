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
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for ROLE complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ROLE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="REAL_ESTATE_AGENT" type="{http://www.mismo.org/residential/2009/schemas}REAL_ESTATE_AGENT" minOccurs="0"/>
 *         &lt;element name="LICENSES" type="{http://www.mismo.org/residential/2009/schemas}LICENSES" minOccurs="0"/>
 *         &lt;element name="PARTY_ROLE_IDENTIFIERS" type="{http://www.mismo.org/residential/2009/schemas}PARTY_ROLE_IDENTIFIERS" minOccurs="0"/>
 *         &lt;element name="ROLE_DETAIL" type="{http://www.mismo.org/residential/2009/schemas}ROLE_DETAIL" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.w3.org/1999/xlink}MISMOresourceLink"/>
 *       &lt;attribute name="SequenceNumber" type="{http://www.mismo.org/residential/2009/schemas}MISMOSequenceNumber_Base" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ROLE", propOrder = {
    "realestateagent",
    "licenses",
    "partyroleidentifiers",
    "roledetail"
})
public class ROLE {

    @XmlElement(name = "REAL_ESTATE_AGENT")
    protected REALESTATEAGENT realestateagent;
    @XmlElement(name = "LICENSES")
    protected LICENSES licenses;
    @XmlElement(name = "PARTY_ROLE_IDENTIFIERS")
    protected PARTYROLEIDENTIFIERS partyroleidentifiers;
    @XmlElement(name = "ROLE_DETAIL")
    protected ROLEDETAIL roledetail;
    @XmlAttribute(name = "SequenceNumber")
    protected Integer sequenceNumber;
    @XmlAttribute(name = "label", namespace = "http://www.w3.org/1999/xlink")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String label;

    /**
     * Gets the value of the realestateagent property.
     * 
     * @return
     *     possible object is
     *     {@link REALESTATEAGENT }
     *     
     */
    public REALESTATEAGENT getREALESTATEAGENT() {
        return realestateagent;
    }

    /**
     * Sets the value of the realestateagent property.
     * 
     * @param value
     *     allowed object is
     *     {@link REALESTATEAGENT }
     *     
     */
    public void setREALESTATEAGENT(REALESTATEAGENT value) {
        this.realestateagent = value;
    }

    /**
     * Gets the value of the licenses property.
     * 
     * @return
     *     possible object is
     *     {@link LICENSES }
     *     
     */
    public LICENSES getLICENSES() {
        return licenses;
    }

    /**
     * Sets the value of the licenses property.
     * 
     * @param value
     *     allowed object is
     *     {@link LICENSES }
     *     
     */
    public void setLICENSES(LICENSES value) {
        this.licenses = value;
    }

    /**
     * Gets the value of the partyroleidentifiers property.
     * 
     * @return
     *     possible object is
     *     {@link PARTYROLEIDENTIFIERS }
     *     
     */
    public PARTYROLEIDENTIFIERS getPARTYROLEIDENTIFIERS() {
        return partyroleidentifiers;
    }

    /**
     * Sets the value of the partyroleidentifiers property.
     * 
     * @param value
     *     allowed object is
     *     {@link PARTYROLEIDENTIFIERS }
     *     
     */
    public void setPARTYROLEIDENTIFIERS(PARTYROLEIDENTIFIERS value) {
        this.partyroleidentifiers = value;
    }

    /**
     * Gets the value of the roledetail property.
     * 
     * @return
     *     possible object is
     *     {@link ROLEDETAIL }
     *     
     */
    public ROLEDETAIL getROLEDETAIL() {
        return roledetail;
    }

    /**
     * Sets the value of the roledetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link ROLEDETAIL }
     *     
     */
    public void setROLEDETAIL(ROLEDETAIL value) {
        this.roledetail = value;
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

    /**
     * Gets the value of the label property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the value of the label property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLabel(String value) {
        this.label = value;
    }

}
