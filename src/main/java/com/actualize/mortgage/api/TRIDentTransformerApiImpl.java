package com.actualize.mortgage.api;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mismo.residential._2009.schemas.MESSAGE;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;

import com.actualize.mortgage.mappingmodels.IntermediateXMLData;
import com.actualize.mortgage.mappingmodels.UCDCSV2XML;
import com.actualize.mortgage.mappingmodels.UCDXMLResult;
import com.actualize.mortgage.services.impl.FileService;
import com.actualize.mortgage.services.impl.UCDTransformerServiceImpl;

import transformer.TRIDTransformer;

/**
 * This class defines the api implemetation of genarating UCD XML from different formats
 * @author sboragala
 * @version 1.0
 */
@RestController
@RequestMapping("/actualize/transformx/transforms")
public class TRIDentTransformerApiImpl {
	
	private static final Logger LOG = LogManager.getLogger(TRIDentTransformerApiImpl.class);
	
    /**
     * converts the csv template to UCD XML
     * @param csvdoc
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/{version}/csvtoxml", method = { RequestMethod.POST }, produces = "application/xml")
    public UCDCSV2XML generateXmlFromCsvCD(@PathVariable String version, @RequestBody String csvdoc) throws Exception {
        LOG.info("Service: csv to xml called");
    	UCDTransformerServiceImpl  ucdTransformerServiceImpl = new UCDTransformerServiceImpl();
        TRIDTransformer transform = new TRIDTransformer();
        Document xmldoc = transform.transformCsvToXml(csvdoc);
        MESSAGE message = ucdTransformerServiceImpl.transformXmlToObject(xmldoc);
        UCDCSV2XML ucdDocument = new UCDCSV2XML();
        ucdDocument.setMessage(message);
        return ucdDocument;
    }
    
    /**
     * converts text data into XML 
     * @param txtdoc
     * @return UCDXMLResult
     * @throws Exception
     */
   @RequestMapping(value = "/{version}/templatetoucd", method = { RequestMethod.POST }, produces = "application/xml")
    public UCDXMLResult generateXmlFromTxtTemplate(@PathVariable String version, @RequestBody String txtdoc) throws Exception {
	    LOG.info("Service: text to xml called");
        Properties propFile = parsePropertiesString(txtdoc);
        InputStream mappingFileStream;
        FileService fileService = new FileService();
        UCDTransformerServiceImpl  ucdTransformerServiceImpl = new UCDTransformerServiceImpl();
        if(null!=fileService.getTextMappingFile() && !"".equalsIgnoreCase(fileService.getTextMappingFile()) && !"TextTemplateMap.xml".equalsIgnoreCase(fileService.getTextMappingFile())) {
            mappingFileStream = new FileInputStream(fileService.getTextMappingFile());
        } else {
            mappingFileStream = getClass().getClassLoader().getResourceAsStream("TextTemplateMap.xml");
        }
        IntermediateXMLData intermediateXMLData = ucdTransformerServiceImpl.generateIntermediateXMLForTxtTemplate(mappingFileStream, propFile);
        MESSAGE message = ucdTransformerServiceImpl.generateMasterXML(intermediateXMLData);
        return ucdTransformerServiceImpl.generateUCDXML(message);
    }
    
    /**
     *convert input data to properties
     * @param s
     * @return Properties
     * @throws Exception
     */
    private Properties parsePropertiesString(String inputData) throws Exception {
        // load() returning void rather than the Properties object
        // so this takes 3 lines instead of "return new Properties().load(...);"
        final Properties p = new Properties();
        p.load(new StringReader(inputData));
        return p;
    }
    
    /**
     * checks the status of the service whether running it or not
     * @param version
     * @return string
     * @throws Exception
     */
    @RequestMapping(value = "/{version}/ping", method = { RequestMethod.GET })
    public String status(@PathVariable String version) throws Exception {
    	LOG.info("Service: ping for template called");
        return "The service for generating UCD XML from various templates is running and ready to accept your request";
    }
}
