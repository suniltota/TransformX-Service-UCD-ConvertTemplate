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
 * <p>Java class for LienPriorityBase.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="LienPriorityBase">
 *   &lt;restriction base="{http://www.mismo.org/residential/2009/schemas}MISMOEnum_Base">
 *     &lt;enumeration value="FirstLien"/>
 *     &lt;enumeration value="FourthLien"/>
 *     &lt;enumeration value="SecondLien"/>
 *     &lt;enumeration value="ThirdLien"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "LienPriorityBase")
@XmlEnum
public enum LienPriorityBase {


    /**
     * A mortgage that gives the mortgagee a security right over all other mortgages of the mortgaged property.
     * 
     */
    @XmlEnumValue("FirstLien")
    FIRST_LIEN("FirstLien"),
    @XmlEnumValue("FourthLien")
    FOURTH_LIEN("FourthLien"),
    @XmlEnumValue("SecondLien")
    SECOND_LIEN("SecondLien"),
    @XmlEnumValue("ThirdLien")
    THIRD_LIEN("ThirdLien");
    private final String value;

    LienPriorityBase(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static LienPriorityBase fromValue(String v) {
        for (LienPriorityBase c: LienPriorityBase.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}