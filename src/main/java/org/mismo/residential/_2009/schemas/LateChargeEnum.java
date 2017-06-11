//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.12.01 at 06:02:48 PM IST 
//


package org.mismo.residential._2009.schemas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * The type of late charge that may be applied to the loan.
 * 
 * <p>Java class for LateChargeEnum complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LateChargeEnum">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.mismo.org/residential/2009/schemas>LateChargeBase">
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LateChargeEnum", propOrder = {
    "value"
})
public class LateChargeEnum {

    @XmlValue
    protected LateChargeBase value;

    /**
     * Term: Late Charge Type Definition: The type of late charge that may be applied to the loan.
     * 
     * @return
     *     possible object is
     *     {@link LateChargeBase }
     *     
     */
    public LateChargeBase getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link LateChargeBase }
     *     
     */
    public void setValue(LateChargeBase value) {
        this.value = value;
    }

}