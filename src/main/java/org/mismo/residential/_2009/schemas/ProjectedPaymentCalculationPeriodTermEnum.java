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
 * The duration of time used to define the projected payment period.
 * 
 * <p>Java class for ProjectedPaymentCalculationPeriodTermEnum complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProjectedPaymentCalculationPeriodTermEnum">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.mismo.org/residential/2009/schemas>ProjectedPaymentCalculationPeriodTermBase">
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProjectedPaymentCalculationPeriodTermEnum", propOrder = {
    "value"
})
public class ProjectedPaymentCalculationPeriodTermEnum {

    @XmlValue
    protected ProjectedPaymentCalculationPeriodTermBase value;

    /**
     * Term: Projected Payment Calculation Period Term Type Definition: The duration of time used to define the projected payment period.
     * 
     * @return
     *     possible object is
     *     {@link ProjectedPaymentCalculationPeriodTermBase }
     *     
     */
    public ProjectedPaymentCalculationPeriodTermBase getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProjectedPaymentCalculationPeriodTermBase }
     *     
     */
    public void setValue(ProjectedPaymentCalculationPeriodTermBase value) {
        this.value = value;
    }

}
