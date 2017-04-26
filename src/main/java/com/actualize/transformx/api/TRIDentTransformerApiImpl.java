package com.actualize.transformx.api;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mismo.residential._2009.schemas.MESSAGE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;

import com.actualize.transformx.mappingmodels.IntermediateXMLData;
import com.actualize.transformx.mappingmodels.UCDCSV2XML;
import com.actualize.transformx.mappingmodels.UCDXMLResult;
import com.actualize.transformx.services.UCDTransformerService;
import com.actualize.transformx.services.impl.FileService;

import transformer.TRIDTransformer;

/**
 * This class defines the api implemetation of genarating UCD XML from different formats
 * @author sboragala
 * @version 1.0
 */
@RestController
@RequestMapping("actualize/transformx/transforms")
public class TRIDentTransformerApiImpl {
	
	private static final Logger LOG = LogManager.getLogger(TRIDentTransformerApiImpl.class);
	
    @Autowired
    private UCDTransformerService ucdTransformerService;

    @Autowired
    private FileService fileService;

    /**
     * 
     * @param csvdoc
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/csvtoxml", method = { RequestMethod.POST }, produces = "application/xml")
    public UCDCSV2XML generateXmlFromCsvCD(@RequestBody String csvdoc) throws Exception {
        // System.out.println("In csvtoxml::::"+csvdoc);
        TRIDTransformer transform = new TRIDTransformer();
        Document xmldoc = transform.transformCsvToXml(csvdoc);
        MESSAGE message = ucdTransformerService.transformXmlToObject(xmldoc);
        UCDCSV2XML ucdDocument = new UCDCSV2XML();
        ucdDocument.setMessage(message);
        return ucdDocument;
    }
    
    /**
     * This API is used to convert text data into XML 
     * @param txtdoc
     * @return UCDXMLResult
     * @throws Exception
     */
    @RequestMapping(value = "/templatetoucd", method = { RequestMethod.POST }, produces = "application/xml")
    public UCDXMLResult generateXmlFromTxtTemplate(@RequestBody String txtdoc) throws Exception {
        Properties propFile = parsePropertiesString(txtdoc);
        InputStream mappingFileStream;
        if(null!=fileService.getTextMappingFile() && !"".equalsIgnoreCase(fileService.getTextMappingFile()) && !"TextTemplateMap.xml".equalsIgnoreCase(fileService.getTextMappingFile())) {
            mappingFileStream = new FileInputStream(fileService.getTextMappingFile());
        } else {
            mappingFileStream = getClass().getClassLoader().getResourceAsStream("TextTemplateMap.xml");
        }
        IntermediateXMLData intermediateXMLData = ucdTransformerService.generateIntermediateXMLForTxtTemplate(mappingFileStream, propFile);
        MESSAGE message = ucdTransformerService.generateMasterXML(intermediateXMLData);
        return ucdTransformerService.generateUCDXML(message);
    }
    
    /**
     * This method is used to convert input data to properties
     * 
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
}
