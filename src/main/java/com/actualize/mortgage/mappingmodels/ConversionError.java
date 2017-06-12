package com.actualize.mortgage.mappingmodels;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
/**
 * defines the structure for conversion error
 * @author sboragala
 *
 */
public class ConversionError implements Serializable{

	private static final long serialVersionUID = -8907306900069386609L;
	private String inputType;
    private String inputId;
    private String errorCode;
    private String errorMsg;
    private String inputValue;

    /**
     * @return the inputType
     */
    @XmlElement(name = "INPUT_TYPE")
    public String getInputType() {
        return inputType;
    }

    /**
     * @param inputType
     *            the inputType to set
     */
    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    /**
     * @return the inputId
     */
    @XmlElement(name = "INPUT_ID")
    public String getInputId() {
        return inputId;
    }

    /**
     * @param inputId
     *            the inputId to set
     */
    public void setInputId(String inputId) {
        this.inputId = inputId;
    }

    /**
     * @return the errorCode
     */
    @XmlElement(name = "ERROR")
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * @param errorCode
     *            the errorCode to set
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * @return the errorMsg
     */
    @XmlElement(name = "ERROR_MSG")
    public String getErrorMsg() {
        return errorMsg;
    }

    /**
     * @param errorMsg
     *            the errorMsg to set
     */
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    /**
     * @return the inputValue
     */
    public String getInputValue() {
        return inputValue;
    }

    /**
     * @param inputValue
     *            the inputValue to set
     */
    public void setInputValue(String inputValue) {
        this.inputValue = inputValue;
    }

}
