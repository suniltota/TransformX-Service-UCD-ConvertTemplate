package com.actualize.mortgage.transformer;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.actualize.mortgage.xmlutils.Utils;

/**
 * Class <code>MISMOUtils </code> execute as main class which created document builder , process each xml and
 * Write document
 *
 */
public class MISMOUtils {
    private static final Logger LOGGER = Logger.getLogger( MISMOUtils.class.getName() );
    private static String mismoAlias = "mismo:";
    
    private enum Command {
        MERGE
    }

    private MISMOUtils() {
        // No instances allowed
    }
 /**
  * Method which used to create document builder , process xml and write the document
  * @param mergeTag 
  * @param args array of arguments which contains the xml file paths
  * @throws MISMOUtilsException
  */
    public static void merge(String mergeTag, String[] args) throws MISMOUtilsException {
        // Create document builder
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new MISMOUtilsException("Failed to create DocumentBuilder", e);
        }

        // Process each XML
        Document finalDoc = null;
        Node dealSetNode = null;
        for (int i = 0; i < args.length; i++) {
            LOGGER.log(Level.FINE, "Reading file '" + args[i] + "'...");
            Document doc;
            try {
                doc = db.parse(new File(args[i]));
            } catch (SAXException | IOException e) {
                throw new MISMOUtilsException("Failed to parse XML", e);
            }
            if (i == 0) {
                finalDoc = doc;
                dealSetNode = finalDoc.getElementsByTagName(mismoAlias + mergeTag).item(0).getParentNode();
            } else {
                LOGGER.log(Level.FINE, "...com.actualize.mortgage.merge XMLs...");
                NodeList nodelist = doc.getElementsByTagName(mismoAlias + mergeTag);
                for (int j = 0; j < nodelist.getLength(); j++)
                    dealSetNode.appendChild(finalDoc.importNode(nodelist.item(j), true));
            }
            LOGGER.log(Level.FINE, "...done with file.");
        }

        // Write document
        try {
            Utils.writeDocument(finalDoc, args[0]);
        } catch (TransformerFactoryConfigurationError | TransformerException | IOException e) {
            throw new MISMOUtilsException("Failed to write Document", e);
        }
    }
    
    static void usage() {
        LOGGER.log(Level.SEVERE, "Incorrect usage");
    }
    
}
