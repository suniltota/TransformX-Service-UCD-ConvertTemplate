//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.12.01 at 06:02:48 PM IST 
//


package org.datamodelextension;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * The title or description used to identify a primary section of the integrated disclosure document.
 * 
 * <p>Java class for IntegratedDisclosureSectionEnum complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IntegratedDisclosureSectionEnum">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.datamodelextension.org>IntegratedDisclosureSection">
 *       &lt;attribute name="SensitiveIndicator" type="{http://www.datamodelextension.org}Indicator_Base" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IntegratedDisclosureSectionEnum", propOrder = {
    "value"
})
public class IntegratedDisclosureSectionEnum {

    @XmlValue
    protected IntegratedDisclosureSection value;
    @XmlAttribute(name = "SensitiveIndicator")
    protected Boolean sensitiveIndicator;

    /**
     * Term: Integrated Disclosure Section Type Definition: The title or description used to identify a primary section of the integrated disclosure document.
     * 
     * @return
     *     possible object is
     *     {@link IntegratedDisclosureSection }
     *     
     */
    public IntegratedDisclosureSection getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntegratedDisclosureSection }
     *     
     */
    public void setValue(IntegratedDisclosureSection value) {
        this.value = value;
    }

    /**
     * Gets the value of the sensitiveIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSensitiveIndicator() {
        return sensitiveIndicator;
    }

    /**
     * Sets the value of the sensitiveIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSensitiveIndicator(Boolean value) {
        this.sensitiveIndicator = value;
    }

}
