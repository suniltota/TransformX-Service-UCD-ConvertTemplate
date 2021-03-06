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
 * Specifies the type of Escrow Item.
 * 
 * <p>Java class for EscrowItemEnum complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EscrowItemEnum">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.mismo.org/residential/2009/schemas>EscrowItemBase">
 *       &lt;attribute ref="{http://www.datamodelextension.org}DisplayLabelText"/>
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EscrowItemEnum", propOrder = {
    "value"
})
public class EscrowItemEnum {

    @XmlValue
    protected EscrowItemBase value;
    @XmlAttribute(name = "DisplayLabelText", namespace = "http://www.datamodelextension.org")
    protected String displayLabelText;

    /**
     * Term: Escrow Item Type Definition: Specifies the type of Escrow Item.
     * 
     * @return
     *     possible object is
     *     {@link EscrowItemBase }
     *     
     */
    public EscrowItemBase getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link EscrowItemBase }
     *     
     */
    public void setValue(EscrowItemBase value) {
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
