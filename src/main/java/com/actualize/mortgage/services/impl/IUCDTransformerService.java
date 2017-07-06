package com.actualize.mortgage.services.impl;

import java.io.InputStream;
import java.util.Properties;

import org.mismo.residential._2009.schemas.MESSAGE;
import org.w3c.dom.Document;

import com.actualize.mortgage.mappingmodels.IntermediateXMLData;
import com.actualize.mortgage.mappingmodels.UCDXMLResult;

public interface IUCDTransformerService {

	public MESSAGE generateMasterXML(IntermediateXMLData intermediateXMLData) throws Exception;

	public String generateDocument(IntermediateXMLData intermediateXMLData) throws Exception;

	public MESSAGE transformXmlToObject(Document xmlout) throws Exception;

	public UCDXMLResult generateUCDXML(MESSAGE message);

	public IntermediateXMLData generateIntermediateXMLForTxtTemplate(InputStream mappingFile, Properties propFile)
			throws Exception;
}
