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
 * Used to hold content that is not part of the namespace or version of the MISMO V3 model  It is either contained directly (via EmbeddedContentXML) or indirectly (via LocationURI). A FOREIGN_OBJECT can be Encoded in a variety of manners without 
 * 
 * <p>Java class for FOREIGN_OBJECT complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FOREIGN_OBJECT">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="EmbeddedContentXML" type="{http://www.mismo.org/residential/2009/schemas}MISMOXML"/>
 *         &lt;element name="MIMETypeIdentifier" type="{http://www.mismo.org/residential/2009/schemas}MISMOIdentifier"/>
 *         &lt;element name="ObjectEncodingType" type="{http://www.mismo.org/residential/2009/schemas}ObjectEncodingEnum"/>
 *         &lt;element name="ObjectName" type="{http://www.mismo.org/residential/2009/schemas}MISMOString"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FOREIGN_OBJECT", propOrder = {
    "embeddedContentXML",
    "mimeTypeIdentifier",
    "objectEncodingType",
    "objectName"
})
public class FOREIGNOBJECT {

    @XmlElement(name = "EmbeddedContentXML", required = true)
    protected MISMOXML embeddedContentXML;
    @XmlElement(name = "MIMETypeIdentifier", required = true)
    protected MISMOIdentifier mimeTypeIdentifier;
    @XmlElement(name = "ObjectEncodingType", required = true)
    protected ObjectEncodingEnum objectEncodingType;
    @XmlElement(name = "ObjectName", required = true)
    protected MISMOString objectName;

    /**
     * Gets the value of the embeddedContentXML property.
     * 
     * @return
     *     possible object is
     *     {@link MISMOXML }
     *     
     */
    public MISMOXML getEmbeddedContentXML() {
        return embeddedContentXML;
    }

    /**
     * Sets the value of the embeddedContentXML property.
     * 
     * @param value
     *     allowed object is
     *     {@link MISMOXML }
     *     
     */
    public void setEmbeddedContentXML(MISMOXML value) {
        this.embeddedContentXML = value;
    }

    /**
     * Gets the value of the mimeTypeIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link MISMOIdentifier }
     *     
     */
    public MISMOIdentifier getMIMETypeIdentifier() {
        return mimeTypeIdentifier;
    }

    /**
     * Sets the value of the mimeTypeIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link MISMOIdentifier }
     *     
     */
    public void setMIMETypeIdentifier(MISMOIdentifier value) {
        this.mimeTypeIdentifier = value;
    }

    /**
     * Gets the value of the objectEncodingType property.
     * 
     * @return
     *     possible object is
     *     {@link ObjectEncodingEnum }
     *     
     */
    public ObjectEncodingEnum getObjectEncodingType() {
        return objectEncodingType;
    }

    /**
     * Sets the value of the objectEncodingType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ObjectEncodingEnum }
     *     
     */
    public void setObjectEncodingType(ObjectEncodingEnum value) {
        this.objectEncodingType = value;
    }

    /**
     * Gets the value of the objectName property.
     * 
     * @return
     *     possible object is
     *     {@link MISMOString }
     *     
     */
    public MISMOString getObjectName() {
        return objectName;
    }

    /**
     * Sets the value of the objectName property.
     * 
     * @param value
     *     allowed object is
     *     {@link MISMOString }
     *     
     */
    public void setObjectName(MISMOString value) {
        this.objectName = value;
    }

}
