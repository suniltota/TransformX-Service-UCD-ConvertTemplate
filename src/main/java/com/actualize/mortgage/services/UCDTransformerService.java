package com.actualize.mortgage.services;

import java.io.InputStream;
import java.util.Properties;

import org.mismo.residential._2009.schemas.MESSAGE;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import com.actualize.mortgage.mappingmodels.IntermediateXMLData;
import com.actualize.mortgage.mappingmodels.UCDXMLResult;
 /**
  * This class defines the services for generating UCD XML from different templates.
  * @author sboragala
  *
  */
@Service
public interface UCDTransformerService {
	
	    
    /**
     * This method converts the client specific text file or properties file into intermediate XML on basis on mapping file specified to the client
     * @param mappingFile
     * @param dataInTxtFormat
     * @return IntermediateXMLData
     * @throws Exception
     */
    public IntermediateXMLData generateIntermediateXMLForTxtTemplate(InputStream mappingFile, Properties dataInTxtFormat) throws Exception;

    /**
     * This method generates the Master XML from intermediate XML
     * @param intermediateXMLData
     * @return MESSAGE object
     * @throws Exception
     */
    public MESSAGE generateMasterXML(IntermediateXMLData intermediateXMLData) throws Exception;
    
    /**
     * This method generates UCD XML with conversion errors(if any) on processing the Master XML
     * @param message
     * @return UCDXMLResult
     * @throws Exception
     */
    public UCDXMLResult generateUCDXML(MESSAGE message) throws Exception;

    /**
     * This method converts the document to JAXB MESSAGE Object
     * @param xml
     * @return MESSAGE
     * @throws Exception
     */
    public MESSAGE transformXmlToObject(Document xml) throws Exception;
}
