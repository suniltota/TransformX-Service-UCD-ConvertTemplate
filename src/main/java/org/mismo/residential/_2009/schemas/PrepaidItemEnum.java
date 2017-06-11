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
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * Identification of a monthly housing expense component that must be paid in advance.
 * 
 * <p>Java class for PrepaidItemEnum complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PrepaidItemEnum">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.mismo.org/residential/2009/schemas>PrepaidItemBase">
 *       &lt;attribute ref="{http://www.datamodelextension.org}DisplayLabelText"/>
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PrepaidItemEnum", propOrder = {
    "value"
})
public class PrepaidItemEnum {

    @XmlValue
    protected PrepaidItemBase value;
    @XmlAttribute(name = "DisplayLabelText", namespace = "http://www.datamodelextension.org")
    protected String displayLabelText;

    /**
     * Term: Prepaid Item Type Definition: Identification of a monthly housing expense component that must be paid in advance.
     * 
     * @return
     *     possible object is
     *     {@link PrepaidItemBase }
     *     
     */
    public PrepaidItemBase getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link PrepaidItemBase }
     *     
     */
    public void setValue(PrepaidItemBase value) {
        this.value = value;
    }

    /**
     * Gets the value of the displayLabelText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDisplayLabelText() {
        return displayLabelText;
    }

    /**
     * Sets the value of the displayLabelText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDisplayLabelText(String value) {
        this.displayLabelText = value;
    }

}