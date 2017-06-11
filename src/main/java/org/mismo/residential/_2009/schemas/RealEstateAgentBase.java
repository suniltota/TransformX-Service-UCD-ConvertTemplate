//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.12.01 at 06:02:48 PM IST 
//


package org.mismo.residential._2009.schemas;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RealEstateAgentBase.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="RealEstateAgentBase">
 *   &lt;restriction base="{http://www.mismo.org/residential/2009/schemas}MISMOEnum_Base">
 *     &lt;enumeration value="Listing"/>
 *     &lt;enumeration value="Selling"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "RealEstateAgentBase")
@XmlEnum
public enum RealEstateAgentBase {


    /**
     * The agent that listed the property and represents the Seller.
     * 
     */
    @XmlEnumValue("Listing")
    LISTING("Listing"),

    /**
     * The agent that sold the property and represents the Buyer (may also be known as the Buyers Agent).
     * 
     */
    @XmlEnumValue("Selling")
    SELLING("Selling");
    private final String value;

    RealEstateAgentBase(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RealEstateAgentBase fromValue(String v) {
        for (RealEstateAgentBase c: RealEstateAgentBase.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}