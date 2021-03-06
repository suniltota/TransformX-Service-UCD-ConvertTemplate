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
 * Identifies the role that the party plays in the transaction. Parties may be either a person or legal entity. A party may play multiple roles in a transaction.
 * 
 * <p>Java class for PartyRoleEnum complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PartyRoleEnum">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.mismo.org/residential/2009/schemas>PartyRoleBase">
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PartyRoleEnum", propOrder = {
    "value"
})
public class PartyRoleEnum {

    @XmlValue
    protected PartyRoleBase value;

    /**
     * Term: Party Role Type Definition: Identifies the role that the party plays in the transaction. Parties may be either a person or legal entity. A party may play multiple roles in a transaction.
     * 
     * @return
     *     possible object is
     *     {@link PartyRoleBase }
     *     
     */
    public PartyRoleBase getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyRoleBase }
     *     
     */
    public void setValue(PartyRoleBase value) {
        this.value = value;
    }

}
