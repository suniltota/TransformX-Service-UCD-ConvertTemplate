package com.actualize.transformx.mappingmodels;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "UCDData")
public class IntermediateXMLMap {

    List<DataElement> dataElementObject;

    /**
     * @return the dataElementObject
     */
    @XmlElement(name = "DataElement")
    public List<DataElement> getDataElementObject() {
        return dataElementObject;
    }

    /**
     * @param dataElementObject
     *            the dataElementObject to set
     */
    public void setDataElementObject(List<DataElement> dataElementObject) {
        this.dataElementObject = dataElementObject;
    }

}
