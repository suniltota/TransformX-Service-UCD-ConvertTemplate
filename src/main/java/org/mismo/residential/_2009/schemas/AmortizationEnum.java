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
 * A classification or description of a loan or a group of loans generally based on the changeability of the rate or payment over time.
 * 
 * <p>Java class for AmortizationEnum complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AmortizationEnum">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.mismo.org/residential/2009/schemas>AmortizationBase">
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AmortizationEnum", propOrder = {
    "value"
})
public class AmortizationEnum {

    @XmlValue
    protected AmortizationBase value;

    /**
     * Term: Amortization Type Definition: A classification or description of a loan or a group of loans generally based on the changeability of the rate or payment over time.
     * 
     * @return
     *     possible object is
     *     {@link AmortizationBase }
     *     
     */
    public AmortizationBase getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link AmortizationBase }
     *     
     */
    public void setValue(AmortizationBase value) {
        this.value = value;
    }

}
