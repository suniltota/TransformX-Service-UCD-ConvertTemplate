//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.12.01 at 06:02:48 PM IST 
//


package org.mismo.residential._2009.schemas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BUYDOWN_RULE complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BUYDOWN_RULE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="BuydownChangeFrequencyMonthsCount" type="{http://www.mismo.org/residential/2009/schemas}MISMOCount" minOccurs="0"/>
 *         &lt;element name="BuydownDurationMonthsCount" type="{http://www.mismo.org/residential/2009/schemas}MISMOCount" minOccurs="0"/>
 *         &lt;element name="BuydownIncreaseRatePercent" type="{http://www.mismo.org/residential/2009/schemas}MISMOPercent" minOccurs="0"/>
 *         &lt;element name="EXTENSION" type="{http://www.mismo.org/residential/2009/schemas}BUYDOWN_RULE_EXTENSION" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BUYDOWN_RULE", propOrder = {
    "buydownChangeFrequencyMonthsCount",
    "buydownDurationMonthsCount",
    "buydownIncreaseRatePercent",
    "extension"
})
public class BUYDOWNRULE {

    @XmlElement(name = "BuydownChangeFrequencyMonthsCount")
    protected MISMOCount buydownChangeFrequencyMonthsCount;
    @XmlElement(name = "BuydownDurationMonthsCount")
    protected MISMOCount buydownDurationMonthsCount;
    @XmlElement(name = "BuydownIncreaseRatePercent")
    protected MISMOPercent buydownIncreaseRatePercent;
    @XmlElement(name = "EXTENSION")
    protected BUYDOWNRULEEXTENSION extension;

    /**
     * Gets the value of the buydownChangeFrequencyMonthsCount property.
     * 
     * @return
     *     possible object is
     *     {@link MISMOCount }
     *     
     */
    public MISMOCount getBuydownChangeFrequencyMonthsCount() {
        return buydownChangeFrequencyMonthsCount;
    }

    /**
     * Sets the value of the buydownChangeFrequencyMonthsCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link MISMOCount }
     *     
     */
    public void setBuydownChangeFrequencyMonthsCount(MISMOCount value) {
        this.buydownChangeFrequencyMonthsCount = value;
    }

    /**
     * Gets the value of the buydownDurationMonthsCount property.
     * 
     * @return
     *     possible object is
     *     {@link MISMOCount }
     *     
     */
    public MISMOCount getBuydownDurationMonthsCount() {
        return buydownDurationMonthsCount;
    }

    /**
     * Sets the value of the buydownDurationMonthsCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link MISMOCount }
     *     
     */
    public void setBuydownDurationMonthsCount(MISMOCount value) {
        this.buydownDurationMonthsCount = value;
    }

    /**
     * Gets the value of the buydownIncreaseRatePercent property.
     * 
     * @return
     *     possible object is
     *     {@link MISMOPercent }
     *     
     */
    public MISMOPercent getBuydownIncreaseRatePercent() {
        return buydownIncreaseRatePercent;
    }

    /**
     * Sets the value of the buydownIncreaseRatePercent property.
     * 
     * @param value
     *     allowed object is
     *     {@link MISMOPercent }
     *     
     */
    public void setBuydownIncreaseRatePercent(MISMOPercent value) {
        this.buydownIncreaseRatePercent = value;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link BUYDOWNRULEEXTENSION }
     *     
     */
    public BUYDOWNRULEEXTENSION getEXTENSION() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link BUYDOWNRULEEXTENSION }
     *     
     */
    public void setEXTENSION(BUYDOWNRULEEXTENSION value) {
        this.extension = value;
    }

}
