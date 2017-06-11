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
 * Information concerning a fee paid at loan closing and reported to HUD according to the provisions of  RESPA.
 * 
 * <p>Java class for FEE complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FEE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FEE_DETAIL" type="{http://www.mismo.org/residential/2009/schemas}FEE_DETAIL" minOccurs="0"/>
 *         &lt;element name="FEE_PAID_TO" type="{http://www.mismo.org/residential/2009/schemas}PAID_TO" minOccurs="0"/>
 *         &lt;element name="FEE_PAYMENTS" type="{http://www.mismo.org/residential/2009/schemas}FEE_PAYMENTS" maxOccurs="5" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FEE", propOrder = {
    "feedetail",
    "feepaidto",
    "feepayments"
})
public class FEE {

    @XmlElement(name = "FEE_DETAIL")
    protected FEEDETAIL feedetail;
    @XmlElement(name = "FEE_PAID_TO")
    protected PAIDTO feepaidto;
    @XmlElement(name = "FEE_PAYMENTS")
    protected List<FEEPAYMENTS> feepayments;

    /**
     * Gets the value of the feedetail property.
     * 
     * @return
     *     possible object is
     *     {@link FEEDETAIL }
     *     
     */
    public FEEDETAIL getFEEDETAIL() {
        return feedetail;
    }

    /**
     * Sets the value of the feedetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link FEEDETAIL }
     *     
     */
    public void setFEEDETAIL(FEEDETAIL value) {
        this.feedetail = value;
    }

    /**
     * Gets the value of the feepaidto property.
     * 
     * @return
     *     possible object is
     *     {@link PAIDTO }
     *     
     */
    public PAIDTO getFEEPAIDTO() {
        return feepaidto;
    }

    /**
     * Sets the value of the feepaidto property.
     * 
     * @param value
     *     allowed object is
     *     {@link PAIDTO }
     *     
     */
    public void setFEEPAIDTO(PAIDTO value) {
        this.feepaidto = value;
    }

    /**
     * Gets the value of the feepayments property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the feepayments property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFEEPAYMENTS().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FEEPAYMENTS }
     * 
     * 
     */
    public List<FEEPAYMENTS> getFEEPAYMENTS() {
        if (feepayments == null) {
            feepayments = new ArrayList<FEEPAYMENTS>();
        }
        return this.feepayments;
    }

}