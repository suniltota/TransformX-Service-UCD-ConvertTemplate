package com.actualize.mortgage.xmlutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Class <code>XsltTransform </code> execute as main class which is responsible xslt transformation
 * 
 */
public class XsltTransform {
	public void transform(String xsltfile, String infile, String outfile) throws ParserConfigurationException, IOException, TransformerFactoryConfigurationError, TransformerException, SAXException {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(infile);
		Transformer transformer = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null).newTemplates(new StreamSource(xsltfile)).newTransformer();
		transformer.transform(new DOMSource(doc), new StreamResult(new File(outfile)));
	}
	
	/**
	 * 
	 * @param file object of  File}
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public void echoToStdout(File file) throws UnsupportedEncodingException, IOException {
		InputStream input = new FileInputStream(file);
		byte[] buffer = new byte[1024];
		while (input.read(buffer) != -1) {
			System.out.print(new String(buffer, "UTF-8"));
		}
		input.close();
	}
	
		
	private String transformDir = null;
	
	private String getTransformDir() {
		if (transformDir == null) {
			ClassLoader cl = ClassLoader.getSystemClassLoader();
			URL[] urls = ((URLClassLoader)cl).getURLs();
			for (URL url : urls)
				if (url.getFile().matches(".*lib.pdfbox-app.*")) {
					transformDir = url.getFile().replaceAll("%20", " ").replaceFirst("lib.pdfbox-app.*", "lib");
					break;
				}
		}
		return transformDir;
	}
}
