package com.actualize.mortgage.transformer;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.actualize.mortgage.domainmodels.UniformDisclosure;
import com.actualize.mortgage.domainmodels.UniformDisclosureResults;
import com.actualize.mortgage.leform.LoanEstimate;
import com.actualize.mortgage.xmlutils.CsvToXml;
import com.actualize.mortgage.xmlutils.JavaVersion;
import com.actualize.mortgage.xmlutils.Utils;

/**
 * Class <code>TRIDTransformer</code> execute as main class which is responsible
 * for creating PDF files of Loan Estimate and Closing Disclosure
 * 
 */
public class TRIDTransformer {

	private String fileName = "version.txt";
	String filepathTxt;

	protected static final String GSE_ALIAS = "gse";
	protected static final String MISMO_ALIAS = "mismo";
	protected static final String XLINK_ALIAS = "xlink";
	protected static final String XMLNS_ALIAS = "xmlns";
	protected static final String GSE_URI = "http://www.datamodelextension.org";
	protected static final String MISMO_URI = "http://www.mismo.org/residential/2009/schemas";
	protected static final String XLINK_URI = "http://www.w3.org/1999/xlink";
	protected static final String XSI_URI = "http://www.w3.org/2001/XMLSchema-instance";

	private static final Logger LOGGER = Logger.getLogger(TRIDTransformer.class.getName());

	private static final String USAGE = "Usage: TRIDTransformer LoanEstimate|ClosingDisclosure Embedded|Standalone|EmbeddedWithPDF|BaseXMLWithPDF <csv filename>";

	private static final String DECIMAL_FORMAT = "%1.2f";
	private static final String PERCENT_FORMAT = "%1.4f";

	protected DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	protected XPath xPath = XPathFactory.newInstance().newXPath();
	private DocumentType docType;

	/**
	 * Method is used to transform one file into another form
	 * 
	 * @param in
	 *            input file
	 * @param docType
	 *            object of DocumentType}
	 * @param outStyle
	 *            object of OutputStyle}
	 * @param pdfFile
	 *            pdf file
	 * @return object of Document}
	 * @throws Exception
	 */
	public Document transform(BufferedReader in, DocumentType docType, OutputStyle outStyle, String pdfFile)
				throws Exception {

		// Transform input stream to intermediate XML
		LOGGER.log(Level.FINE, "Transforming to intermediate format...");
		CsvToXml intermediateTransformer = new CsvToXml();
		Document intermediateDocument = intermediateTransformer.transform(in);
		in.close();

		// Prune intermediate XML for performance
		LOGGER.log(Level.FINE, "Pruning data not needed...");
		try {
			Utils.removeNodes(intermediateDocument, "/UCDData/DataElement[INCLUDE_IN_XML_INDICATOR='FALSE']");
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Document pruning failed", e);
		}
		this.docType = docType;
		// Transform intermediate XML to UCD
		LOGGER.log(Level.FINE, "Constructing MISMO UCD XML...");
		Document doc = transform(intermediateDocument);

		// Insert PDF
		if (JavaVersion.MAJOR > 7 && outStyle != OutputStyle.Standalone) {
			LOGGER.log(Level.FINE, "Creating PDF...");
			Document resultsDocument = docType == DocumentType.LoanEstimate
					? new LoanEstimate().run(Utils.convertToInputStream(doc), true)
					: new UniformDisclosureResults().run(Utils.convertToInputStream(doc), true);
			NodeList pdfNodeList = resultsDocument.getElementsByTagName("PdfDocument");
			for (int i = 0; i < pdfNodeList.getLength(); i++) {
				Node pdfnode = pdfNodeList.item(i);
				Node insertpoint = doc.getElementsByTagName(MISMO_ALIAS + ":EmbeddedContentXML").item(i);
				if (pdfnode != null && insertpoint != null
						&& (outStyle == OutputStyle.EmbeddedWithPDF || outStyle == OutputStyle.Embedded))
					try {
						insertpoint.appendChild(doc.createTextNode(pdfnode.getTextContent()));
					} catch (Exception e) {
						LOGGER.log(Level.WARNING, "PDF creation failed", e);
					}
				// Write and remove PDF if requested
				LOGGER.log(Level.FINE, "Writing file '" + pdfFile + "'...");
				String pdfFileName = pdfFile;
				if (pdfNodeList.getLength() > 1) {
					pdfFileName = pdfFileName.replace(".pdf", "_" + i + ".pdf");
				}
				if ((outStyle == OutputStyle.EmbeddedWithPDF || outStyle == OutputStyle.BaseXMLWithPDF)
						&& pdfnode != null) {
					Decoder decoder = Base64.getDecoder();
					FileOutputStream out = new FileOutputStream(pdfFileName);
					out.write(decoder.decode(pdfnode.getTextContent()));
					out.getFD().sync();
					out.close();
				}
			}
		} else if (JavaVersion.MAJOR <= 7)
			LOGGER.log(Level.SEVERE, "PDF creation requires Java upgrade to 8 or greater");

		// Remove empty nodes
		LOGGER.log(Level.FINE, "Removing empty nodes...");
		Utils.removeEmptyNodes(doc);
		return doc;
	}

	public boolean convertToAbsolute() {
		return false;
	}

	/**
	 * Transform input xml file to another xml file
	 * 
	 * @param xmlin
	 *            input xml file
	 * @return object of Document}
	 */
	public Document transform(Document xmlin) throws ParserConfigurationException {
		Document xmlout = createDocument();
		Element message = (Element) xmlout.appendChild(xmlout.createElement(addNamespace("MESSAGE")));
		insertMessage(xmlin, "MESSAGE/", null, xmlout, message);
		return xmlout;
	}

	/**
	 * 
	 * @param tag
	 *            node form xml
	 * @return node name
	 */
	protected String addNamespace(String tag) {
		return (tag.indexOf(':') == -1 ? MISMO_ALIAS + ":" : "") + tag;
	}

	/**
	 * 
	 * @param xpath
	 *            xpath value
	 * @return value of xpath
	 */
	protected String baseXPath(String xpath) {
		String value = "/UCDData/DataElement[INCLUDE_IN_XML_INDICATOR='TRUE']";
		if (xpath != null)
			value += "[XPATH_VALUE='" + xpath + "']";
		return value;
	}

	/**
	 * 
	 * @return object of Document} created document
	 * @throws ParserConfigurationException
	 */
	protected Document createDocument() throws ParserConfigurationException {
		dbf.setNamespaceAware(true);
		return dbf.newDocumentBuilder().newDocument();
	}

	/**
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of file
	 * @param containerName
	 * @param groupPrefix
	 * @return array of nodes form nodelist
	 */
	protected String[] findGroupings(Document xmlin, String path, String containerName, String groupPrefix) {
		String xpath = startingXPath(path + containerName + "/", groupPrefix);
		try {
			NodeList nodelist = (NodeList) xPath.compile(xpath).evaluate(xmlin, XPathConstants.NODESET);
			Set<String> groupings = new TreeSet<>();
			for (int i = 0; i < nodelist.getLength(); i++) {
				String prefix = groupMatched(containerName, xPath.compile("GROUP_IDENTIFIER").evaluate(nodelist.item(i)));
				if (prefix != null)
					groupings.add(prefix);
			}
			if (!groupings.isEmpty())
				return groupings.toArray(new String[groupings.size()]);
			String[] empty = { groupPrefix };
			return empty;
		} catch (XPathExpressionException e) {
			LOGGER.log(Level.SEVERE, "Error: invalid xPath \"" + xpath + "\"", e);
		}
		return null;
	}

	/**
	 * The function determines if there's a group matching
	 * 
	 * @param containerName
	 * @param groupIdentifier
	 * @return prefix if matched, empty string otherwise
	 */
	private String groupMatched(String containerName, String groupName) {
		if (containerName == null)
			return null;
		int start = groupName.indexOf(containerName);
		if (start == -1)
			return null;
		int end = containerName.length() + start;
		if (end < groupName.length() && groupName.substring(end, end+1).matches("[A-Za-z_]"))
			return null;
		return groupName.substring(0, start);
	}

	/**
	 * This method written for RELATIONSHIP and SIGNATORY containers as per
	 * UCD-113. Need to identify other places to use this method
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of file
	 * @param endString
	 * @param groupPrefix
	 * @return array of nodes form nodelist
	 */
	protected String[] findUniqueGroupings(Document xmlin, String path, String endString, String groupPrefix) {
		String xpath = startingXPath(path + endString + "/", groupPrefix);
		try {
			NodeList nodelist = (NodeList) xPath.compile(xpath).evaluate(xmlin, XPathConstants.NODESET);
			Set<String> groupings = new TreeSet<>();
			for (int i = 0; i < nodelist.getLength(); i++) {
				String str = xPath.compile("GROUP_IDENTIFIER").evaluate(nodelist.item(i));
				if (endString != null && str.indexOf(endString) != -1) {
					groupings.add(str);
				}
			}
			if (!groupings.isEmpty())
				return groupings.toArray(new String[groupings.size()]);
			String[] empty = { groupPrefix };
			return empty;
		} catch (XPathExpressionException e) {
			LOGGER.log(Level.SEVERE, "Error: invalid xPath \"" + xpath + "\"", e);
		}
		return null;
	}

	/**
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param dataPointName
	 *            data point name
	 * @param path
	 *            path of xml
	 * @param group
	 * @return data value
	 */
	protected String getDataValue(Document xmlin, String dataPointName, String path, String group) {
		String xpath = baseXPath(path) + "[DATA_POINT_NAME='" + dataPointName + "']"
				+ (group == null ? "" : "[GROUP_IDENTIFIER[starts-with(.,'" + group + "')]]") + "/DATA_VALUE";
		try {
			String value = xPath.compile(xpath).evaluate(xmlin);
			if (null != dataPointName && null != value && isNumeric(value)) {
				if (docType == DocumentType.ClosingDisclosure && dataPointName.trim().endsWith("Amount"))
					value = String.format(DECIMAL_FORMAT, Double.parseDouble(value));
				else if (dataPointName.trim().endsWith("Percent"))
					value = String.format(PERCENT_FORMAT, Double.parseDouble(value));
			}
			return value;
		} catch (XPathExpressionException e) {
			LOGGER.log(Level.SEVERE, "Error: invalid xPath \"" + xpath + "\"", e);
		}
		return null;
	}

	/**
	 * 
	 * @param s
	 * @return BOOLEAN value true or false
	 */
	private boolean isNumeric(String s) {
		return s.matches("[-+]?\\d*\\.?\\d+");
	}

	/**
	 * 
	 * @param xmlout
	 *            object of Document} output xml
	 * @param parentElement
	 *            parent node
	 * @param dataPointName
	 *            the data point name
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml
	 * @param group
	 */
	protected void insertAttributeValue(Document xmlout, Element parentElement, String dataPointName, Document xmlin,
			String path, String group) {
		if (parentElement != null) {
			String str = getDataValue(xmlin, dataPointName, path, group);
			if (str != null && !str.isEmpty()) {
				parentElement.setAttribute(dataPointName, str);
			}
		}
	}

	/**
	 * Method used to insert attribute value
	 * 
	 * @param xmlout
	 *            object of Document} output xml
	 * @param parentElement
	 *            parent node
	 * @param dataPointName
	 *            the data point name
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml
	 */
	protected void insertAttributeValue(Document xmlout, Element parentElement, String dataPointName, Document xmlin,
			String path) {
		insertAttributeValue(xmlout, parentElement, dataPointName, xmlin, path, null);
	}

	/**
	 * Method used to insert ASSET_DETAIL value
	 * 
	 * @param xmlin
	 * @param path
	 * @param group
	 * @param xmlout
	 * @param parentElement
	 */
	protected void insertAssetDetail(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "AssetType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "AssetTypeOtherDescription", xmlin, path, group);
	}

	/**
	 * Method is used to insert data value
	 * 
	 * @param xmlout
	 *            object of Document} output xml
	 * @param parentElement
	 *            parent node
	 * @param dataPointName
	 *            the data point name
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml
	 * @param group
	 * @return object of Element}
	 */

	protected Element insertDataValue(Document xmlout, Element parentElement, String dataPointName, Document xmlin,
			String path, String group) {
		return insertDataValue(xmlout, parentElement, dataPointName, xmlin, path, group,
				getDataValue(xmlin, dataPointName, path, group));
	}

	/**
	 * Method is used to insert data value
	 * 
	 * @param xmlout
	 *            object of Document} output xml
	 * @param parentElement
	 *            parent node
	 * @param dataPointName
	 *            the data point name
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml
	 * @param group
	 * @param str
	 *            data value
	 * @return object of Element}
	 */
	protected Element insertDataValue(Document xmlout, Element parentElement, String dataPointName, Document xmlin,
			String path, String group, String str) {
		Element element = null;
		if (str != null && !str.isEmpty()) {
			element = (Element) parentElement.appendChild(xmlout.createElement(addNamespace(dataPointName)));
			if (Objects.equals("AggregateAdjustment", dataPointName))
				element.appendChild(xmlout.createTextNode(str));
			else
				element.appendChild(xmlout.createTextNode(toAbsolute(str)));
		}
		return element;
	}

	/**
	 * Method used to insert levels
	 * 
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parenet node from xml
	 * @param path
	 *            location of xml file
	 * @return objet of Element}
	 */
	protected Element insertLevels(Document xmlout, Element parentElement, String path) {
		Element parent = parentElement;
		for (String container : path.split("/"))
			parent = (Element) parent.appendChild(xmlout.createElement(addNamespace(container)));
		return parent;
	}

	/**
	 * 
	 * @param xpath
	 *            xpath value from xml file
	 * @param groupPrefix
	 *            groupd prefix from xml
	 * @return value of starting xpath
	 */
	protected String startingXPath(String xpath, String groupPrefix) {
		String value = "/UCDData/DataElement[INCLUDE_IN_XML_INDICATOR='TRUE']";
		if (xpath != null)
			value += "[XPATH_VALUE[starts-with(.,'" + xpath + "')]]";
		if (groupPrefix != null && !groupPrefix.isEmpty())
			value += "[GROUP_IDENTIFIER[starts-with(.,'" + groupPrefix + "')]]";
		return value;
	}

