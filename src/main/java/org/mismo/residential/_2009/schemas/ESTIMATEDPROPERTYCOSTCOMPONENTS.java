//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.12.01 at 06:02:48 PM IST 
//


package org.mismo.residential._2009.schemas;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Information about one or more estimated property costs. Holds all occurrences of ESTIMATED_PROPERTY_COST_COMPONENT.
 * 
 * <p>Java class for ESTIMATED_PROPERTY_COST_COMPONENTS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ESTIMATED_PROPERTY_COST_COMPONENTS">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ESTIMATED_PROPERTY_COST_COMPONENT" type="{http://www.mismo.org/residential/2009/schemas}ESTIMATED_PROPERTY_COST_COMPONENT" maxOccurs="15" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ESTIMATED_PROPERTY_COST_COMPONENTS", propOrder = {
    "estimatedpropertycostcomponent"
})
public class ESTIMATEDPROPERTYCOSTCOMPONENTS {

    @XmlElement(name = "ESTIMATED_PROPERTY_COST_COMPONENT")
    protected List<ESTIMATEDPROPERTYCOSTCOMPONENT> estimatedpropertycostcomponent;

    /**
     * Gets the value of the estimatedpropertycostcomponent property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the estimatedpropertycostcomponent property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getESTIMATEDPROPERTYCOSTCOMPONENT().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ESTIMATEDPROPERTYCOSTCOMPONENT }
     * 
     * 
     */
    public List<ESTIMATEDPROPERTYCOSTCOMPONENT> getESTIMATEDPROPERTYCOSTCOMPONENT() {
        if (estimatedpropertycostcomponent == null) {
            estimatedpropertycostcomponent = new ArrayList<ESTIMATEDPROPERTYCOSTCOMPONENT>();
        }
        return this.estimatedpropertycostcomponent;
    }

}