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
 * Holds all occurrences of DOCUMENT_SET. It may contain a collection of FOREIGN_OBJECT elements representing shared document components.
 * 
 * <p>Java class for DOCUMENT_SETS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DOCUMENT_SETS">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DOCUMENT_SET" type="{http://www.mismo.org/residential/2009/schemas}DOCUMENT_SET"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DOCUMENT_SETS", propOrder = {
    "documentset"
})
public class DOCUMENTSETS {

    @XmlElement(name = "DOCUMENT_SET", required = true)
    protected DOCUMENTSET documentset;

    /**
     * Gets the value of the documentset property.
     * 
     * @return
     *     possible object is
     *     {@link DOCUMENTSET }
     *     
     */
    public DOCUMENTSET getDOCUMENTSET() {
        return documentset;
    }

    /**
     * Sets the value of the documentset property.
     * 
     * @param value
     *     allowed object is
     *     {@link DOCUMENTSET }
     *     
     */
    public void setDOCUMENTSET(DOCUMENTSET value) {
        this.documentset = value;
    }

}
