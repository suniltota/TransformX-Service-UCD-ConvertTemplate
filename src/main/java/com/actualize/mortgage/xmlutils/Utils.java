package com.actualize.mortgage.xmlutils;

import java.io.ByteArrayInputStream;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class <code>Utils </code> execute as utils class which is helpful to create the xmls
 * 
 */
@SuppressWarnings("unused")
public class Utils {
	private static final Logger LOGGER = Logger.getLogger( Utils.class.getName() );
	public static InputStream convertToInputStream(Document doc) throws TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Source xmlSource = new DOMSource(doc);
		Result outputTarget = new StreamResult(outputStream);
		try {
			TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return new ByteArrayInputStream(outputStream.toByteArray());
	}

	/**
	 * Method to remove empty node
	 * 
	 * @param node
	 */
	public static void removeEmptyNodes(Node node) {
		removeEmptyNodes(node, new IsEmptyNode() {
			public boolean isEmptyNode(Node node) {
				boolean emptyElementNode = node.getNodeType() == Node.ELEMENT_NODE && node.getChildNodes().getLength() == 0 && node.getAttributes().getLength() == 0;
				boolean emptyTextNode = node.getNodeType() == Node.TEXT_NODE    && node.getNodeValue().trim().isEmpty();
			    return emptyElementNode || emptyTextNode;	
			}
		});
	}
	/**
	 * Method to remove node from xml
	 * 
	 * @param doc object of  Document}
	 * @param expression
	 * @throws XPathExpressionException
	 */
	public static void removeNodes(Document doc, String expression) throws XPathExpressionException {
		// Grab nodes to remove
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodelist = (NodeList)xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);

	    // Save all children nodes into list that doesn't change
	    List<Node> immutable = new LinkedList<Node>();
	    for (int i = 0; i < nodelist.getLength(); i++)
	    	immutable.add(nodelist.item(i));

		// Recursive through list that doesn't change
	    for (Node child : immutable)
	    	child.getParentNode().removeChild(child);
	}
	/**
	 * 
	 * @param document object of  Document}
	 * @param out created file name
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 */
	public static void writeDocument(Document document, OutputStream out) throws TransformerFactoryConfigurationError, TransformerException {

		// Prepare document to write
		Transformer tr = TransformerFactory.newInstance().newTransformer();
	    tr.setOutputProperty(OutputKeys.INDENT, "yes");
	    tr.setOutputProperty(OutputKeys.METHOD, "xml");
	    tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	    tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

	    // Write xmldoc to stream out
	    tr.transform(new DOMSource(document), new StreamResult(out));
	}
	/**
	 *  Method is used to write the xml file
	 *  
	 * @param document object of  Document}
	 * @param filename name of the file
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 * @throws IOException
	 */
	public static void writeDocument(Document document, String filename) throws TransformerFactoryConfigurationError, TransformerException, IOException {
    	LOGGER.log(Level.FINE, "Writing file '" + filename + "'...");
        OutputStream out;
		out = new FileOutputStream(filename);
		Utils.writeDocument(document, out);
		out.close();
        LOGGER.log(Level.FINE, "...done with file write.");        
	}
  /**
   * 
   * @param node node of xml file
   * @param emptyCheck boolean to check if node is empty
   */
	private static void removeEmptyNodes(Node node, IsEmptyNode emptyCheck) {
		// Grab all children of node
	    NodeList childnodes = node.getChildNodes();

	    // Save all children nodes into list that doesn't change
	    List<Node> immutable = new LinkedList<Node>();
	    for (int i = 0; i < childnodes.getLength(); i++)
	    	immutable.add(childnodes.item(i));

		// Recursive through list that doesn't change
	    for (Node child : immutable)
	    	removeEmptyNodes(child, emptyCheck);
	    
	    // Remove node if empty
	    if (emptyCheck.isEmptyNode(node))
	    	node.getParentNode().removeChild(node);
	}
}
