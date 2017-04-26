package com.actualize.transformx.mappingmodels;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
/**
 * This class defines the RESULT element in response 
 * @author sboragala
 * @version 1.0
 */
@XmlRootElement(name = "RESULT")
public class UCDXMLResult {

    private UCDDocument ucdDocument;
    private ConversionErrors conversionErrors;

    /**
     * @return the ucdDocument
     */
    @XmlElement(name = "UCD_DOCUMENT")
    public UCDDocument getUcdDocument() {
        return ucdDocument;
    }

    /**
     * @param ucdDocument
     *            the ucdDocument to set
     */
    public void setUcdDocument(UCDDocument ucdDocument) {
        this.ucdDocument = ucdDocument;
    }

    /**
     * @return the conversionErrors
     */
    @XmlElement(name = "TRANSFORM_ERRORS")
    public ConversionErrors getConversionErrors() {
        return conversionErrors;
    }

    /**
     * @param conversionErrors
     *            the conversionErrors to set
     */
    public void setConversionErrors(ConversionErrors conversionErrors) {
        this.conversionErrors = conversionErrors;
    }

}