	protected String toAbsolute(String str) {
		if (!convertToAbsolute())
			return str;
		String s = str;
		try {
			double d = Double.parseDouble(s);
			if (d < 0) // Only convert if absolutely necessary
				s = String.valueOf(Math.abs(d));
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Cast of string to absolute failed", e);
		}
		return s;
	}

	/**
	 * Method used to insert about version
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node
	 */
	protected void insertAboutVersion(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		String filepath = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toString())
				.getParent() + "\\" + fileName;
		filepath = filepath.replaceAll("file:\\\\", "");
		String versionFromFile = "Not available";
		try {
			FileReader fileReader = new FileReader(new File(filepath));
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			versionFromFile = bufferedReader.readLine();
			fileReader.close();
		} catch (FileNotFoundException e) {
			LOGGER.log(Level.WARNING, "Unable to open File" + fileName + "");
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Error reading file" + fileName + "");
			e.printStackTrace();
		}

		String version = getDataValue(xmlin, "AboutVersionIdentifier", path, group);
		Element element = insertDataValue(xmlout, parentElement, "AboutVersionIdentifier", xmlin, path, group,
				version + (version.startsWith("TRIDenToolKit") ? ", TransformX UCD v" + versionFromFile : ""));
		insertAttributeValue(xmlout, element, "IdentifierOwnerURI", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "CreatedDatetime", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "DataVersionIdentifier", xmlin, path, group);
	}

	/**
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            path of xml
	 * @param groupPrefix
	 *            group prefix from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node
	 */
	protected void insertAboutVersions(Document xmlin, String path, String groupPrefix, Document xmlout,
			Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "ABOUT_VERSION", groupPrefix);
		for (String group : groupings)
			insertAboutVersion(xmlin, path + "ABOUT_VERSION/", group, xmlout,
					insertLevels(xmlout, parentElement, "ABOUT_VERSION"));
	}

	/**
	 * Method is used to insert address in xml file
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node
	 */
	protected void insertAddress(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "AddressLineText", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "AddressType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "AddressUnitDesignatorType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "AddressUnitDesignatorTypeOtherDescription", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "AddressUnitIdentifier", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "CityName", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "CountryCode", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PostalCode", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "StateCode", xmlin, path, group);
	}

	/**
	 * Method used to insert adjustment
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node
	 */
	protected void insertAdjustment(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertInterestRateAdjustment(xmlin, path + "INTEREST_RATE_ADJUSTMENT/", group, xmlout,
				insertLevels(xmlout, parentElement, "INTEREST_RATE_ADJUSTMENT"));
		insertPrincipalAndInterestPaymentAdjustment(xmlin, path + "PRINCIPAL_AND_INTEREST_PAYMENT_ADJUSTMENT/", group,
				xmlout, insertLevels(xmlout, parentElement, "PRINCIPAL_AND_INTEREST_PAYMENT_ADJUSTMENT"));
	}

	/**
	 * Method used to insert Amortization Rule
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node
	 */
	protected void insertAmortizationRule(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "AmortizationType", xmlin, path, group);
	}

	/**
	 * Method used to insert Addresses
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param groupprefix
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node
	 */
	protected void insertAddresses(Document xmlin, String path, String groupPrefix, Document xmlout,
			Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "ADDRESS", groupPrefix);
		for (String group : groupings)
			insertAddress(xmlin, path + "ADDRESS/", group, xmlout, insertLevels(xmlout, parentElement, "ADDRESS"));
	}

	/**
	 * Method used to insert AuditTrail
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node
	 */

	protected void insertAuditTrail(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertAuditTrailEntries(xmlin, path + "AUDIT_TRAIL_ENTRIES/", group, xmlout,
				insertLevels(xmlout, parentElement, "AUDIT_TRAIL_ENTRIES"));
	}

	/**
	 * Method used to insert AuditTrail Entries
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node
	 */
	protected void insertAuditTrailEntries(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertAuditTrailEntry(xmlin, path + "AUDIT_TRAIL_ENTRY/", group, xmlout,
				insertLevels(xmlout, parentElement, "AUDIT_TRAIL_ENTRY"));
	}

	/**
	 * Method used to insert AuditTrail Entry
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node
	 */
	protected void insertAuditTrailEntry(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertAuditTrailEntryDetail(xmlin, path + "AUDIT_TRAIL_ENTRY_DETAIL/", group, xmlout,
				insertLevels(xmlout, parentElement, "AUDIT_TRAIL_ENTRY_DETAIL"));
	}

	/**
	 * Method used to insert AuditTrail Entry detail
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node
	 */
	protected void insertAuditTrailEntryDetail(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "EntryDatetime", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "EventType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "EventTypeOtherDescription", xmlin, path, group);
	}

	/**
	 * Method used to insert AutomatedUnderwriting
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node
	 */
	protected void insertAutomatedUnderwriting(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "AutomatedUnderwritingCaseIdentifier", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "AutomatedUnderwritingRecommendationDescription", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "AutomatedUnderwritingSystemType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "AutomatedUnderwritingSystemTypeOtherDescription", xmlin, path, group);
	}

	/**
	 * Method used to insert AutomatedUnderwritings
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertAutomatedUnderwritings(Document xmlin, String path, String groupPrefix, Document xmlout,
			Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "AUTOMATED_UNDERWRITING", groupPrefix);
		for (String group : groupings)
			insertAutomatedUnderwriting(xmlin, path + "AUTOMATED_UNDERWRITING/", group, xmlout,
					insertLevels(xmlout, parentElement, "AUTOMATED_UNDERWRITING"));
	}

	/**
	 * Method used to insert borrower
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node
	 */
	protected void insertBorrower(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertBorrowerDetail(xmlin, path + "BORROWER_DETAIL/", group, xmlout,
				insertLevels(xmlout, parentElement, "BORROWER_DETAIL"));
		insertGovernmentMonitoring(xmlin, path + "GOVERNMENT_MONITORING/", group, xmlout,
				insertLevels(xmlout, parentElement, "GOVERNMENT_MONITORING"));
	}

	/**
	 * Method used to insert borrower detail
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertBorrowerDetail(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "BorrowerIsAnIndividualPersonIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "BorrowerClassificationType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "BorrowerAgeAtApplicationYearsCount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "BorrowerQualifyingIncomeAmount", xmlin, path, group);
	}

	/**
	 * Method used to insert Buydown
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertBuydown(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertBuydownOccurences(xmlin, path + "BUYDOWN_OCCURRENCES/", group, xmlout,
				insertLevels(xmlout, parentElement, "BUYDOWN_OCCURRENCES"));
		insertBuydownRule(xmlin, path + "BUYDOWN_RULE/", group, xmlout,
				insertLevels(xmlout, parentElement, "BUYDOWN_RULE"));
	}

	/**
	 * Method used to insert Buydown Occurence
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertBuydownOccurence(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "BuydownInitialEffectiveInterestRatePercent", xmlin, path, group);
	}

	/**
	 * Method used to insert Buydown Occurences
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertBuydownOccurences(Document xmlin, String path, String groupPrefix, Document xmlout,
			Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "BUYDOWN_OCCURRENCE", groupPrefix);
		for (String group : groupings)
			insertBuydownOccurence(xmlin, path + "BUYDOWN_OCCURRENCE/", group, xmlout,
					insertLevels(xmlout, parentElement, "BUYDOWN_OCCURRENCE"));
	}

	/**
	 * Method used to insert Buydown Rule
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertBuydownRule(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "BuydownChangeFrequencyMonthsCount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "BuydownDurationMonthsCount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "BuydownIncreaseRatePercent", xmlin, path, group);
		insertExtension(xmlin, path + "EXTENSION/", group, xmlout, insertLevels(xmlout, parentElement, "EXTENSION"));
	}

	/**
	 * Method used to insert Cash To Close Item
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertCashToCloseItem(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureCashToCloseItemAmountChangedIndicator", xmlin, path,
				group);
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureCashToCloseItemChangeDescription", xmlin, path,
				group);
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureCashToCloseItemEstimatedAmount", xmlin, path,
				group);
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureCashToCloseItemFinalAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureCashToCloseItemPaymentType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureCashToCloseItemType", xmlin, path, group);
	}

	/**
	 * Method used to insert Cash To Close Items
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertCashToCloseItems(Document xmlin, String path, String groupPrefix, Document xmlout,
			Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "CASH_TO_CLOSE_ITEM", groupPrefix);
		for (String group : groupings)
			insertCashToCloseItem(xmlin, path + "CASH_TO_CLOSE_ITEM/", group, xmlout,
					insertLevels(xmlout, parentElement, "CASH_TO_CLOSE_ITEM"));
	}

	/**
	 * Method used to insert census information
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertCensusInformation(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "CensusTractIdentifier", xmlin, path, group);
	}

	/**
	 * Method used to insert Closing Adjustment Item
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertClosingAdjustmentItem(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertClosingAdjustmentItemDetail(xmlin, path + "CLOSING_ADJUSTMENT_ITEM_DETAIL/", group, xmlout,
				insertLevels(xmlout, parentElement, "CLOSING_ADJUSTMENT_ITEM_DETAIL"));
		insertClosingAdjustmentItemPaidBy(xmlin, path + "CLOSING_ADJUSTMENT_ITEM_PAID_BY/", group, xmlout,
				insertLevels(xmlout, parentElement, "CLOSING_ADJUSTMENT_ITEM_PAID_BY"));
		insertLegalEntityDetail(xmlin,
				path + "EXTENSION/OTHER/gse:CLOSING_ADJUSTMENT_ITEM_PAID_TO/LEGAL_ENTITY/LEGAL_ENTITY_DETAIL/", group,
				xmlout, insertLevels(xmlout, parentElement,
						"EXTENSION/OTHER/gse:CLOSING_ADJUSTMENT_ITEM_PAID_TO/LEGAL_ENTITY/LEGAL_ENTITY_DETAIL"));
	}

	/**
	 * Method used to insert Closing Adjustment Item
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertClosingAdjustmentItemDetail(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "ClosingAdjustmentItemAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ClosingAdjustmentItemPaidOutsideOfClosingIndicator", xmlin, path,
				group);
		Element closingAdjustmentItemTypeElement = insertDataValue(xmlout, parentElement, "ClosingAdjustmentItemType",
				xmlin, path, group);
		insertAttributeValue(xmlout, closingAdjustmentItemTypeElement, "gse:DisplayLabelText", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ClosingAdjustmentItemTypeOtherDescription", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureSectionType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureSubsectionType", xmlin, path, group);
	}

	/**
	 * Method used to insert Closing Adjustment Item paid by
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertClosingAdjustmentItemPaidBy(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertLegalEntity(xmlin, path + "LEGAL_ENTITY/", group, xmlout,
				insertLevels(xmlout, parentElement, "LEGAL_ENTITY"));
		insertIndividual(xmlin, path + "INDIVIDUAL/", group, xmlout, insertLevels(xmlout, parentElement, "INDIVIDUAL"));
	}

	/**
	 * Method used to insert Closing Adjustment Item
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param groupprefix
	 *            groupprefix from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertClosingAdjustmentItems(Document xmlin, String path, String groupPrefix, Document xmlout,
			Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "CLOSING_ADJUSTMENT_ITEM", groupPrefix);
		for (String group : groupings)
			insertClosingAdjustmentItem(xmlin, path + "CLOSING_ADJUSTMENT_ITEM/", group, xmlout,
					insertLevels(xmlout, parentElement, "CLOSING_ADJUSTMENT_ITEM"));
	}

	/**
	 * Method used to insert Closing Cost Fund
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertClosingCostFund(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "ClosingCostFundAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "FundsType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureSectionType", xmlin, path, group);
	}

	/**
	 * Method used to insert Closing Cost Funds
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param groupprefix
	 *            groupprefix from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertClosingCostFunds(Document xmlin, String path, String groupPrefix, Document xmlout,
			Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "CLOSING_COST_FUND", groupPrefix);
		for (String group : groupings)
			insertClosingCostFund(xmlin, path + "CLOSING_COST_FUND/", group, xmlout,
					insertLevels(xmlout, parentElement, "CLOSING_COST_FUND"));
	}

	/**
	 * Method used to insert Closing Information
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertClosingInformation(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertClosingAdjustmentItems(xmlin, path + "CLOSING_ADJUSTMENT_ITEMS/", group, xmlout,
				insertLevels(xmlout, parentElement, "CLOSING_ADJUSTMENT_ITEMS"));
		insertClosingCostFunds(xmlin, path + "CLOSING_COST_FUNDS/", group, xmlout,
				insertLevels(xmlout, parentElement, "CLOSING_COST_FUNDS"));
		insertClosingInformationDetail(xmlin, path + "CLOSING_INFORMATION_DETAIL/", group, xmlout,
				insertLevels(xmlout, parentElement, "CLOSING_INFORMATION_DETAIL"));
		insertPrepaidItems(xmlin, path + "PREPAID_ITEMS/", group, xmlout,
				insertLevels(xmlout, parentElement, "PREPAID_ITEMS"));
		insertProrationItems(xmlin, path + "PRORATION_ITEMS/", group, xmlout,
				insertLevels(xmlout, parentElement, "PRORATION_ITEMS"));
	}

	/**
	 * Method used to insert Closing Information details
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertClosingInformationDetail(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "CashFromBorrowerAtClosingAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "CashFromSellerAtClosingAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "CashToBorrowerAtClosingAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "CashToSellerAtClosingAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ClosingAgentOrderNumberIdentifier", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ClosingDate", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ClosingRateSetDate", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "CurrentRateSetDate", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "DisbursementDate", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "DocumentOrderClassificationType", xmlin, path, group);
	}

	/**
	 * Method used to insert Collateral
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertCollateral(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertPledgedAsset(xmlin, path + "PLEDGED_ASSET/", group, xmlout,
				insertLevels(xmlout, parentElement, "PLEDGED_ASSET"));
		insertSubjectProperty(xmlin, path + "SUBJECT_PROPERTY/", group, xmlout,
				insertLevels(xmlout, parentElement, "SUBJECT_PROPERTY"));
	}

	/**
	 * Method used to insert Collaterals
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertCollaterals(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertCollateral(xmlin, path + "COLLATERAL/", group, xmlout, insertLevels(xmlout, parentElement, "COLLATERAL"));
	}

	/**
	 * Method used to insert combined LTV
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertCombinedLtv(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "CombinedLTVRatioPercent", xmlin, path, group);
	}

	/**
	 * Method used to insert combined LTVs
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param groupprefix
	 *            group prefix from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertCombinedLtvs(Document xmlin, String path, String groupPrefix, Document xmlout,
			Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "COMBINED_LTV", groupPrefix);
		for (String group : groupings)
			insertCombinedLtv(xmlin, path + "COMBINED_LTV/", group, xmlout,
					insertLevels(xmlout, parentElement, "COMBINED_LTV"));
	}

	/**
	 * Method used to insert Construction
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertConstruction(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "ConstructionLoanTotalTermMonthsCount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ConstructionLoanType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ConstructionPeriodNumberOfMonthsCount", xmlin, path, group);
	}

	/**
	 * Method used to insert Contact point
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertContactPoint(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertContactPointEmail(xmlin, path + "CONTACT_POINT_EMAIL/", group, xmlout,
				insertLevels(xmlout, parentElement, "CONTACT_POINT_EMAIL"));
		insertContactPointTelephone(xmlin, path + "CONTACT_POINT_TELEPHONE/", group, xmlout,
				insertLevels(xmlout, parentElement, "CONTACT_POINT_TELEPHONE"));
	}

	/**
	 * Method used to insert Contact point email
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertContactPointEmail(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "ContactPointEmailValue", xmlin, path, group);
	}

	/**
	 * Method used to insert Contact point telephone
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertContactPointTelephone(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "ContactPointTelephoneValue", xmlin, path, group);
	}

	/**
	 * Method used to insert Contact points
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param groupprefix
	 *            groupprefix from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertContactPoints(Document xmlin, String path, String groupPrefix, Document xmlout,
			Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "CONTACT_POINT", groupPrefix);
		for (String group : groupings)
			insertContactPoint(xmlin, path + "CONTACT_POINT/", group, xmlout,
					insertLevels(xmlout, parentElement, "CONTACT_POINT"));
	}

	/**
	 * Method used to insert deal
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertDeal(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertCollaterals(xmlin, path + "COLLATERALS/", group, xmlout,
				insertLevels(xmlout, parentElement, "COLLATERALS"));
		insertLiabilities(xmlin, path + "LIABILITIES/", group, xmlout,
				insertLevels(xmlout, parentElement, "LIABILITIES")); // None of
																		// these
																		// in LE
		insertLoans(xmlin, path + "LOANS/", group, xmlout, insertLevels(xmlout, parentElement, "LOANS"));
		insertParties(xmlin, path + "PARTIES/", group, xmlout, insertLevels(xmlout, parentElement, "PARTIES"));
		// insertRelationships(xmlin, path + "RELATIONSHIPS/", group, xmlout,
		// insertLevels(xmlout, parentElement, "RELATIONSHIPS")); // None of
		// these in LE
	}

	/**
	 * Method used to insert deals
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertDeals(Document xmlin, String path, String groupPrefix, Document xmlout,
			Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "DEAL", groupPrefix);
		for (String group : groupings)
			insertDeal(xmlin, path + "DEAL/", group, xmlout, insertLevels(xmlout, parentElement, "DEAL"));
	}

	/**
	 * Method used to insert deal set
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertDealSet(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDeals(xmlin, path + "DEALS/", group, xmlout, insertLevels(xmlout, parentElement, "DEALS"));
	}

	/**
	 * Method used to insert deal sets
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertDealSets(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDealSet(xmlin, path + "DEAL_SET/", group, xmlout, insertLevels(xmlout, parentElement, "DEAL_SET"));
		insertDealSetServices(xmlin, path + "DEAL_SET_SERVICES/", group, xmlout,
				insertLevels(xmlout, parentElement, "DEAL_SET_SERVICES"));
		insertParties(xmlin, path + "PARTIES/", group, xmlout, insertLevels(xmlout, parentElement, "PARTIES"));
	}

	/**
	 * Method used to insert deal set
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertDealSetService(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertReportingInformation(xmlin, path + "REPORTING_INFORMATION/", group, xmlout,
				insertLevels(xmlout, parentElement, "REPORTING_INFORMATION"));
	}

	/**
	 * Method used to insert deal set services
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertDealSetServices(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "DEAL_SET_SERVICE", group);
		for (String g : groupings)
			insertDealSetService(xmlin, path + "DEAL_SET_SERVICE/", g, xmlout,
					insertLevels(xmlout, parentElement, "DEAL_SET_SERVICE"));
	}

	/**
	 * Method used to insert documents
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertDocument(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertAttributeValue(xmlout, parentElement, "MISMOReferenceModelIdentifier", xmlin, path);
		insertAuditTrail(xmlin, path + "AUDIT_TRAIL/", group, xmlout,
				insertLevels(xmlout, parentElement, "AUDIT_TRAIL"));
		insertDealSets(xmlin, path + "DEAL_SETS/", group, xmlout, insertLevels(xmlout, parentElement, "DEAL_SETS"));
		insertRelationships(xmlin, path + "RELATIONSHIPS/", group, xmlout,
				insertLevels(xmlout, parentElement, "RELATIONSHIPS"));
		insertSignatories(xmlin, path + "SIGNATORIES/", group, xmlout,
				insertLevels(xmlout, parentElement, "SIGNATORIES"));
		insertSystemSignatures(xmlin, path + "SYSTEM_SIGNATORIES/", group, xmlout,
				insertLevels(xmlout, parentElement, "SYSTEM_SIGNATORIES"));
		insertViews(xmlin, path + "VIEWS/", group, xmlout, insertLevels(xmlout, parentElement, "VIEWS"));
		insertAboutVersions(xmlin, path + "ABOUT_VERSIONS/", group, xmlout,
				insertLevels(xmlout, parentElement, "ABOUT_VERSIONS"));
		insertDocumentClassification(xmlin, path + "DOCUMENT_CLASSIFICATION/", group, xmlout,
				insertLevels(xmlout, parentElement, "DOCUMENT_CLASSIFICATION"));
	}

	/**
	 * Method used to insert document class
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertDocumentClass(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "DocumentType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "DocumentTypeOtherDescription", xmlin, path, group);
	}

	/**
	 * Method used to insert document classes
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param groupprefix
	 *            groupprefix from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertDocumentClasses(Document xmlin, String path, String groupPrefix, Document xmlout,
			Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "DOCUMENT_CLASS", groupPrefix);
		for (String group : groupings)
			insertDocumentClass(xmlin, path + "DOCUMENT_CLASS/", group, xmlout,
					insertLevels(xmlout, parentElement, "DOCUMENT_CLASS"));
	}

	/**
	 * Method used to insert document classification
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertDocumentClassification(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDocumentClasses(xmlin, path + "DOCUMENT_CLASSES/", group, xmlout,
				insertLevels(xmlout, parentElement, "DOCUMENT_CLASSES"));
		insertDocumentClassificationDetail(xmlin, path + "DOCUMENT_CLASSIFICATION_DETAIL/", group, xmlout,
				insertLevels(xmlout, parentElement, "DOCUMENT_CLASSIFICATION_DETAIL"));
	}

	/**
	 * Method used to insert document classification detail
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertDocumentClassificationDetail(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "DocumentFormIssuingEntityNameType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "DocumentFormIssuingEntityVersionIdentifier", xmlin, path, group);
		insertExtension(xmlin, path + "EXTENSION/", group, xmlout, insertLevels(xmlout, parentElement, "EXTENSION"));
	}

	/**
	 * Method used to insert documents
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param groupprefix
	 *            groupprefix from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertDocuments(Document xmlin, String path, String groupPrefix, Document xmlout,
			Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "DOCUMENT", groupPrefix);
		for (String group : groupings)
			insertDocument(xmlin, path + "DOCUMENT/", group, xmlout, insertLevels(xmlout, parentElement, "DOCUMENT"));
	}

	/**
	 * Method used to insert document set
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */

	protected void insertDocumentSet(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDocuments(xmlin, path + "DOCUMENTS/", group, xmlout, insertLevels(xmlout, parentElement, "DOCUMENTS"));
	}

	/**
	 * Method used to insert document sets
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertDocumentSets(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDocumentSet(xmlin, path + "DOCUMENT_SET/", group, xmlout,
				insertLevels(xmlout, parentElement, "DOCUMENT_SET"));
	}

	/**
	 * Method used to insert document specific data set
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertDocumentSpecificDataSet(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertExecution(xmlin, path + "EXECUTION/", group, xmlout, insertLevels(xmlout, parentElement, "EXECUTION"));
		insertIntegratedDisclosure(xmlin, path + "INTEGRATED_DISCLOSURE/", group, xmlout,
				insertLevels(xmlout, parentElement, "INTEGRATED_DISCLOSURE"));
		insertURLA(xmlin, path + "URLA/", group, xmlout, insertLevels(xmlout, parentElement, "URLA"));
	}

	/**
	 * Method used to insert Escrow
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */

	protected void insertEscrow(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertEscrowDetail(xmlin, path + "ESCROW_DETAIL/", group, xmlout,
				insertLevels(xmlout, parentElement, "ESCROW_DETAIL"));
		insertEscrowItems(xmlin, path + "ESCROW_ITEMS/", group, xmlout,
				insertLevels(xmlout, parentElement, "ESCROW_ITEMS"));
	}

	/**
	 * Method used to insert escrow details
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertEscrowDetail(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "EscrowAggregateAccountingAdjustmentAmount", xmlin, path, group);
	}

	/**
	 * Method used to insert escrow item
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertEscrowItem(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertEscrowItemDetail(xmlin, path + "ESCROW_ITEM_DETAIL/", group, xmlout,
				insertLevels(xmlout, parentElement, "ESCROW_ITEM_DETAIL"));
		insertEscrowItemPayments(xmlin, path + "ESCROW_ITEM_PAYMENTS/", group, xmlout,
				insertLevels(xmlout, parentElement, "ESCROW_ITEM_PAYMENTS"));
	}

	/**
	 * Method used to insert escrow item detail
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertEscrowItemDetail(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "EscrowCollectedNumberOfMonthsCount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "EscrowItemCategoryType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "EscrowItemEstimatedTotalAmount", xmlin, path, group);
		Element escrowItemTypeElement = insertDataValue(xmlout, parentElement, "EscrowItemType", xmlin, path, group);
		insertAttributeValue(xmlout, escrowItemTypeElement, "gse:DisplayLabelText", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "EscrowItemTypeOtherDescription", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "EscrowMonthlyPaymentAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "FeePaidToType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "FeePaidToTypeOtherDescription", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureSectionType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "RegulationZPointsAndFeesIndicator", xmlin, path, group);
		insertExtension(xmlin, path + "EXTENSION/", group, xmlout, insertLevels(xmlout, parentElement, "EXTENSION"));
	}

	/**
	 * Method used to insert escrow item payment
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertEscrowItemPayment(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "EscrowItemActualPaymentAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "EscrowItemPaymentPaidByType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "EscrowItemPaymentTimingType", xmlin, path, group);
	}

	/**
	 * Method used to insert escrow item payments
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertEscrowItemPayments(Document xmlin, String path, String groupPrefix, Document xmlout,
			Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "ESCROW_ITEM_PAYMENT", groupPrefix);
		for (String group : groupings)
			insertEscrowItemPayment(xmlin, path + "ESCROW_ITEM_PAYMENT/", group, xmlout,
					insertLevels(xmlout, parentElement, "ESCROW_ITEM_PAYMENT"));
	}

	/**
	 * Method used to insert escrow items
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertEscrowItems(Document xmlin, String path, String groupPrefix, Document xmlout,
			Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "ESCROW_ITEM", groupPrefix);
		for (String group : groupings)
			insertEscrowItem(xmlin, path + "ESCROW_ITEM/", group, xmlout,
					insertLevels(xmlout, parentElement, "ESCROW_ITEM"));
	}

	/**
	 * Method used to insert Estimated Property Cost
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertEstimatedPropertyCost(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertEstimatedPropertyCostComponents(xmlin, path + "ESTIMATED_PROPERTY_COST_COMPONENTS/", group, xmlout,
				insertLevels(xmlout, parentElement, "ESTIMATED_PROPERTY_COST_COMPONENTS"));
		insertEstimatedPropertyCostDetail(xmlin, path + "ESTIMATED_PROPERTY_COST_DETAIL/", group, xmlout,
				insertLevels(xmlout, parentElement, "ESTIMATED_PROPERTY_COST_DETAIL"));
	}

	/**
	 * Method used to insert Estimated Property Cost component
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertEstimatedPropertyCostComponent(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "ProjectedPaymentEscrowedType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ProjectedPaymentEstimatedTaxesInsuranceAssessmentComponentType", xmlin,
				path, group);
		insertDataValue(xmlout, parentElement,
				"ProjectedPaymentEstimatedTaxesInsuranceAssessmentComponentTypeOtherDescription", xmlin, path, group);
	}

	/**
	 * Method used to insert Estimated Property Cost components
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertEstimatedPropertyCostComponents(Document xmlin, String path, String groupPrefix,
			Document xmlout, Element parentElement) {
		insertAttributeValue(xmlout, parentElement, "gse:DisplayLabelText", xmlin, path, groupPrefix);
		String[] groupings = findGroupings(xmlin, path, "ESTIMATED_PROPERTY_COST_COMPONENT", groupPrefix);
		for (String group : groupings)
			insertEstimatedPropertyCostComponent(xmlin, path + "ESTIMATED_PROPERTY_COST_COMPONENT/", group, xmlout,
					insertLevels(xmlout, parentElement, "ESTIMATED_PROPERTY_COST_COMPONENT"));
	}

	/**
	 * Method used to insert Estimated Property Cost detail
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertEstimatedPropertyCostDetail(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "ProjectedPaymentEstimatedTaxesInsuranceAssessmentTotalAmount", xmlin,
				path, group);
	}

	/**
	 * Method used to insert execution
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertExecution(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertExecutionDetail(xmlin, path + "EXECUTION_DETAIL/", group, xmlout,
				insertLevels(xmlout, parentElement, "EXECUTION_DETAIL"));
	}

	/**
	 * Method used to insert execution detail
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertExecutionDetail(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "ActualSignatureType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ActualSignatureTypeOtherDescription", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ExecutionDate", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ExecutionDatetime", xmlin, path, group);
	}

	/**
	 * Method used to insert exemption
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertExemption(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "AbilityToRepayExemptionReasonType", xmlin, path, group);
	}

	/**
	 * Method used to insert extension
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertExtension(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertMismo(xmlin, path + "MISMO/", group, xmlout, insertLevels(xmlout, parentElement, "MISMO"));
		insertOther(xmlin, path + "OTHER/", group, xmlout, insertLevels(xmlout, parentElement, "OTHER"));
	}

	/**
	 * Method used to insert fee
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */

	protected void insertFee(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertFeeDetail(xmlin, path + "FEE_DETAIL/", group, xmlout, insertLevels(xmlout, parentElement, "FEE_DETAIL"));
		insertFeePaidTo(xmlin, path + "FEE_PAID_TO/", group, xmlout,
				insertLevels(xmlout, parentElement, "FEE_PAID_TO"));
		insertFeePayments(xmlin, path + "FEE_PAYMENTS/", group, xmlout,
				insertLevels(xmlout, parentElement, "FEE_PAYMENTS"));
	}

	/**
	 * Method used to insert fee detail
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertFeeDetail(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "BorrowerChosenProviderIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "FeeActualTotalAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "FeeEstimatedTotalAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "FeePaidToType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "FeePaidToTypeOtherDescription", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "FeePercentBasisType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "FeeTotalPercent", xmlin, path, group);
		Element feeTypeElement = insertDataValue(xmlout, parentElement, "FeeType", xmlin, path, group);
		insertAttributeValue(xmlout, feeTypeElement, "gse:DisplayLabelText", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "FeeTypeOtherDescription", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureSectionType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "OptionalCostIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "RegulationZPointsAndFeesIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "RequiredProviderOfServiceIndicator", xmlin, path, group);
		insertExtension(xmlin, path + "EXTENSION/", group, xmlout, insertLevels(xmlout, parentElement, "EXTENSION"));
	}

	/**
	 * Method used to insert fee information
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertFeeInformation(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertFees(xmlin, path + "FEES/", group, xmlout, insertLevels(xmlout, parentElement, "FEES"));
		insertFeeSummaryDetail(xmlin, path + "FEES_SUMMARY/FEE_SUMMARY_DETAIL/", group, xmlout,
				insertLevels(xmlout, parentElement, "FEES_SUMMARY/FEE_SUMMARY_DETAIL"));
	}

	/**
	 * Method used to insert fee paid to
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertFeePaidTo(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertLegalEntity(xmlin, path + "LEGAL_ENTITY/", group, xmlout,
				insertLevels(xmlout, parentElement, "LEGAL_ENTITY"));
	}

	/**
	 * Method used to insert fee payment
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertFeePayment(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "FeeActualPaymentAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "FeePaymentPaidByType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "FeePaymentPaidOutsideOfClosingIndicator", xmlin, path, group);
	}

	/**
	 * Method used to insert fee payments
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param groupprefix
	 *            group prefix from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertFeePayments(Document xmlin, String path, String groupPrefix, Document xmlout,
			Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "FEE_PAYMENT", groupPrefix);
		for (String group : groupings)
			insertFeePayment(xmlin, path + "FEE_PAYMENT/", group, xmlout,
					insertLevels(xmlout, parentElement, "FEE_PAYMENT"));
	}

	/**
	 * Method used to insert fees
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param groupprefix
	 *            group prefix from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertFees(Document xmlin, String path, String groupPrefix, Document xmlout, Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "FEE", groupPrefix);
		for (String group : groupings)
			insertFee(xmlin, path + "FEE/", group, xmlout, insertLevels(xmlout, parentElement, "FEE"));
	}

	/**
	 * Method used to FIPS information
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertFipsInformation(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "FIPSCountyCode", xmlin, path, group);
	}

	/**
	 * Method used to insert fee summary detail
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */

	protected void insertFeeSummaryDetail(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "APRPercent", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "FeeSummaryTotalAmountFinancedAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "FeeSummaryTotalFinanceChargeAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "FeeSummaryTotalInterestPercent", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "FeeSummaryTotalOfAllPaymentsAmount", xmlin, path, group);
	}

	/**
	 * Method used to insert foreclosure
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertForeclosure(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertForeclosureDetail(xmlin, path + "FORECLOSURE_DETAIL/", group, xmlout,
				insertLevels(xmlout, parentElement, "FORECLOSURE_DETAIL"));
	}

	/**
	 * Method used to insert foreclosure detail
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertForeclosureDetail(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "DeficiencyRightsPreservedIndicator", xmlin, path, group);
	}

	/**
	 * Method used to insert foreclosures
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param groupprefix
	 *            group prefix from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertForeclosures(Document xmlin, String path, String groupPrefix, Document xmlout,
			Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "FORECLOSURE", groupPrefix);
		for (String group : groupings)
			insertForeclosure(xmlin, path + "FORECLOSURE/", group, xmlout,
					insertLevels(xmlout, parentElement, "FORECLOSURE"));
	}

	/**
	 * Method used to insert foreign object
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertForeignObject(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertLevels(xmlout, parentElement, "EmbeddedContentXML"); // Placeholder
																	// for
																	// Base64
																	// document
		parentElement.appendChild(xmlout.createElement(addNamespace("MIMETypeIdentifier")))
				.appendChild(xmlout.createTextNode("application/pdf"));
		parentElement.appendChild(xmlout.createElement(addNamespace("ObjectEncodingType")))
				.appendChild(xmlout.createTextNode("Base64"));
		parentElement.appendChild(xmlout.createElement(addNamespace("ObjectName")))
				.appendChild(xmlout.createTextNode("ClosingDisclosure.pdf"));
	}

	/**
	 * Method used to insert borrower
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node
	 */
	protected void insertGovernmentMonitoring(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertGovernmentMonitoringDetail(xmlin, path + "GOVERNMENT_MONITORING_DETAIL/", group, xmlout,
				insertLevels(xmlout, parentElement, "GOVERNMENT_MONITORING_DETAIL"));
		insertHmdaEthnicityOrigins(xmlin, path + "HMDA_ETHNICITY_ORIGINS/", group, xmlout,
				insertLevels(xmlout, parentElement, "HMDA_ETHNICITY_ORIGINS"));
		insertHmdaRaces(xmlin, path + "HMDA_RACES/", group, xmlout, insertLevels(xmlout, parentElement, "HMDA_RACES"));
	}

	/**
	 * Method used to insert HELOC rule
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertGovernmentMonitoringDetail(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "HMDAEthnicityType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "HMDAEthnicityRefusalIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "HMDAEthnicityCollectedBasedOnVisualObservationOrSurnameIndicator",
				xmlin, path, group);
		insertDataValue(xmlout, parentElement, "HMDARaceRefusalIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "HMDARaceCollectedBasedOnVisualObservationOrSurnameIndicator", xmlin,
				path, group);
		insertDataValue(xmlout, parentElement, "GenderType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "HMDAGenderRefusalIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "HMDAGenderCollectedBasedOnVisualObservationOrNameIndicator", xmlin,
				path, group);
	}

	/**
	 * Method used to insert HELOC
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertHeloc(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertHelocRule(xmlin, path + "HELOC_RULE/", group, xmlout, insertLevels(xmlout, parentElement, "HELOC_RULE"));
	}

	/**
	 * Method used to insert HELOC rule
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertHelocRule(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "HELOCMaximumBalanceAmount", xmlin, path, group);
	}

	/**
	 * Method used to insert high cost mortgage
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertHighCostMortgage(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "AveragePrimeOfferRatePercent", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "RegulationZExcludedBonaFideDiscountPointsIndicator", xmlin, path,
				group);
		insertDataValue(xmlout, parentElement, "RegulationZExcludedBonaFideDiscountPointsPercent", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "RegulationZTotalAffiliateFeesAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "RegulationZTotalLoanAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "RegulationZTotalPointsAndFeesAmount", xmlin, path, group);
	}

	/**
	 * Method used to insert high cost mortgages
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertHighCostMortgages(Document xmlin, String path, String groupPrefix, Document xmlout,
			Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "HIGH_COST_MORTGAGE", groupPrefix);
		for (String group : groupings)
			insertHighCostMortgage(xmlin, path + "HIGH_COST_MORTGAGE/", group, xmlout,
					insertLevels(xmlout, parentElement, "HIGH_COST_MORTGAGE"));
	}

	/**
	 * Method used to insert HMDA ethnicity origin
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertHmdaEthnicityOrigin(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "HMDAEthnicityOriginType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "HMDAEthnicityOriginTypeOtherDescription", xmlin, path, group);
	}

	/**
	 * Method used to insert HMDA ethnicity origins
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertHmdaEthnicityOrigins(Document xmlin, String path, String groupPrefix, Document xmlout,
			Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "HMDA_ETHNICITY_ORIGIN", groupPrefix);
		for (String group : groupings)
			insertHmdaEthnicityOrigin(xmlin, path + "HMDA_ETHNICITY_ORIGIN/", group, xmlout,
					insertLevels(xmlout, parentElement, "HMDA_ETHNICITY_ORIGIN"));
	}

	/**
	 * Method used to insert HMDA loan denial
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertHmdaLoanDenial(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "HMDAReasonForDenialType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "HMDAReasonForDenialTypeOtherDescription", xmlin, path, group);
	}

	/**
	 * Method used to insert HMDA loan denials
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertHmdaLoanDenials(Document xmlin, String path, String groupPrefix, Document xmlout,
			Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "HMDA_LOAN_DENIAL", groupPrefix);
		for (String group : groupings)
			insertHmdaLoanDenial(xmlin, path + "HMDA_LOAN_DENIAL/", group, xmlout,
					insertLevels(xmlout, parentElement, "HMDA_LOAN_DENIAL"));
	}

	/**
	 * Method used to insert HMDA race
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertHmdaRace(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertHmdaRaceDesignations(xmlin, path + "HMDA_RACE_DESIGNATIONS/", group, xmlout,
				insertLevels(xmlout, parentElement, "HMDA_RACE_DESIGNATIONS"));
		insertHmdaRaceDetail(xmlin, path + "HMDA_RACE_DETAIL/", group, xmlout,
				insertLevels(xmlout, parentElement, "HMDA_RACE_DETAIL"));
	}

	/**
	 * Method used to insert HMDA race designation
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertHmdaRaceDesignation(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "HMDARaceDesignationType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "HMDARaceDesignationTypeOtherDescription", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "HMDARaceDesignationType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "HMDARaceDesignationTypeOtherDescription", xmlin, path, group);
	}

	/**
	 * Method used to insert HMDA race designations
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertHmdaRaceDesignations(Document xmlin, String path, String groupPrefix, Document xmlout,
			Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "HMDA_RACE_DESIGNATION", groupPrefix);
		for (String group : groupings)
			insertHmdaRaceDesignation(xmlin, path + "HMDA_RACE_DESIGNATION/", group, xmlout,
					insertLevels(xmlout, parentElement, "HMDA_RACE_DESIGNATION"));
	}

	/**
	 * Method used to insert HMDA race detail
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertHmdaRaceDetail(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "HMDARaceType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "HMDARaceTypeAdditionalDescription", xmlin, path, group);
	}

	/**
	 * Method used to insert HMDA races
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertHmdaRaces(Document xmlin, String path, String groupPrefix, Document xmlout,
			Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "HMDA_RACE", groupPrefix);
		for (String group : groupings)
			insertHmdaRace(xmlin, path + "HMDA_RACE/", group, xmlout, insertLevels(xmlout, parentElement, "HMDA_RACE"));
	}

	/**
	 * Method used to insert HMDA loan
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertHmdaLoan(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertHmdaLoanDenial(xmlin, path + "HMDA_LOAN_DENIALS/", group, xmlout,
				insertLevels(xmlout, parentElement, "HMDA_LOAN_DENIALS"));
		insertHmdaLoanDetail(xmlin, path + "HMDA_LOAN_DETAIL/", group, xmlout,
				insertLevels(xmlout, parentElement, "HMDA_LOAN_DETAIL"));
	}

	/**
	 * Method used to insert HMDA loan
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertHmdaLoanDetail(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "HMDAPurposeOfLoanType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "HMDAPreapprovalType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "HMDADispositionType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "HMDADispositionDate", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "HMDAReportingCRAExemptionIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "HMDAReportingSmallPopulationIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "HMDAPurchaserType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "HMDAPurchaserTypeOtherDescription", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "HMDARateSpreadPercent", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "HMDAHOEPALoanStatusIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "HMDAMultipleCreditScoresUsedIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "HMDAOtherNonAmortizingFeaturesIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "HMDAManufacturedHomeLegalClassificationType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "HMDAApplicationSubmissionType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "HMDACoveredLoanInitiallyPayableToReportingInstitutionStatusType", xmlin,
				path, group);
		insertDataValue(xmlout, parentElement, "HMDABusinessPurposeIndicator", xmlin, path, group);
	}

	/**
	 * Method used to insert index rule
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertIndexRule(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "IndexType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "IndexTypeOtherDescription", xmlin, path, group);
	}

	/**
	 * Method used to insert index rules
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertIndexRules(Document xmlin, String path, String groupPrefix, Document xmlout,
			Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "INDEX_RULE", groupPrefix);
		for (String group : groupings)
			insertIndexRule(xmlin, path + "INDEX_RULE/", group, xmlout,
					insertLevels(xmlout, parentElement, "INDEX_RULE"));
	}

	/**
	 * Method used to insert individual
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertIndividual(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertContactPoints(xmlin, path + "CONTACT_POINTS/", group, xmlout,
				insertLevels(xmlout, parentElement, "CONTACT_POINTS"));
		insertName(xmlin, path + "NAME/", group, xmlout, insertLevels(xmlout, parentElement, "NAME"));
	}

	/**
	 * Method used to insert integrated disclosure
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertIntegratedDisclosure(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertCashToCloseItems(xmlin, path + "CASH_TO_CLOSE_ITEMS/", group, xmlout,
				insertLevels(xmlout, parentElement, "CASH_TO_CLOSE_ITEMS"));
		insertEstimatedPropertyCost(xmlin, path + "ESTIMATED_PROPERTY_COST/", group, xmlout,
				insertLevels(xmlout, parentElement, "ESTIMATED_PROPERTY_COST"));
		insertIntegratedDisclosureDetail(xmlin, path + "INTEGRATED_DISCLOSURE_DETAIL/", group, xmlout,
				insertLevels(xmlout, parentElement, "INTEGRATED_DISCLOSURE_DETAIL"));
		insertIntegratedDisclosureSectionSummaries(xmlin, path + "INTEGRATED_DISCLOSURE_SECTION_SUMMARIES/", group,
				xmlout, insertLevels(xmlout, parentElement, "INTEGRATED_DISCLOSURE_SECTION_SUMMARIES"));
		insertProjectedPayments(xmlin, path + "PROJECTED_PAYMENTS/", group, xmlout,
				insertLevels(xmlout, parentElement, "PROJECTED_PAYMENTS"));
	}

	/**
	 * Method used to insert integrated disclosure details
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertIntegratedDisclosureDetail(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "FirstYearTotalEscrowPaymentAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "FirstYearTotalEscrowPaymentDescription", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "FirstYearTotalNonEscrowPaymentAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "FirstYearTotalNonEscrowPaymentDescription", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "FiveYearPrincipalReductionComparisonAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "FiveYearTotalOfPaymentsComparisonAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureEstimatedClosingCostsExpirationDatetime", xmlin,
				path, group);
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureHomeEquityLoanIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureIssuedDate", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureLoanProductDescription", xmlin, path, group);
		insertExtension(xmlin, path + "EXTENSION/", null, xmlout, insertLevels(xmlout, parentElement, "EXTENSION"));
	}

	/**
	 * Method used to insert integrated disclosure SectionSummaries
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertIntegratedDisclosureSectionSummaries(Document xmlin, String path, String groupPrefix,
			Document xmlout, Element parentElement) {
		String[] groupings;
		groupings = findGroupings(xmlin, path, "INTEGRATED_DISCLOSURE_SECTION_SUMMARY", groupPrefix);
		for (String group : groupings)
			insertIntegratedDisclosureSectionSummary(xmlin, path + "INTEGRATED_DISCLOSURE_SECTION_SUMMARY/", group,
					xmlout, insertLevels(xmlout, parentElement, "INTEGRATED_DISCLOSURE_SECTION_SUMMARY"));
	}

	/**
	 * Method used to insert integrated disclosure SectionSummary
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertIntegratedDisclosureSectionSummary(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertIntegratedDisclosureSectionSummaryDetail(xmlin, path + "INTEGRATED_DISCLOSURE_SECTION_SUMMARY_DETAIL/",
				group, xmlout, insertLevels(xmlout, parentElement, "INTEGRATED_DISCLOSURE_SECTION_SUMMARY_DETAIL"));
		insertIntegratedDisclosureSubsectionPayments(xmlin, path + "INTEGRATED_DISCLOSURE_SUBSECTION_PAYMENTS/", group,
				xmlout, insertLevels(xmlout, parentElement, "INTEGRATED_DISCLOSURE_SUBSECTION_PAYMENTS"));
	}

	/**
	 * Method used to insert integrated disclosure SectionSummary details
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertIntegratedDisclosureSectionSummaryDetail(Document xmlin, String path, String group,
			Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureSectionTotalAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureSectionType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureSubsectionTotalAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureSubsectionType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureSubsectionTypeOtherDescription", xmlin, path,
				group);
		insertDataValue(xmlout, parentElement, "LenderCreditToleranceCureAmount", xmlin, path, group);
	}

	/**
	 * Method used to insert integrated disclosure sub section payment
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertIntegratedDisclosureSubsectionPayment(Document xmlin, String path, String group,
			Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureSubsectionPaidByType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureSubsectionPaymentAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureSubsectionPaymentTimingType", xmlin, path, group);
	}

	/**
	 * Method used to insert integrated disclosure subsection payments
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertIntegratedDisclosureSubsectionPayments(Document xmlin, String path, String groupPrefix,
			Document xmlout, Element parentElement) {
		String[] groupings;
		groupings = findGroupings(xmlin, path, "INTEGRATED_DISCLOSURE_SUBSECTION_PAYMENT", groupPrefix);
		for (String group : groupings)
			insertIntegratedDisclosureSubsectionPayment(xmlin, path + "INTEGRATED_DISCLOSURE_SUBSECTION_PAYMENT/",
					group, xmlout, insertLevels(xmlout, parentElement, "INTEGRATED_DISCLOSURE_SUBSECTION_PAYMENT"));
	}

	/**
	 * Method used to insert interest only
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertInterestOnly(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "InterestOnlyTermMonthsCount", xmlin, path, group);
	}

	/**
	 * Method used to insert interest rate adjustment
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertInterestRateAdjustment(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertIndexRules(xmlin, path + "INDEX_RULES/", group, xmlout,
				insertLevels(xmlout, parentElement, "INDEX_RULES"));
		insertInterestRateLifetimeAdjustmentRule(xmlin, path + "INTEREST_RATE_LIFETIME_ADJUSTMENT_RULE/", group, xmlout,
				insertLevels(xmlout, parentElement, "INTEREST_RATE_LIFETIME_ADJUSTMENT_RULE"));
		insertInterestRatePerChangeAdjustmentRules(xmlin, path + "INTEREST_RATE_PER_CHANGE_ADJUSTMENT_RULES/", group,
				xmlout, insertLevels(xmlout, parentElement, "INTEREST_RATE_PER_CHANGE_ADJUSTMENT_RULES"));
	}

	/**
	 * Method used to insert interest rate life time adjustment Rule
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertInterestRateLifetimeAdjustmentRule(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "CeilingRatePercent", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "CeilingRatePercentEarliestEffectiveMonthsCount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "FirstRateChangeMonthsCount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "FloorRatePercent", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "MarginRatePercent", xmlin, path, group);
		insertExtension(xmlin, path + "EXTENSION/", group, xmlout, insertLevels(xmlout, parentElement, "EXTENSION"));
	}

	/**
	 * Method used to insert interest rate per change adjustment Rule
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertInterestRatePerChangeAdjustmentRule(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "AdjustmentRuleType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PerChangeMaximumIncreaseRatePercent", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PerChangeRateAdjustmentFrequencyMonthsCount", xmlin, path, group);
	}

	/**
	 * Method used to insert interest rate per changed adjustment Rules
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param groupprefix
	 *            group prefix from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertInterestRatePerChangeAdjustmentRules(Document xmlin, String path, String groupPrefix,
			Document xmlout, Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "INTEREST_RATE_PER_CHANGE_ADJUSTMENT_RULE", groupPrefix);
		for (String group : groupings)
			insertInterestRatePerChangeAdjustmentRule(xmlin, path + "INTEREST_RATE_PER_CHANGE_ADJUSTMENT_RULE/", group,
					xmlout, insertLevels(xmlout, parentElement, "INTEREST_RATE_PER_CHANGE_ADJUSTMENT_RULE"));
	}

	/**
	 * Method used to insert late charge Rule
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertLateChargeRule(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "LateChargeAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "LateChargeGracePeriodDaysCount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "LateChargeMaximumAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "LateChargeMinimumAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "LateChargeRatePercent", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "LateChargeType", xmlin, path, group);
	}

	/**
	 * Method used to insert legal entity
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */

	protected void insertLegalEntity(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertLegalEntityDetail(xmlin, path + "LEGAL_ENTITY_DETAIL/", group, xmlout,
				insertLevels(xmlout, parentElement, "LEGAL_ENTITY_DETAIL"));
	}

	/**
	 * Method used to insert legal entity detail
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertLegalEntityDetail(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "FullName", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "GlobalLegalEntityIdentifier", xmlin, path, group);
	}

	/**
	 * Method used to insert liabilities
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertLiabilities(Document xmlin, String path, String groupPrefix, Document xmlout,
			Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "LIABILITY", groupPrefix);
		for (String group : groupings)
			insertLiability(xmlin, path + "LIABILITY/", group, xmlout,
					insertLevels(xmlout, parentElement, "LIABILITY"));
	}

	/**
	 * Method used to insert liability
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertLiability(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertLiabilityDetail(xmlin, path + "LIABILITY_DETAIL/", group, xmlout,
				insertLevels(xmlout, parentElement, "LIABILITY_DETAIL"));
		insertLiabilityHolder(xmlin, path + "LIABILITY_HOLDER/", group, xmlout,
				insertLevels(xmlout, parentElement, "LIABILITY_HOLDER"));
		insertPayoff(xmlin, path + "PAYOFF/", group, xmlout, insertLevels(xmlout, parentElement, "PAYOFF"));
	}

	/**
	 * Method used to insert liability detail
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertLiabilityDetail(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "LiabilityDescription", xmlin, path, group);
		Element liabilityTypeElement = insertDataValue(xmlout, parentElement, "LiabilityType", xmlin, path, group);
		insertAttributeValue(xmlout, liabilityTypeElement, "gse:DisplayLabelText", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "LiabilityTypeOtherDescription", xmlin, path, group);
		insertExtension(xmlin, path + "EXTENSION/", group, xmlout, insertLevels(xmlout, parentElement, "EXTENSION"));
	}

	/**
	 * Method used to insert liability holder
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertLiabilityHolder(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertName(xmlin, path + "NAME/", group, xmlout, insertLevels(xmlout, parentElement, "NAME"));
	}

	/**
	 * Method used to insert license
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertLicense(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertLicenseDetail(xmlin, path + "LICENSE_DETAIL/", group, xmlout,
				insertLevels(xmlout, parentElement, "LICENSE_DETAIL"));
	}

	/**
	 * Method used to insert license detail
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertLicenseDetail(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "LicenseAuthorityLevelType", xmlin, path, group);
		Element element = insertDataValue(xmlout, parentElement, "LicenseIdentifier", xmlin, path, group);
		insertAttributeValue(xmlout, element, "IdentifierOwnerURI", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "LicenseIssueDate", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "LicenseIssuingAuthorityName", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "LicenseIssuingAuthorityStateCode", xmlin, path, group);
	}

	/**
	 * Method used to insert licenses
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param groupprefix
	 *            group prefix from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertLicenses(Document xmlin, String path, String groupPrefix, Document xmlout,
			Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "LICENSE", groupPrefix);
		for (String group : groupings)
			insertLicense(xmlin, path + "LICENSE/", group, xmlout, insertLevels(xmlout, parentElement, "LICENSE"));
	}

	/**
	 * Method used to insert loan
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertLoan(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertAdjustment(xmlin, path + "ADJUSTMENT/", group, xmlout, insertLevels(xmlout, parentElement, "ADJUSTMENT"));
		insertAmortizationRule(xmlin, path + "AMORTIZATION/AMORTIZATION_RULE/", group, xmlout,
				insertLevels(xmlout, parentElement, "AMORTIZATION/AMORTIZATION_RULE"));
		insertBuydown(xmlin, path + "BUYDOWN/", group, xmlout, insertLevels(xmlout, parentElement, "BUYDOWN"));
		insertClosingInformation(xmlin, path + "CLOSING_INFORMATION/", group, xmlout,
				insertLevels(xmlout, parentElement, "CLOSING_INFORMATION"));
		insertConstruction(xmlin, path + "CONSTRUCTION/", group, xmlout,
				insertLevels(xmlout, parentElement, "CONSTRUCTION"));
		insertDocumentSpecificDataSet(xmlin, path + "DOCUMENT_SPECIFIC_DATA_SETS/DOCUMENT_SPECIFIC_DATA_SET/", group,
				xmlout, insertLevels(xmlout, parentElement, "DOCUMENT_SPECIFIC_DATA_SETS/DOCUMENT_SPECIFIC_DATA_SET"));
		insertEscrow(xmlin, path + "ESCROW/", group, xmlout, insertLevels(xmlout, parentElement, "ESCROW"));
		insertFeeInformation(xmlin, path + "FEE_INFORMATION/", group, xmlout,
				insertLevels(xmlout, parentElement, "FEE_INFORMATION"));
		insertForeclosures(xmlin, path + "FORECLOSURES/", group, xmlout,
				insertLevels(xmlout, parentElement, "FORECLOSURES")); // Not
																		// needed
																		// for
																		// LE
		insertHeloc(xmlin, path + "HELOC/", group, xmlout, insertLevels(xmlout, parentElement, "HELOC")); // Not
																											// needed
																											// for
																											// LE
		insertHighCostMortgages(xmlin, path + "HIGH_COST_MORTGAGES/", group, xmlout,
				insertLevels(xmlout, parentElement, "HIGH_COST_MORTGAGES")); // Not
																				// needed
																				// for
																				// LE
		insertHmdaLoan(xmlin, path + "HMDA_LOAN/", group, xmlout, insertLevels(xmlout, parentElement, "HMDA_LOAN")); // Not
																														// needed
																														// for
																														// LE
		insertInterestOnly(xmlin, path + "INTEREST_ONLY/", group, xmlout,
				insertLevels(xmlout, parentElement, "INTEREST_ONLY"));
		insertLateChargeRule(xmlin, path + "LATE_CHARGE/EXTENSION/OTHER/gse:LATE_CHARGE_RULES/LATE_CHARGE_RULE/", group,
				xmlout, insertLevels(xmlout, parentElement,
						"LATE_CHARGE/EXTENSION/OTHER/gse:LATE_CHARGE_RULES/LATE_CHARGE_RULE"));
		insertLoanDetail(xmlin, path + "LOAN_DETAIL/", group, xmlout,
				insertLevels(xmlout, parentElement, "LOAN_DETAIL"));
		insertLoanIdentifiers(xmlin, path + "LOAN_IDENTIFIERS/", group, xmlout,
				insertLevels(xmlout, parentElement, "LOAN_IDENTIFIERS"));
		insertLoanLevelCredit(xmlin, path + "LOAN_LEVEL_CREDIT/", group, xmlout,
				insertLevels(xmlout, parentElement, "LOAN_LEVEL_CREDIT"));
		insertLoanProduct(xmlin, path + "LOAN_PRODUCT/", group, xmlout,
				insertLevels(xmlout, parentElement, "LOAN_PRODUCT")); // Not
																		// needed
																		// for
																		// LE
		insertMaturityRule(xmlin, path + "MATURITY/MATURITY_RULE/", group, xmlout,
				insertLevels(xmlout, parentElement, "MATURITY/MATURITY_RULE"));
		insertMIDataDetail(xmlin, path + "MI_DATA/MI_DATA_DETAIL/", group, xmlout,
				insertLevels(xmlout, parentElement, "MI_DATA/MI_DATA_DETAIL")); // Not
																				// needed
																				// for
																				// LE
		insertNegativeAmortization(xmlin, path + "NEGATIVE_AMORTIZATION/", group, xmlout,
				insertLevels(xmlout, parentElement, "NEGATIVE_AMORTIZATION"));
		insertPayment(xmlin, path + "PAYMENT/", group, xmlout, insertLevels(xmlout, parentElement, "PAYMENT"));
		insertPrepaymentPenalty(xmlin, path + "PREPAYMENT_PENALTY/", group, xmlout,
				insertLevels(xmlout, parentElement, "PREPAYMENT_PENALTY"));
		insertQualification(xmlin, path + "QUALIFICATION/", group, xmlout,
				insertLevels(xmlout, parentElement, "QUALIFICATION")); // Not
																		// needed
																		// for
																		// LE
		insertQualifiedMortgage(xmlin, path + "QUALIFIED_MORTGAGE/", group, xmlout,
				insertLevels(xmlout, parentElement, "QUALIFIED_MORTGAGE")); // Not
																			// needed
																			// for
																			// LE
		insertRefinance(xmlin, path + "REFINANCE/", group, xmlout, insertLevels(xmlout, parentElement, "REFINANCE")); // Not
																														// needed
																														// for
																														// LE
		insertReverseMortgage(xmlin, path + "REVERSE_MORTGAGE/", group, xmlout,
				insertLevels(xmlout, parentElement, "REVERSE_MORTGAGE")); // Not
																			// needed
																			// for
																			// LE
		insertServicing(xmlin, path + "SERVICING/", group, xmlout, insertLevels(xmlout, parentElement, "SERVICING")); // Not
																														// needed
																														// for
																														// LE
		insertTermsOfLoan(xmlin, path + "TERMS_OF_LOAN/", group, xmlout,
				insertLevels(xmlout, parentElement, "TERMS_OF_LOAN"));
		insertUnderwriting(xmlin, path + "UNDERWRITING/", group, xmlout,
				insertLevels(xmlout, parentElement, "UNDERWRITING")); // Not
																		// needed
																		// for
																		// LE
	}

	/**
	 * Method used to insert loan detail
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertLoanDetail(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "AssumedIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "AssumabilityIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "BalloonIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "BalloonPaymentAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "BuydownTemporarySubsidyFundingIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ConstructionLoanIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "CreditorServicingOfLoanStatementType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "DemandFeatureIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "EscrowAbsenceReasonType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "EscrowIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "InterestOnlyIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "InterestRateIncreaseIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "LoanAmountIncreaseIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "LoanLevelCreditScoreValue", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "MIRequiredIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "NegativeAmortizationIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PaymentIncreaseIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PrepaymentPenaltyIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "SeasonalPaymentFeatureIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "StepPaymentsFeatureDescription", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "TotalSubordinateFinancingAmount", xmlin, path, group);
		insertExtension(xmlin, path + "EXTENSION/", group, xmlout, insertLevels(xmlout, parentElement, "EXTENSION"));
	}

	/**
	 * Method used to insert loan identifier
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertLoanIdentifier(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "LoanIdentifier", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "LoanIdentifierType", xmlin, path, group);
	}

	/**
	 * Method used to insert loan identifiers
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param groupprefix
	 *            group prefix from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertLoanIdentifiers(Document xmlin, String path, String groupPrefix, Document xmlout,
			Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "LOAN_IDENTIFIER", groupPrefix);
		for (String group : groupings)
			insertLoanIdentifier(xmlin, path + "LOAN_IDENTIFIER/", group, xmlout,
					insertLevels(xmlout, parentElement, "LOAN_IDENTIFIER"));
	}

	/**
	 * Method used to insert loan level credit
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertLoanLevelCredit(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertLoanLevelCreditDetail(xmlin, path + "LOAN_LEVEL_CREDIT_DETAIL/", group, xmlout,
				insertLevels(xmlout, parentElement, "LOAN_LEVEL_CREDIT_DETAIL"));
	}

	/**
	 * Method used to insert loan level credit detail
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertLoanLevelCreditDetail(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "LoanLevelCreditScoreValue", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "CreditScoreModelNameType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "CreditScoreModelNameTypeOtherDescription", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "CreditScoreCategoryVersionType", xmlin, path, group);
	}

	/**
	 * Method used to insert loan price quote
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertLoanPriceQuote(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertLoanPriceQuoteDetail(xmlin, path + "LOAN_PRICE_QUOTE_DETAIL/", group, xmlout,
				insertLevels(xmlout, parentElement, "LOAN_PRICE_QUOTE_DETAIL"));
	}

	/**
	 * Method used to insert loan price quote detail
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertLoanPriceQuoteDetail(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "LoanPriceQuoteInterestRatePercent", xmlin, path, group);
	}

	/**
	 * Method used to insert loan price quotes
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param groupprefix
	 *            group prefix from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertLoanPriceQuotes(Document xmlin, String path, String groupPrefix, Document xmlout,
			Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "LOAN_PRICE_QUOTE", groupPrefix);
		for (String group : groupings)
			insertLoanPriceQuote(xmlin, path + "LOAN_PRICE_QUOTE/", group, xmlout,
					insertLevels(xmlout, parentElement, "LOAN_PRICE_QUOTE"));
	}

	/**
	 * Method used to insert loanproduct
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertLoanProduct(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertLoanPriceQuotes(xmlin, path + "LOAN_PRICE_QUOTES/", group, xmlout,
				insertLevels(xmlout, parentElement, "LOAN_PRICE_QUOTES"));
		insertLocks(xmlin, path + "LOCKS/", group, xmlout, insertLevels(xmlout, parentElement, "LOCKS"));
	}

	/**
	 * Method used to insert loans
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertLoans(Document xmlin, String path, String groupPrefix, Document xmlout,
			Element parentElement) {
		insertCombinedLtvs(xmlin, path + "COMBINED_LTVS/", groupPrefix, xmlout,
				insertLevels(xmlout, parentElement, "COMBINED_LTVS"));
		String[] groupings = findGroupings(xmlin, path, "LOAN", groupPrefix);
		for (String group : groupings)
			insertLoan(xmlin, path + "LOAN/", group, xmlout, insertLevels(xmlout, parentElement, "LOAN"));
	}

	/**
	 * Method used to insert location identifier
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertLocationIdentifier(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertCensusInformation(xmlin, path + "CENSUS_INFORMATION/", group, xmlout,
				insertLevels(xmlout, parentElement, "CENSUS_INFORMATION"));
		insertFipsInformation(xmlin, path + "FIPS_INFORMATION/", group, xmlout,
				insertLevels(xmlout, parentElement, "FIPS_INFORMATION"));
	}

	/**
	 * Method used to insert lock
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertLock(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "LockExpirationDatetime", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "LockStatusType", xmlin, path, group);
		insertExtension(xmlin, path + "EXTENSION/", group, xmlout, insertLevels(xmlout, parentElement, "EXTENSION"));
	}

	/**
	 * Method used to insert locks
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertLocks(Document xmlin, String path, String groupPrefix, Document xmlout,
			Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "LOCK", groupPrefix);
		for (String group : groupings)
			insertLock(xmlin, path + "LOCK/", group, xmlout, insertLevels(xmlout, parentElement, "LOCK"));
	}

	/**
	 * Method used to insert maturty rule
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertMaturityRule(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "LoanMaturityPeriodCount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "LoanMaturityPeriodType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "LoanTermMaximumMonthsCount", xmlin, path, group);
	}

	/**
	 * Method used to insert messege
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertMessage(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		parentElement.setAttribute(XMLNS_ALIAS + ":xsi", XSI_URI);
		parentElement.setAttribute("xsi:schemaLocation",
				"http://www.mismo.org/residential/2009/schemas ../../../MISMO/V3.3.0_CR_2014-02/ReferenceModel_v3.3.0_B299/MISMO_3.3.0_B299.xsd");
		parentElement.setAttribute(XMLNS_ALIAS + ":" + MISMO_ALIAS, MISMO_URI);
		parentElement.setAttribute(XMLNS_ALIAS + ":" + GSE_ALIAS, GSE_URI);
		parentElement.setAttribute(XMLNS_ALIAS + ":" + XLINK_ALIAS, XLINK_URI);

		insertAttributeValue(xmlout, parentElement, "MISMOReferenceModelIdentifier", xmlin, path);
		insertAboutVersions(xmlin, path + "ABOUT_VERSIONS/", group, xmlout,
				insertLevels(xmlout, xmlout.getDocumentElement(), "ABOUT_VERSIONS"));
		insertDealSets(xmlin, path + "DEAL_SETS/", group, xmlout,
				insertLevels(xmlout, xmlout.getDocumentElement(), "DEAL_SETS"));
		insertDocumentSets(xmlin, path + "DOCUMENT_SETS/", group, xmlout,
				insertLevels(xmlout, xmlout.getDocumentElement(), "DOCUMENT_SETS"));
	}

	/**
	 * Method used to insert MI Data detail
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertMIDataDetail(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "MICertificateIdentifier", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "MICompanyNameType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "MICompanyNameTypeOtherDescription", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "MIScheduledTerminationDate", xmlin, path, group);
	}

	/**
	 * Method used to insert mismo
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertMismo(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "PaymentIncludedInAPRIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PayoffPartialIndicator", xmlin, path, group);
	}

	/**
	 * Method used to insert name
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertName(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "FirstName", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "FullName", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "LastName", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "MiddleName", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "SuffixName", xmlin, path, group);
	}

	/**
	 * Method used to insert negative amortization
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertNegativeAmortization(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertNegativeAmortizationRule(xmlin, path + "NEGATIVE_AMORTIZATION_RULE/", group, xmlout,
				insertLevels(xmlout, parentElement, "NEGATIVE_AMORTIZATION_RULE"));
	}

	/**
	 * Method used to insert negative amortization rule
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertNegativeAmortizationRule(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "LoanNegativeAmortizationResolutionType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "LoanNegativeAmortizationResolutionTypeOtherDescription", xmlin, path,
				group);
		insertDataValue(xmlout, parentElement, "NegativeAmortizationLimitMonthsCount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "NegativeAmortizationMaximumLoanBalanceAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "NegativeAmortizationType", xmlin, path, group);
	}

	/**
	 * Method used to insert other
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertOther(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, GSE_ALIAS + ":BuydownReflectedInNoteIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, GSE_ALIAS + ":DocumentSignatureRequiredIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, GSE_ALIAS + ":EscrowAccountRolloverAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement,
				GSE_ALIAS + ":IntegratedDisclosureEstimatedClosingCostsExpirationTimezoneType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, GSE_ALIAS + ":IntegratedDisclosureSectionType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, GSE_ALIAS + ":LiabilitySecuredBySubjectPropertyIndicator", xmlin, path,
				group);
		insertDataValue(xmlout, parentElement, GSE_ALIAS + ":LockExpirationTimezoneType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, GSE_ALIAS + ":TotalOptionalPaymentCount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, GSE_ALIAS + ":TotalStepCount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, GSE_ALIAS + ":TotalStepPaymentCount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, GSE_ALIAS + ":SubordinateFinancingIsNewIndicator", xmlin, path, group);
	}

	/**
	 * Method used to insert partial payment
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertPartialPayment(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "PartialPaymentApplicationMethodType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PartialPaymentApplicationMethodTypeOtherDescription", xmlin, path, group);
	}

	/**
	 * Method used to insert partial payments
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param groupprefix
	 *            group prefix from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertPartialPayments(Document xmlin, String path, String groupPrefix, Document xmlout,
			Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "PARTIAL_PAYMENT", groupPrefix);
		for (String group : groupings)
			insertPartialPayment(xmlin, path + "PARTIAL_PAYMENT/", group, xmlout,
					insertLevels(xmlout, parentElement, "PARTIAL_PAYMENT"));
	}

	/**
	 * Method used to insert parties
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param groupprefix
	 *            group prefix from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertParties(Document xmlin, String path, String groupPrefix, Document xmlout,
			Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "PARTY", groupPrefix);
		for (String group : groupings)
			insertParty(xmlin, path + "PARTY/", group, xmlout, insertLevels(xmlout, parentElement, "PARTY"));
	}

	/**
	 * Method used to insert party
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertParty(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertIndividual(xmlin, path + "INDIVIDUAL/", group, xmlout, insertLevels(xmlout, parentElement, "INDIVIDUAL"));
		insertLegalEntity(xmlin, path + "LEGAL_ENTITY/", group, xmlout,
				insertLevels(xmlout, parentElement, "LEGAL_ENTITY"));
		insertAddresses(xmlin, path + "ADDRESSES/", group, xmlout, insertLevels(xmlout, parentElement, "ADDRESSES"));
		insertRoles(xmlin, path + "ROLES/", group, xmlout, insertLevels(xmlout, parentElement, "ROLES"));
		insertTaxpayerIdentifiers(xmlin, path + "TAXPAYER_IDENTIFIERS/", group, xmlout,
				insertLevels(xmlout, parentElement, "TAXPAYER_IDENTIFIERS"));
	}

	/**
	 * Method used to insert party role identifier
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param groupPrefix
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertPartyRoleIdentifier(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		Element element = insertDataValue(xmlout, parentElement, "PartyRoleIdentifier", xmlin, path, group);
		insertAttributeValue(xmlout, element, "IdentifierOwnerURI", xmlin, path, group);
	}

	/**
	 * Method used to insert party role identifiers
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param groupPrefix
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertPartyRoleIdentifiers(Document xmlin, String path, String groupPrefix, Document xmlout,
			Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "PARTY_ROLE_IDENTIFIER", groupPrefix);
		for (String group : groupings)
			insertPartyRoleIdentifier(xmlin, path + "PARTY_ROLE_IDENTIFIER/", group, xmlout,
					insertLevels(xmlout, parentElement, "PARTY_ROLE_IDENTIFIER"));
	}

	/**
	 * Method used to insert payment
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertPayment(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertPartialPayments(xmlin, path + "PARTIAL_PAYMENTS/", group, xmlout,
				insertLevels(xmlout, parentElement, "PARTIAL_PAYMENTS")); // Not
																			// needed
																			// in
																			// LE
		insertPaymentRule(xmlin, path + "PAYMENT_RULE/", group, xmlout,
				insertLevels(xmlout, parentElement, "PAYMENT_RULE"));
	}

	/**
	 * Method used to insert payment rule
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertPaymentRule(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "FullyIndexedInitialPrincipalAndInterestPaymentAmount", xmlin, path,
				group);
		insertDataValue(xmlout, parentElement, "InitialPrincipalAndInterestPaymentAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PartialPaymentAllowedIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PaymentFrequencyType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PaymentOptionIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "SeasonalPaymentPeriodEndMonth", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "SeasonalPaymentPeriodStartMonth", xmlin, path, group);
		insertExtension(xmlin, path + "EXTENSION/", group, xmlout, insertLevels(xmlout, parentElement, "EXTENSION"));
	}

	/**
	 * Method used to insert payoff
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertPayoff(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "PayoffAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PayoffPrepaymentPenaltyAmount", xmlin, path, group);
		insertExtension(xmlin, path + "EXTENSION/", group, xmlout, insertLevels(xmlout, parentElement, "EXTENSION"));
	}

	/**
	 * Method used to insert Pledged Asset
	 * 
	 * @param xmlin
	 * @param path
	 * @param group
	 * @param xmlout
	 * @param parentElement
	 */
	protected void insertPledgedAsset(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertAssetDetail(xmlin, path + "ASSET_DETAIL/", group, xmlout,
				insertLevels(xmlout, parentElement, "ASSET_DETAIL"));
	}

	/**
	 * Method used to insert prepaid item
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertPrepaidItem(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertPrepaidItemDetail(xmlin, path + "PREPAID_ITEM_DETAIL/", group, xmlout,
				insertLevels(xmlout, parentElement, "PREPAID_ITEM_DETAIL"));
		insertPrepaidItemPaidTo(xmlin, path + "PREPAID_ITEM_PAID_TO/", group, xmlout,
				insertLevels(xmlout, parentElement, "PREPAID_ITEM_PAID_TO"));
		insertPrepaidItemPayments(xmlin, path + "PREPAID_ITEM_PAYMENTS/", group, xmlout,
				insertLevels(xmlout, parentElement, "PREPAID_ITEM_PAYMENTS"));
	}

	/**
	 * Method used to insert prepaid item detail
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertPrepaidItemDetail(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "FeePaidToType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "FeePaidToTypeOtherDescription", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureSectionType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PrepaidItemEstimatedTotalAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PrepaidItemMonthsPaidCount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PrepaidItemNumberOfDaysCount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PrepaidItemPaidFromDate", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PrepaidItemPaidThroughDate", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PrepaidItemPerDiemAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PrepaidItemPerDiemCalculationMethodType", xmlin, path, group);
		Element prepaidItemTypeElement = insertDataValue(xmlout, parentElement, "PrepaidItemType", xmlin, path, group);
		insertAttributeValue(xmlout, prepaidItemTypeElement, "gse:DisplayLabelText", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PrepaidItemTypeOtherDescription", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "RegulationZPointsAndFeesIndicator", xmlin, path, group);
		insertExtension(xmlin, path + "EXTENSION/", group, xmlout, insertLevels(xmlout, parentElement, "EXTENSION"));
	}

	/**
	 * Method used to insert prepaid item paid to
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertPrepaidItemPaidTo(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertLegalEntity(xmlin, path + "LEGAL_ENTITY/", group, xmlout,
				insertLevels(xmlout, parentElement, "LEGAL_ENTITY"));
	}

	/**
	 * Method used to insert prepaid item payment
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertPrepaidItemPayment(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "PrepaidItemActualPaymentAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PrepaidItemPaymentPaidByType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PrepaidItemPaymentTimingType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "RegulationZPointsAndFeesIndicator", xmlin, path, group);
	}

	/**
	 * Method used to insert prepaid item payments
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param groupprefix
	 *            group prefix from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertPrepaidItemPayments(Document xmlin, String path, String groupPrefix, Document xmlout,
			Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "PREPAID_ITEM_PAYMENT", groupPrefix);
		for (String group : groupings)
			insertPrepaidItemPayment(xmlin, path + "PREPAID_ITEM_PAYMENT/", group, xmlout,
					insertLevels(xmlout, parentElement, "PREPAID_ITEM_PAYMENT"));
	}

	/**
	 * Method used to insert prepaid items
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param groupprefix
	 *            group prefix from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertPrepaidItems(Document xmlin, String path, String groupPrefix, Document xmlout,
			Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "PREPAID_ITEM", groupPrefix);
		for (String group : groupings)
			insertPrepaidItem(xmlin, path + "PREPAID_ITEM/", group, xmlout,
					insertLevels(xmlout, parentElement, "PREPAID_ITEM"));
	}

	/**
	 * Method used to insert pre payment penalty
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param groupprefix
	 *            group prefix from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertPrepaymentPenalty(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertPrepaymentPenaltyLifetimeRule(xmlin, path + "PREPAYMENT_PENALTY_LIFETIME_RULE/", group, xmlout,
				insertLevels(xmlout, parentElement, "PREPAYMENT_PENALTY_LIFETIME_RULE"));
	}

	/**
	 * Method used to insert pre payment penalty life time rule
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param groupprefix
	 *            group prefix from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertPrepaymentPenaltyLifetimeRule(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "PrepaymentPenaltyExpirationDate", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PrepaymentPenaltyExpirationMonthsCount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PrepaymentPenaltyMaximumLifeOfLoanAmount", xmlin, path, group);
	}

	/**
	 * Method used to insert principal and interest payment adjustment
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param groupprefix
	 *            group prefix from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertPrincipalAndInterestPaymentAdjustment(Document xmlin, String path, String group,
			Document xmlout, Element parentElement) {
		insertPrincipalAndInterestPaymentLifetimeAdjustmentRule(xmlin,
				path + "PRINCIPAL_AND_INTEREST_PAYMENT_LIFETIME_ADJUSTMENT_RULE/", group, xmlout,
				insertLevels(xmlout, parentElement, "PRINCIPAL_AND_INTEREST_PAYMENT_LIFETIME_ADJUSTMENT_RULE"));
		insertPrincipalAndInterestPaymentPerChangeAdjustmentRules(xmlin,
				path + "PRINCIPAL_AND_INTEREST_PAYMENT_PER_CHANGE_ADJUSTMENT_RULES/", group, xmlout,
				insertLevels(xmlout, parentElement, "PRINCIPAL_AND_INTEREST_PAYMENT_PER_CHANGE_ADJUSTMENT_RULES"));
	}

	/**
	 * Method used to
	 * insertPrincipalAndInterestPaymentAdjustmentLimitedPaymentOption
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertPrincipalAndInterestPaymentAdjustmentLimitedPaymentOption(Document xmlin, String path,
			String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "LimitedPrincipalAndInterestPaymentEffectiveDate", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "LimitedPrincipalAndInterestPaymentPeriodEndDate", xmlin, path, group);
	}

	/**
	 * Method used to
	 * insertPrincipalAndInterestPaymentAdjustmentLimitedPaymentOptions
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param groupprefix
	 *            group prefix from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertPrincipalAndInterestPaymentAdjustmentLimitedPaymentOptions(Document xmlin, String path,
			String groupPrefix, Document xmlout, Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "PRINCIPAL_AND_INTEREST_ADJUSTMENT_LIMITED_PAYMENT_OPTION",
				groupPrefix);
		for (String group : groupings)
			insertPrincipalAndInterestPaymentAdjustmentLimitedPaymentOption(xmlin,
					path + "PRINCIPAL_AND_INTEREST_ADJUSTMENT_LIMITED_PAYMENT_OPTION/", group, xmlout,
					insertLevels(xmlout, parentElement, "PRINCIPAL_AND_INTEREST_ADJUSTMENT_LIMITED_PAYMENT_OPTION"));
	}

	/**
	 * Method used to insert PrincipalAndInterestPaymentLifetimeAdjustmentRule
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertPrincipalAndInterestPaymentLifetimeAdjustmentRule(Document xmlin, String path, String group,
			Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "FirstPrincipalAndInterestPaymentChangeMonthsCount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PrincipalAndInterestPaymentMaximumAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PrincipalAndInterestPaymentMaximumAmountEarliestEffectiveMonthsCount",
				xmlin, path, group);
	}

	/**
	 * Method used to insert PrincipalAndInterestPaymentPerChangeAdjustmentRule
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertPrincipalAndInterestPaymentPerChangeAdjustmentRule(Document xmlin, String path, String group,
			Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "AdjustmentRuleType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PerChangeMaximumPrincipalAndInterestPaymentAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PerChangeMinimumPrincipalAndInterestPaymentAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PerChangePrincipalAndInterestPaymentAdjustmentFrequencyMonthsCount",
				xmlin, path, group);
	}

	/**
	 * Method used to insert PrincipalAndInterestPaymentPerChangeAdjustmentRules
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param groupprefix
	 *            group prefix from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertPrincipalAndInterestPaymentPerChangeAdjustmentRules(Document xmlin, String path,
			String groupPrefix, Document xmlout, Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "PRINCIPAL_AND_INTEREST_PAYMENT_PER_CHANGE_ADJUSTMENT_RULE",
				groupPrefix);
		for (String group : groupings)
			insertPrincipalAndInterestPaymentPerChangeAdjustmentRule(xmlin,
					path + "PRINCIPAL_AND_INTEREST_PAYMENT_PER_CHANGE_ADJUSTMENT_RULE/", group, xmlout,
					insertLevels(xmlout, parentElement, "PRINCIPAL_AND_INTEREST_PAYMENT_PER_CHANGE_ADJUSTMENT_RULE"));
	}

	/**
	 * Method used to insert projected payment
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertProjectedPayment(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertAttributeValue(xmlout, parentElement, "SequenceNumber", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PaymentFrequencyType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ProjectedPaymentCalculationPeriodEndNumber", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ProjectedPaymentCalculationPeriodStartNumber", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ProjectedPaymentCalculationPeriodTermType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ProjectedPaymentCalculationPeriodTermTypeOtherDescription", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ProjectedPaymentEstimatedEscrowPaymentAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ProjectedPaymentEstimatedTotalMaximumPaymentAmount", xmlin, path,
				group);
		insertDataValue(xmlout, parentElement, "ProjectedPaymentEstimatedTotalMinimumPaymentAmount", xmlin, path,
				group);
		insertDataValue(xmlout, parentElement, "ProjectedPaymentMIPaymentAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ProjectedPaymentPrincipalAndInterestMaximumPaymentAmount", xmlin, path,
				group);
		insertDataValue(xmlout, parentElement, "ProjectedPaymentPrincipalAndInterestMinimumPaymentAmount", xmlin, path,
				group);
	}

	/**
	 * Method used to insert projected payments
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param groupprefix
	 *            group prefix from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertProjectedPayments(Document xmlin, String path, String groupPrefix, Document xmlout,
			Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "PROJECTED_PAYMENT", groupPrefix);
		for (String group : groupings)
			insertProjectedPayment(xmlin, path + "PROJECTED_PAYMENT/", group, xmlout,
					insertLevels(xmlout, parentElement, "PROJECTED_PAYMENT"));
	}

	/**
	 * Method used to insert property detail
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertPropertyDetail(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "AffordableUnitsCount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ConstructionMethodType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "FinancedUnitCount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "MetropolitanDivisionIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "MSAIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PropertyEstateType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PropertyEstateTypeOtherDescription", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PropertyEstimatedValueAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PropertyUsageType", xmlin, path, group);
	}

	/**
	 * Method used to insert property valuation
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertPropertyValuation(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertPropertyValuationDetail(xmlin, path + "PROPERTY_VALUATION_DETAIL/", group, xmlout,
				insertLevels(xmlout, parentElement, "PROPERTY_VALUATION_DETAIL"));
	}

	/**
	 * Method used to insert property valuation detail
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertPropertyValuationDetail(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		Element element = insertDataValue(xmlout, parentElement, "AppraisalIdentifier", xmlin, path, group);
		insertAttributeValue(xmlout, element, "IdentifierOwnerURI", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PropertyValuationAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PropertyValuationMethodType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PropertyValuationMethodTypeOtherDescription", xmlin, path, group);
	}

	/**
	 * Method used to insert property valuations
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param groupprefix
	 *            group prefix from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertPropertyValuations(Document xmlin, String path, String groupPrefix, Document xmlout,
			Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "PROPERTY_VALUATION", groupPrefix);
		for (String group : groupings)
			insertPropertyValuation(xmlin, path + "PROPERTY_VALUATION/", group, xmlout,
					insertLevels(xmlout, parentElement, "PROPERTY_VALUATION"));
	}

	/**
	 * Method used to insert proration item
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertProrationItem(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureSectionType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureSubsectionType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ProrationItemAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ProrationItemPaidFromDate", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ProrationItemPaidThroughDate", xmlin, path, group);
		Element prorationItemTypeElement = insertDataValue(xmlout, parentElement, "ProrationItemType", xmlin, path,
				group);
		insertAttributeValue(xmlout, prorationItemTypeElement, "gse:DisplayLabelText", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ProrationItemTypeOtherDescription", xmlin, path, group);
	}

	/**
	 * Method used to insert proration items
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param groupprefix
	 *            group prefix from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertProrationItems(Document xmlin, String path, String groupPrefix, Document xmlout,
			Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "PRORATION_ITEM", groupPrefix);
		for (String group : groupings)
			insertProrationItem(xmlin, path + "PRORATION_ITEM/", group, xmlout,
					insertLevels(xmlout, parentElement, "PRORATION_ITEM"));
	}

	/**
	 * Method used to insert qualification
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertQualification(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "TotalMonthlyIncomeAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "IncomeConsideredInDecisionIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "CreditScoreConsideredInDecisionIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "TotalDebtExpenseRatioPercent", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "TotalDebtExpenseRatioConsideredInDecisionIndicator", xmlin, path,
				group);
		insertDataValue(xmlout, parentElement, "CombinedLTVRatioConsideredInDecisionIndicator", xmlin, path, group);
	}

	/**
	 * Method used to insert qualified mortgage
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertQualifiedMortgage(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertExemption(xmlin, path + "EXEMPTIONS/EXEMPTION/", group, xmlout,
				insertLevels(xmlout, parentElement, "EXEMPTIONS/EXEMPTION"));
		insertQualifiedMortgageDetail(xmlin, path + "QUALIFIED_MORTGAGE_DETAIL/", group, xmlout,
				insertLevels(xmlout, parentElement, "QUALIFIED_MORTGAGE_DETAIL"));
	}

	/**
	 * Method used to insert qualified mortgage detail
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertQualifiedMortgageDetail(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "AbilityToRepayMethodType", xmlin, path, group);
	}

	/**
	 * Method used to insert real estate agent
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertRealEstateAgent(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "RealEstateAgentType", xmlin, path, group);
	}

	/**
	 * Method used to insert refinance
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertRefinance(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "RefinanceCashOutDeterminationType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "RefinanceSameLenderIndicator", xmlin, path, group);
	}

	/**
	 * Method used to insert reverse mortgage
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertReverseMortgage(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "ReverseInitialPrincipalLimitAmount", xmlin, path, group);
	}

	/**
	 * Method used to insert regulatory agency
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertRegulatoryAgency(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "RegulatoryAgencyType", xmlin, path, group);
	}

	/**
	 * Method used to insert relationship
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertRelationship(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertAttributeValue(xmlout, parentElement, "SequenceNumber", xmlin, path, group);
		insertAttributeValue(xmlout, parentElement, XLINK_ALIAS + ":from", xmlin, path, group);
		insertAttributeValue(xmlout, parentElement, XLINK_ALIAS + ":to", xmlin, path, group);
		insertAttributeValue(xmlout, parentElement, XLINK_ALIAS + ":arcrole", xmlin, path, group);
	}

	/**
	 * Method used to insert realtionships
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param groupprefix
	 *            group prefix from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertRelationships(Document xmlin, String path, String groupPrefix, Document xmlout,
			Element parentElement) {
		String[] groupings = findUniqueGroupings(xmlin, path, "RELATIONSHIP", groupPrefix);
		for (String group : groupings)
			insertRelationship(xmlin, path + "RELATIONSHIP/", group, xmlout,
					insertLevels(xmlout, parentElement, "RELATIONSHIP"));
	}

	/**
	 * Method used to insert reporting information
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertReportingInformation(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "ReportingInformationIdentifier", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ReportingPeriodStartDate", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ReportingPeriodEndDate", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "TotalReportingEntriesCount", xmlin, path, group);
	}

	/**
	 * Method used to insert role
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertRole(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertAttributeValue(xmlout, parentElement, "SequenceNumber", xmlin, path, group);
		insertAttributeValue(xmlout, parentElement, XLINK_ALIAS + ":label", xmlin, path, group);
		insertRealEstateAgent(xmlin, path + "REAL_ESTATE_AGENT/", group, xmlout,
				insertLevels(xmlout, parentElement, "REAL_ESTATE_AGENT")); // Not
																			// needed
																			// in
																			// LE
		insertBorrower(xmlin, path + "BORROWER/", group, xmlout, insertLevels(xmlout, parentElement, "BORROWER"));
		insertLicenses(xmlin, path + "LICENSES/", group, xmlout, insertLevels(xmlout, parentElement, "LICENSES"));
		insertPartyRoleIdentifiers(xmlin, path + "PARTY_ROLE_IDENTIFIERS/", group, xmlout,
				insertLevels(xmlout, parentElement, "PARTY_ROLE_IDENTIFIERS"));
		insertRegulatoryAgency(xmlin, path + "REGULATORY_AGENCY/", group, xmlout,
				insertLevels(xmlout, parentElement, "REGULATORY_AGENCY"));
		insertRoleDetail(xmlin, path + "ROLE_DETAIL/", group, xmlout,
				insertLevels(xmlout, parentElement, "ROLE_DETAIL"));
	}

	/**
	 * Method used to insert role detail
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertRoleDetail(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "PartyRoleType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PartyRoleTypeOtherDescription", xmlin, path, group);
	}

	/**
	 * Method used to insert roles
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param groupprefix
	 *            group prefix from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertRoles(Document xmlin, String path, String groupPrefix, Document xmlout,
			Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "ROLE", groupPrefix);
		for (String group : groupings)
			insertRole(xmlin, path + "ROLE/", group, xmlout, insertLevels(xmlout, parentElement, "ROLE"));
	}

	/**
	 * Method used to insert signatories
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param groupprefix
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertSignatories(Document xmlin, String path, String groupPrefix, Document xmlout,
			Element parentElement) {
		String[] groupings = findUniqueGroupings(xmlin, path, "SIGNATORY", groupPrefix);
		for (String group : groupings)
			insertSignatory(xmlin, path + "SIGNATORY/", group, xmlout,
					insertLevels(xmlout, parentElement, "SIGNATORY"));
	}

	/**
	 * Method used to insert signatory
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertSignatory(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertAttributeValue(xmlout, parentElement, "SequenceNumber", xmlin, path, group);
		insertAttributeValue(xmlout, parentElement, XLINK_ALIAS + ":label", xmlin, path, group);
		insertExecution(xmlin, path + "EXECUTION/", group, xmlout, insertLevels(xmlout, parentElement, "EXECUTION"));
	}

	/**
	 * Method used to insert signature
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertSignature(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "XMLDigitalSignatureElement", xmlin, path, group);
	}

	/**
	 * Method used to insert subject property
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertSubjectProperty(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertAddress(xmlin, path + "ADDRESS/", group, xmlout, insertLevels(xmlout, parentElement, "ADDRESS"));
		insertUnparsedLegalDescription(xmlin,
				path + "LEGAL_DESCRIPTIONS/LEGAL_DESCRIPTION/UNPARSED_LEGAL_DESCRIPTIONS/UNPARSED_LEGAL_DESCRIPTION/",
				group, xmlout, insertLevels(xmlout, parentElement,
						"LEGAL_DESCRIPTIONS/LEGAL_DESCRIPTION/UNPARSED_LEGAL_DESCRIPTIONS/UNPARSED_LEGAL_DESCRIPTION"));
		insertLocationIdentifier(xmlin, path + "LOCATION_IDENTIFIER/", group, xmlout,
				insertLevels(xmlout, parentElement, "LOCATION_IDENTIFIER"));
		insertPropertyDetail(xmlin, path + "PROPERTY_DETAIL/", group, xmlout,
				insertLevels(xmlout, parentElement, "PROPERTY_DETAIL"));
		insertPropertyValuations(xmlin, path + "PROPERTY_VALUATIONS/", group, xmlout,
				insertLevels(xmlout, parentElement, "PROPERTY_VALUATIONS"));
		insertSalesContractDetail(xmlin, path + "SALES_CONTRACTS/SALES_CONTRACT/SALES_CONTRACT_DETAIL/", group, xmlout,
				insertLevels(xmlout, parentElement, "SALES_CONTRACTS/SALES_CONTRACT/SALES_CONTRACT_DETAIL"));
	}

	/**
	 * Method used to insert sale contract detail
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertSalesContractDetail(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "PersonalPropertyAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PersonalPropertyIncludedIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "RealPropertyAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "SalesContractAmount", xmlin, path, group);
	}

	/**
	 * Method used to insert servicing
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertServicing(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertServicingDetail(xmlin, path + "SERVICING_DETAIL/", group, xmlout,
				insertLevels(xmlout, parentElement, "SERVICING_DETAIL"));
	}

	/**
	 * Method used to insert servicing detail
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertServicingDetail(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "LoanAcquisitionActualUPBAmount", xmlin, path, group);
	}

	/**
	 * Method used to insert systems signatures
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param groupprefix
	 *            group prefix from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertSystemSignatures(Document xmlin, String path, String groupPrefix, Document xmlout,
			Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "SYSTEM_SIGNATURE", groupPrefix);
		for (String group : groupings)
			insertSignature(xmlin, path + "SYSTEM_SIGNATURE/", group, xmlout,
					insertLevels(xmlout, parentElement, "SYSTEM_SIGNATURE"));
	}

	/**
	 * Method used to insert signatories
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param groupprefix
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertTaxpayerIdentifiers(Document xmlin, String path, String groupPrefix, Document xmlout,
			Element parentElement) {
		String[] groupings = findUniqueGroupings(xmlin, path, "TAXPAYER_IDENTIFIER", groupPrefix);
		for (String group : groupings)
			insertTaxpayerIdentifier(xmlin, path + "TAXPAYER_IDENTIFIER/", group, xmlout,
					insertLevels(xmlout, parentElement, "TAXPAYER_IDENTIFIER"));
	}

	/**
	 * Method used to insert taxpayer identifier
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertTaxpayerIdentifier(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "TaxpayerIdentifierValue", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "TaxpayerIdentifierType", xmlin, path, group);
	}

	/**
	 * Method used to insert taxpayer identifiers
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertTermsOfLoan(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "AssumedLoanAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "DisclosedFullyIndexedRatePercent", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "LienPriorityType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "LoanPurposeType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "MortgageType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "MortgageTypeOtherDescription", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "NoteAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "NoteRatePercent", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "WeightedAverageInterestRatePercent", xmlin, path, group);
	}

	/**
	 * Method used to insert unparsed legal description
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertUnparsedLegalDescription(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "UnparsedLegalDescription", xmlin, path, group);
	}

	/**
	 * Method used to insert URLA
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertURLA(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertURLADetail(xmlin, path + "URLA_DETAIL/", group, xmlout,
				insertLevels(xmlout, parentElement, "URLA_DETAIL"));
	}

	/**
	 * Method used to insert URLA detail
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertURLADetail(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "BorrowerRequestedLoanAmount", xmlin, path, group);
	}

	/**
	 * Method used to insert under writing
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertUnderwriting(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertAutomatedUnderwritings(xmlin, path + "AUTOMATED_UNDERWRITINGS/", group, xmlout,
				insertLevels(xmlout, parentElement, "AUTOMATED_UNDERWRITINGS"));
		insertUnderwritingDetail(xmlin, path + "UNDERWRITING_DETAIL/", group, xmlout,
				insertLevels(xmlout, parentElement, "UNDERWRITING_DETAIL"));
	}

	/**
	 * Method used to insert under writing detail
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertUnderwritingDetail(Document xmlin, String path, String group, Document xmlout,
			Element parentElement) {
		insertDataValue(xmlout, parentElement, "LoanManualUnderwritingIndicator", xmlin, path, group);
	}

	/**
	 * Method used to insert view
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertView(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertAttributeValue(xmlout, parentElement, "SequenceNumber", xmlin, path, group);
		insertViewFile(xmlin, path + "VIEW_FILES/VIEW_FILE/", group, xmlout,
				insertLevels(xmlout, parentElement, "VIEW_FILES/VIEW_FILE"));
	}

	/**
	 * Method used to insert view detail
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param group
	 *            group from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertViewFile(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertForeignObject(xmlin, path + "FOREIGN_OBJECT/", group, xmlout,
				insertLevels(xmlout, parentElement, "FOREIGN_OBJECT"));
	}

	/**
	 * Method used to insert views
	 * 
	 * @param xmlin
	 *            input xml file
	 * @param path
	 *            location of xml file
	 * @param groupprefix
	 *            group prefix from xml file
	 * @param xmlout
	 *            output xml file
	 * @param parentElement
	 *            parent node of xml
	 */
	protected void insertViews(Document xmlin, String path, String groupPrefix, Document xmlout,
			Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "VIEW", groupPrefix);
		for (String group : groupings)
			insertView(xmlin, path + "VIEW/", group, xmlout, insertLevels(xmlout, parentElement, "VIEW"));
	}
	
    public Document transformCsvToXml(String csvFile) {
        System.out.println("Start time: " + new Date().toString());
        // Read csv file
        Document doc = null;
        try {
            InputStream is = new ByteArrayInputStream(csvFile.getBytes(StandardCharsets.UTF_8));
            System.out.println("Input Stream Reader:::::" + is);
            BufferedReader in = new BufferedReader(new InputStreamReader(is));

            // Hack for UCDBuilder... convert csv to XML and write
            System.out.println("Transforming to intermediate format..." + in);

            CsvToXml intermediateTransformer = new CsvToXml();
            Document intermediateDocument = intermediateTransformer.transform(in);
            in.close();

            System.out.println("Intermediate XML:::::" + intermediateDocument);

            // Prune intermediate XML for performance

            try {
                System.out.println("Document pruning Starting.....");
                Utils.removeNodes(intermediateDocument, "/UCDData/DataElement[INCLUDE_IN_XML_INDICATOR='FALSE']");
            } catch (Exception e) {
                System.out.println("Document pruning failed" + e);
            }
            // Transform intermediate XML to UCD
            doc = transform(intermediateDocument);
            System.out.println("Final document:::::" + doc);
            // Remove empty nodes
            Utils.removeEmptyNodes(doc);

        } catch (Exception e) {
            // TODO: handle exception
        }
        System.out.println("End time: " + new Date().toString());
        return doc;
    }
}
