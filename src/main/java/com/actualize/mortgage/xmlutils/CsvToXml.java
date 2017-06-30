package com.actualize.mortgage.xmlutils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Class <code>CsvToXml </code> execute as main class which takes csv as input and create xml as output
 * 
 */
public class CsvToXml {
	String[] tokenize(String str) {
		return str.replaceAll("^\"", "").split("\"?(,|$)(?=(([^\"]*\"){2})*[^\"]*$) *\"?");
	}
	
	/**
	 * 
	 * @return object of  Document}
	 * @throws ParserConfigurationException
	 */
	public Document createDocument() throws ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        return builder.newDocument();		
	}
	
	/**
	 * 
	 * @param name the name of the element
	 * @param doc 
	 * @param parent parent node 
	 * @return Object of  Element}
	 */
	public Element createElement(String name, Document doc, Element parent) {
		Element resultsElement = doc.createElement(name);
        if (parent == null)
        	doc.appendChild(resultsElement);
        else
        	parent.appendChild(resultsElement);
		return resultsElement;
	}

	 /**
	  * Metod used to transform the element from csv to xml
	  * 
	  * @param in object of  BufferedReader}
	  * @return Object of  Document}
	  * @throws Exception
	  */
	public Document transform(BufferedReader in) throws Exception {
		Document document = createDocument();
		Element csvElement = createElement("UCDData", document, null);
		String[] header = null;
		String line = null;
		int lineCount = 1;
		while ((line = in.readLine()) != null)
			if (header == null) {
				header = tokenize(line);
				for (int i = 0; i < header.length; i++)
					header[i] = header[i].replaceAll(" ", "_");  // no spaces in header
			} else {
				lineCount++;
				Element dataElement = createElement("DataElement", document, csvElement);
				String[] tokens = tokenize(line);
				if (header.length < tokens.length)
					throw new Exception("line " + lineCount + " format error, expecting " + header.length + " element(s) and found " + tokens.length + " element(s)");
				for (int i = 0; i < tokens.length && i < header.length; i++)
					if (tokens[i] != null && !tokens[i].isEmpty())
						try {
							createElement(header[i], document, dataElement).setTextContent(tokens[i]);
						} catch (Exception e){
							System.err.println("Error on line " + lineCount + " token " + i + " (" + tokens[i] + ").");
							e.printStackTrace();
							throw e;
						}
			}
		return document;
	}
	
	private static void usage() {
		System.err.println("Usage: CsvToXml <csv filename>"); 
	}
	
}
