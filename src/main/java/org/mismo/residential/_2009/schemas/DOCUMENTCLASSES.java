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
 * Contains repeatable information about the type of document being described. Is used to address multi-title documents.
 * 
 * <p>Java class for DOCUMENT_CLASSES complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DOCUMENT_CLASSES">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DOCUMENT_CLASS" type="{http://www.mismo.org/residential/2009/schemas}DOCUMENT_CLASS"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DOCUMENT_CLASSES", propOrder = {
    "documentclass"
})
public class DOCUMENTCLASSES {

    @XmlElement(name = "DOCUMENT_CLASS", required = true)
    protected DOCUMENTCLASS documentclass;

    /**
     * Gets the value of the documentclass property.
     * 
     * @return
     *     possible object is
     *     {@link DOCUMENTCLASS }
     *     
     */
    public DOCUMENTCLASS getDOCUMENTCLASS() {
        return documentclass;
    }

    /**
     * Sets the value of the documentclass property.
     * 
     * @param value
     *     allowed object is
     *     {@link DOCUMENTCLASS }
     *     
     */
    public void setDOCUMENTCLASS(DOCUMENTCLASS value) {
        this.documentclass = value;
    }

}
