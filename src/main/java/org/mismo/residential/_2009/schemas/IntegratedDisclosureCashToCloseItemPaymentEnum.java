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
 * Identifies whether the Cash To Close Item Amount is due to or from the borrower.
 * 
 * <p>Java class for IntegratedDisclosureCashToCloseItemPaymentEnum complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IntegratedDisclosureCashToCloseItemPaymentEnum">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.mismo.org/residential/2009/schemas>IntegratedDisclosureCashToCloseItemPaymentBase">
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IntegratedDisclosureCashToCloseItemPaymentEnum", propOrder = {
    "value"
})
public class IntegratedDisclosureCashToCloseItemPaymentEnum {

    @XmlValue
    protected IntegratedDisclosureCashToCloseItemPaymentBase value;

    /**
     * Term: Integrated Disclosure Cash To Close Item Payment Type Definition: Identifies whether the Cash To Close Item Amount is due to or from the borrower.
     * 
     * @return
     *     possible object is
     *     {@link IntegratedDisclosureCashToCloseItemPaymentBase }
     *     
     */
    public IntegratedDisclosureCashToCloseItemPaymentBase getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntegratedDisclosureCashToCloseItemPaymentBase }
     *     
     */
    public void setValue(IntegratedDisclosureCashToCloseItemPaymentBase value) {
        this.value = value;
    }

}