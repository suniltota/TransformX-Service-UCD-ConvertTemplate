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
 * <p>Java class for EscrowAbsenceReasonBase.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="EscrowAbsenceReasonBase">
 *   &lt;restriction base="{http://www.mismo.org/residential/2009/schemas}MISMOEnum_Base">
 *     &lt;enumeration value="BorrowerDeclined"/>
 *     &lt;enumeration value="LenderDoesNotOffer"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "EscrowAbsenceReasonBase")
@XmlEnum
public enum EscrowAbsenceReasonBase {

    @XmlEnumValue("BorrowerDeclined")
    BORROWER_DECLINED("BorrowerDeclined"),
    @XmlEnumValue("LenderDoesNotOffer")
    LENDER_DOES_NOT_OFFER("LenderDoesNotOffer");
    private final String value;

    EscrowAbsenceReasonBase(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EscrowAbsenceReasonBase fromValue(String v) {
        for (EscrowAbsenceReasonBase c: EscrowAbsenceReasonBase.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}