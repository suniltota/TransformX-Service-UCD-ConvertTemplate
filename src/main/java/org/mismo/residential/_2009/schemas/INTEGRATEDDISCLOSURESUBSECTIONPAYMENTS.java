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
 * Information about one or more types of integrated subsection payment. Holds all occurrences of INTEGRATED_DISCLOSURE_SUBSECTION_PAYMENT.
 * 
 * <p>Java class for INTEGRATED_DISCLOSURE_SUBSECTION_PAYMENTS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="INTEGRATED_DISCLOSURE_SUBSECTION_PAYMENTS">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="INTEGRATED_DISCLOSURE_SUBSECTION_PAYMENT" type="{http://www.mismo.org/residential/2009/schemas}INTEGRATED_DISCLOSURE_SUBSECTION_PAYMENT" maxOccurs="5"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "INTEGRATED_DISCLOSURE_SUBSECTION_PAYMENTS", propOrder = {
    "integrateddisclosuresubsectionpayment"
})
public class INTEGRATEDDISCLOSURESUBSECTIONPAYMENTS {

    @XmlElement(name = "INTEGRATED_DISCLOSURE_SUBSECTION_PAYMENT", required = true)
    protected List<INTEGRATEDDISCLOSURESUBSECTIONPAYMENT> integrateddisclosuresubsectionpayment;

    /**
     * Gets the value of the integrateddisclosuresubsectionpayment property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the integrateddisclosuresubsectionpayment property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getINTEGRATEDDISCLOSURESUBSECTIONPAYMENT().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link INTEGRATEDDISCLOSURESUBSECTIONPAYMENT }
     * 
     * 
     */
    public List<INTEGRATEDDISCLOSURESUBSECTIONPAYMENT> getINTEGRATEDDISCLOSURESUBSECTIONPAYMENT() {
        if (integrateddisclosuresubsectionpayment == null) {
            integrateddisclosuresubsectionpayment = new ArrayList<INTEGRATEDDISCLOSURESUBSECTIONPAYMENT>();
        }
        return this.integrateddisclosuresubsectionpayment;
    }

}
