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
 * <p>Java class for AUDIT_TRAIL_ENTRY_DETAIL complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AUDIT_TRAIL_ENTRY_DETAIL">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="EntryDatetime" type="{http://www.mismo.org/residential/2009/schemas}MISMODatetime" minOccurs="0"/>
 *         &lt;element name="EventType" type="{http://www.mismo.org/residential/2009/schemas}EventEnum" minOccurs="0"/>
 *         &lt;element name="EventTypeOtherDescription" type="{http://www.mismo.org/residential/2009/schemas}MISMOString" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AUDIT_TRAIL_ENTRY_DETAIL", propOrder = {
    "entryDatetime",
    "eventType",
    "eventTypeOtherDescription"
})
public class AUDITTRAILENTRYDETAIL {

    @XmlElement(name = "EntryDatetime")
    protected MISMODatetime entryDatetime;
    @XmlElement(name = "EventType")
    protected EventEnum eventType;
    @XmlElement(name = "EventTypeOtherDescription")
    protected MISMOString eventTypeOtherDescription;

    /**
     * Gets the value of the entryDatetime property.
     * 
     * @return
     *     possible object is
     *     {@link MISMODatetime }
     *     
     */
    public MISMODatetime getEntryDatetime() {
        return entryDatetime;
    }

    /**
     * Sets the value of the entryDatetime property.
     * 
     * @param value
     *     allowed object is
     *     {@link MISMODatetime }
     *     
     */
    public void setEntryDatetime(MISMODatetime value) {
        this.entryDatetime = value;
    }

    /**
     * Gets the value of the eventType property.
     * 
     * @return
     *     possible object is
     *     {@link EventEnum }
     *     
     */
    public EventEnum getEventType() {
        return eventType;
    }

    /**
     * Sets the value of the eventType property.
     * 
     * @param value
     *     allowed object is
     *     {@link EventEnum }
     *     
     */
    public void setEventType(EventEnum value) {
        this.eventType = value;
    }

    /**
     * Gets the value of the eventTypeOtherDescription property.
     * 
     * @return
     *     possible object is
     *     {@link MISMOString }
     *     
     */
    public MISMOString getEventTypeOtherDescription() {
        return eventTypeOtherDescription;
    }

    /**
     * Sets the value of the eventTypeOtherDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link MISMOString }
     *     
     */
    public void setEventTypeOtherDescription(MISMOString value) {
        this.eventTypeOtherDescription = value;
    }

}
