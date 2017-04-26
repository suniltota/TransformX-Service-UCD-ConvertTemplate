package com.actualize.transformx.mappingmodels;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;

public class DataElement {

    private String inputType;
    private String inputId;
    private String includeInXmlIndicator;
    private String xPathValue;
    private String groupIdentifier;
    private String dataPointName;
    private String dataValue;
    private String enumerationValues;
    private String outputFormat;
    private List<DataElement> dataElement;

    @XmlElement(name = "INPUT_TYPE")
    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    @XmlElement(name = "INPUT_ID")
    public String getInputId() {
        return inputId;
    }

    public void setInputId(String inputId) {
        this.inputId = inputId;
    }

    @XmlElement(name = "INCLUDE_IN_XML_INDICATOR")
    public String getIncludeInXmlIndicator() {
        return includeInXmlIndicator;
    }

    public void setIncludeInXmlIndicator(String includeInXmlIndicator) {
        this.includeInXmlIndicator = includeInXmlIndicator;
    }

    /**
     * @return the xPathValue
     */
    @XmlElement(name = "XPATH_VALUE")
    public String getxPathValue() {
        return xPathValue;
    }

    /**
     * @param xPathValue
     *            the xPathValue to set
     */
    public void setxPathValue(String xPathValue) {
        this.xPathValue = xPathValue;
    }

    /**
     * @return the groupIdentifier
     */
    @XmlElement(name = "GROUP_IDENTIFIER")
    public String getGroupIdentifier() {
        return groupIdentifier;
    }

    /**
     * @param groupIdentifier
     *            the groupIdentifier to set
     */
    public void setGroupIdentifier(String groupIdentifier) {
        this.groupIdentifier = groupIdentifier;
    }

    @XmlElement(name = "DATA_POINT_NAME")
    public String getDataPointName() {
        return dataPointName;
    }

    public void setDataPointName(String dataPointName) {
        this.dataPointName = dataPointName;
    }

    @XmlElement(name = "DATA_VALUE")
    public String getDataValue() {
        return dataValue;
    }

    public void setDataValue(String dataValue) {
        this.dataValue = dataValue;
    }

    @XmlElement(name = "ENUMERATION_VALUES")
    public String getEnumerationValues() {
        return enumerationValues;
    }

    public void setEnumerationValues(String enumerationValues) {
        this.enumerationValues = enumerationValues;
    }

    /**
     * @return the outputFormat
     */
    @XmlElement(name = "OUTPUT_FORMAT")
    public String getOutputFormat() {
        return outputFormat;
    }

    /**
     * @param outputFormat
     *            the outputFormat to set
     */
    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    /**
     * @return the embedded data elements
     */
    @XmlElement(name = "DataElement")
    public List<DataElement> getDataElement() {
        return dataElement;
    }

    /**
     * @param dataElement
     *            the dataElement to set
     */
    public void setDataElement(List<DataElement> dataElement) {
        this.dataElement = dataElement;
    }

}
