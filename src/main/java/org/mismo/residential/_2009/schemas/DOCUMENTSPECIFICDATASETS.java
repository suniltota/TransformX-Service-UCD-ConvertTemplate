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
 * Data that is not used for anything else except for document preparation AND does not fit well anywhere else under DEAL.
 * 
 * <p>Java class for DOCUMENT_SPECIFIC_DATA_SETS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DOCUMENT_SPECIFIC_DATA_SETS">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DOCUMENT_SPECIFIC_DATA_SET" type="{http://www.mismo.org/residential/2009/schemas}DOCUMENT_SPECIFIC_DATA_SET"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DOCUMENT_SPECIFIC_DATA_SETS", propOrder = {
    "documentspecificdataset"
})
public class DOCUMENTSPECIFICDATASETS {

    @XmlElement(name = "DOCUMENT_SPECIFIC_DATA_SET", required = true)
    protected DOCUMENTSPECIFICDATASET documentspecificdataset;

    /**
     * Gets the value of the documentspecificdataset property.
     * 
     * @return
     *     possible object is
     *     {@link DOCUMENTSPECIFICDATASET }
     *     
     */
    public DOCUMENTSPECIFICDATASET getDOCUMENTSPECIFICDATASET() {
        return documentspecificdataset;
    }

    /**
     * Sets the value of the documentspecificdataset property.
     * 
     * @param value
     *     allowed object is
     *     {@link DOCUMENTSPECIFICDATASET }
     *     
     */
    public void setDOCUMENTSPECIFICDATASET(DOCUMENTSPECIFICDATASET value) {
        this.documentspecificdataset = value;
    }

}
