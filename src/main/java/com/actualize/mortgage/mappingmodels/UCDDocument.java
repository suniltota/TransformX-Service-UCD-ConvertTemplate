package com.actualize.mortgage.mappingmodels;

import javax.xml.bind.annotation.XmlElement;

import org.mismo.residential._2009.schemas.MESSAGE;

public class UCDDocument {

    private MESSAGE message;

    /**
     * @return the message
     */
    @XmlElement(name = "MESSAGE", namespace = "http://www.mismo.org/residential/2009/schemas")
    public MESSAGE getMessage() {
        return message;
    }

    /**
     * @param message
     *            the message to set
     */
    public void setMessage(MESSAGE message) {
        this.message = message;
    }

}
