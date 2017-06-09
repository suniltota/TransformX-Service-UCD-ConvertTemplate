package com.actualize.mortgage.mappingmodels;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "UCDData")
public class IntermediateXMLData {

    List<DataElement> dataElementObject;

    @XmlElement(name = "DataElement")
    public List<DataElement> getDataElementObject() {
        return dataElementObject;
    }

    public void setDataElementObject(List<DataElement> dataElementObject) {
        this.dataElementObject = dataElementObject;
    }
}
