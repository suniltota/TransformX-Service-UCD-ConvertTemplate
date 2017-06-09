package com.actualize.mortgage.mappingmodels;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class ConversionErrors {

    List<ConversionError> conversionErrors;

    /**
     * @return the conversionErrors
     */
    @XmlElement(name = "TRANSFORM_ERROR")
    public List<ConversionError> getConversionErrors() {
        return conversionErrors;
    }

    /**
     * @param conversionErrors
     *            the conversionErrors to set
     */
    public void setConversionErrors(List<ConversionError> conversionErrors) {
        this.conversionErrors = conversionErrors;
    }

}
