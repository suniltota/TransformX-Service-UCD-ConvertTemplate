package com.actualize.mortgage.transformer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
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

import com.actualize.mortgage.domainmodels.UniformDisclosureResults;
import com.actualize.mortgage.xmlutils.CsvToXml;
import com.actualize.mortgage.xmlutils.JavaVersion;
import com.actualize.mortgage.xmlutils.Utils;


/**
 * Class <code>ConstructUCDXml </code> execute as main class which construct the xml file 
 * 
 *
 */
public class ConstructUCDXml {
	public Document transform(Document xmlin) throws Exception {
		Document xmlout = createDocument();
		xmlout.appendChild(xmlout.createElement(addNamespace("MESSAGE")));
		insertMessage(xmlin, "MESSAGE/", xmlout, xmlout.getDocumentElement());
	    return xmlout;
	}

	private static final String gseAlias = "gse";
	private static final String mismoAlias = "mismo";
	private static final String xlinkAlias = "xlink";
	private static final String gseURI = "http://www.datamodelextension.org";
	private static final String mismoURI = "http://www.mismo.org/residential/2009/schemas";
	private static final String xlinkURI = "http://www.w3.org/1999/xlink";
	private static final String xsiURI   = "http://www.w3.org/2001/XMLSchema-instance";
	
	private DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	private XPath xPath = XPathFactory.newInstance().newXPath();

	private String addNamespace(String tag) {
		return (tag.indexOf(':') == -1 ? mismoAlias+":" : "") + tag;
	}

	/**
	 * 
	 * @param xpath
	 * @return value of xpath
	 */
	private String baseXPath(String xpath) {
		String value = "/UCDData/DataElement[INCLUDE_IN_XML_INDICATOR='TRUE']";
		if (xpath != null)
			value += "[XPATH_VALUE='" + xpath + "']";
		return value;
	}
	
	/**
	 * 
	 * @return created document 
	 * @throws ParserConfigurationException
	 */
	private Document createDocument() throws ParserConfigurationException {
		dbf.setNamespaceAware(true);
		return dbf.newDocumentBuilder().newDocument();
	}
	
	/**
	 * 
	 * @param xmlin input xml file name
	 * @param path path of input xml file
	 * @param endString the endstring
	 * @param groupPrefix
	 * @return array of String 
	 */
	private String[] findGroupings(Document xmlin, String path, String endString, String groupPrefix) {
		String xpath = startingXPath(path + endString + "/", groupPrefix);
		try {
			NodeList nodelist = (NodeList)xPath.compile(xpath).evaluate(xmlin, XPathConstants.NODESET);
			Set<String> groupings = new TreeSet<String>();
			for (int i = 0; i < nodelist.getLength(); i++) {
				String str = (String)xPath.compile("GROUP_IDENTIFIER").evaluate(nodelist.item(i));
				if (endString != null)
					str = str.substring(0, str.indexOf(endString));
				groupings.add(str);
			}
			return groupings.toArray(new String[groupings.size()]);
		} catch (Exception e) {
			System.err.println("Error: invalid xPath \"" + xpath + "\"");
		}
		return null;
	}
	
	private String[] findGroupings(Document xmlin, String path, String endString) {
		return findGroupings(xmlin, path, endString, null);
	}
	
	/**
	 * 
	 * @param xmlin input xml file name
	 * @param dataPointName
	 * @param path path of xml file
	 * @param group
	 * @return value of xpath
	 */
	private String getDataValue(Document xmlin, String dataPointName, String path, String group) {
		String str = null;
		String xpath = baseXPath(path) + "[DATA_POINT_NAME='" + dataPointName + "']" + (group == null ? "" : "[GROUP_IDENTIFIER[starts-with(.,'" + group + "')]]") + "/DATA_VALUE";
		try {
			str = (String)xPath.compile(xpath).evaluate(xmlin);
		} catch (XPathExpressionException e) {
			System.err.println("Error: invalid xPath \"" + xpath + "\"");
		}
		return str;
	}
	
	/**
	 * 
	 * @param xmlout output xml file 
	 * @param parentElement parent node 
	 * @param dataPointName
	 * @param xmlin input xml file name
	 * @param path path of input xml
	 * @param group
	 */
	private void insertAttributeValue(Document xmlout, Element parentElement, String dataPointName, Document xmlin, String path, String group) {
		String str = getDataValue(xmlin, dataPointName, path, group);
		if (str != null && !"".equals(str))
			parentElement.setAttribute(dataPointName, str);
	}
	
	private void insertAttributeValue(Document xmlout, Element parentElement, String dataPointName, Document xmlin, String path) {
		insertAttributeValue(xmlout, parentElement, dataPointName, xmlin, path, null);
	}

	/**
	 * Method used to insert all the data value
	 * @param xmlout output xml file
	 * @param parentElement parent node
	 * @param dataPointName data point
	 * @param xmlin input xml name
	 * @param path path of the xml file
	 * @param group
	 * @return Element object
	 */
	private Element insertDataValue(Document xmlout, Element parentElement, String dataPointName, Document xmlin, String path, String group) {
		Element element = null;
		String str = getDataValue(xmlin, dataPointName, path, group);
		if (str != null && !"".equals(str)) {
			element = (Element)parentElement.appendChild(xmlout.createElement(addNamespace(dataPointName)));
			if ("AggregateAdjustment".equals(dataPointName))
				element.appendChild(xmlout.createTextNode(str));
			else
				element.appendChild(xmlout.createTextNode(toAbsolute(str)));
		}
		return element;
	}

	private Element insertDataValue(Document xmlout, Element parentElement, String dataPointName, Document xmlin, String path) {
		return insertDataValue(xmlout, parentElement, dataPointName, xmlin, path, null);
	}
/**
 * Method used to insert level
 * @param xmlout output xml file name
 * @param parentElement parentt node
 * @param path path of xml file
 * @return Element object
 */
	private Element insertLevels(Document xmlout, Element parentElement, String path) {
		Element elem = parentElement;
		String[] containers = path.split("/");
		for (String container : containers)
			elem = (Element)elem.appendChild(xmlout.createElement(addNamespace(container)));
		return elem;
	}
 /**
  * 
  * @param xpath
  * @param groupPrefix
  * @return
  */
	private String startingXPath(String xpath, String groupPrefix) {
		String value = "/UCDData/DataElement[INCLUDE_IN_XML_INDICATOR='TRUE']";
		if (xpath != null)
			value += "[XPATH_VALUE[starts-with(.,'" + xpath + "')]]";
		if (groupPrefix != null)
			value += "[GROUP_IDENTIFIER[starts-with(.,'" + groupPrefix + "')]]";
		return value;
	}
	
	private String toAbsolute(String str) {
		String s = str;
		try {
			double d = Double.parseDouble(s);
			if (d < 0) // Only convert if absolutely necessary
				s = String.valueOf(Math.abs(d));
		} catch (Exception e) {
		}
		return s;
	}

	private void insertAboutVersion(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		Element element = insertDataValue(xmlout, parentElement, "AboutVersionIdentifier", xmlin, path, group);
		insertAttributeValue(xmlout, element, "IdentifierOwnerURI", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "CreatedDatetime", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "DataVersionIdentifier", xmlin, path, group);
	}
	
	private void insertAboutVersions(Document xmlin, String path, Document xmlout, Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "ABOUT_VERSION");
		for (String group : groupings)
			insertAboutVersion(xmlin, path + "ABOUT_VERSION/", group, xmlout, insertLevels(xmlout, parentElement, "ABOUT_VERSION"));
	}

	private void insertAddress(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "AddressLineText", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "AddressType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "AddressUnitDesignatorType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "AddressUnitIdentifier", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "CityName", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "CountryCode", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PostalCode", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "StateCode", xmlin, path, group);
	}
	
	private void insertAddresses(Document xmlin, String path, String groupPrefix, Document xmlout, Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "ADDRESS", groupPrefix);
		for (String group : groupings)
			insertAddress(xmlin, path + "ADDRESS/", group, xmlout, insertLevels(xmlout, parentElement, "ADDRESS"));
	}

	private void insertAdjustment(Document xmlin, String path, Document xmlout, Element parentElement) {
        insertInterestRateAdjustment(xmlin, path + "INTEREST_RATE_ADJUSTMENT/", xmlout, insertLevels(xmlout, parentElement, "INTEREST_RATE_ADJUSTMENT"));
        insertPrincipalAndInterestPaymentAdjustment(xmlin, path + "PRINCIPAL_AND_INTEREST_PAYMENT_ADJUSTMENT/", xmlout, insertLevels(xmlout, parentElement, "PRINCIPAL_AND_INTEREST_PAYMENT_ADJUSTMENT"));
	}
	
	private void insertAmortizationRule(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "AmortizationType", xmlin, path);
	}
	
	private void insertAuditTrailEntryDetail(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "EntryDatetime", xmlin, path);
		insertDataValue(xmlout, parentElement, "EventType", xmlin, path);
		insertDataValue(xmlout, parentElement, "EventTypeOtherDescription", xmlin, path);
	}
	
	private void insertAutomatedUnderwriting(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "AutomatedUnderwritingCaseIdentifier", xmlin, path);
		insertDataValue(xmlout, parentElement, "AutomatedUnderwritingSystemType", xmlin, path);
		insertDataValue(xmlout, parentElement, "AutomatedUnderwritingSystemTypeOtherDescription", xmlin, path);
	}
	
	private void insertAutomatedUnderwritings(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertAutomatedUnderwriting(xmlin, path + "AUTOMATED_UNDERWRITING/", xmlout, insertLevels(xmlout, parentElement, "AUTOMATED_UNDERWRITING"));
	}
	
	private void insertBuydown(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertBuydownOccurences(xmlin, path + "BUYDOWN_OCCURRENCES/", xmlout, insertLevels(xmlout, parentElement, "BUYDOWN_OCCURRENCES"));
		insertBuydownRule(xmlin, path + "BUYDOWN_RULE/", xmlout, insertLevels(xmlout, parentElement, "BUYDOWN_RULE"));
	}
	
	private void insertBuydownOccurence(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "BuydownInitialEffectiveInterestRatePercent", xmlin, path, group);
	}
	
	private void insertBuydownOccurences(Document xmlin, String path, Document xmlout, Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "BUYDOWN_OCCURRENCE");
		for (String group : groupings)
			insertBuydownOccurence(xmlin, path + "BUYDOWN_OCCURRENCE/", group, xmlout, insertLevels(xmlout, parentElement, "BUYDOWN_OCCURRENCE"));
	}
	
	private void insertBuydownRule(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "BuydownChangeFrequencyMonthsCount", xmlin, path);
		insertDataValue(xmlout, parentElement, "BuydownDurationMonthsCount", xmlin, path);
		insertDataValue(xmlout, parentElement, "BuydownIncreaseRatePercent", xmlin, path);
		insertExtension(xmlin, path + "EXTENSION/", null, xmlout, insertLevels(xmlout, parentElement, "EXTENSION"));
	}

	private void insertCashToCloseItem(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureCashToCloseItemAmountChangedIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureCashToCloseItemChangeDescription", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureCashToCloseItemEstimatedAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureCashToCloseItemFinalAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureCashToCloseItemPaymentType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureCashToCloseItemType", xmlin, path, group);
	}
	
	private void insertCashToCloseItems(Document xmlin, String path, Document xmlout, Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "CASH_TO_CLOSE_ITEM");
		for (String group : groupings)
			insertCashToCloseItem(xmlin, path + "CASH_TO_CLOSE_ITEM/", group, xmlout, insertLevels(xmlout, parentElement, "CASH_TO_CLOSE_ITEM"));
	}
	
	private void insertClosingAdjustmentItem(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertClosingAdjustmentItemDetail(xmlin, path + "CLOSING_ADJUSTMENT_ITEM_DETAIL/", group, xmlout, insertLevels(xmlout, parentElement, "CLOSING_ADJUSTMENT_ITEM_DETAIL"));
		insertClosingAdjustmentItemPaidBy(xmlin, path + "CLOSING_ADJUSTMENT_ITEM_PAID_BY/", group, xmlout, insertLevels(xmlout, parentElement, "CLOSING_ADJUSTMENT_ITEM_PAID_BY"));
	}
	
	private void insertClosingAdjustmentItemDetail(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertAttributeValue(xmlout, parentElement, "DisplayLabelText", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ClosingAdjustmentItemAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ClosingAdjustmentItemPaidOutsideOfClosingIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ClosingAdjustmentItemType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ClosingAdjustmentItemTypeOtherDescription", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureSectionType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureSubsectionType", xmlin, path, group);
	}
	
	private void insertClosingAdjustmentItemPaidBy(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertLegalEntity(xmlin, path + "LEGAL_ENTITY/", group, xmlout, insertLevels(xmlout, parentElement, "LEGAL_ENTITY"));
	}
	
	private void insertClosingAdjustmentItems(Document xmlin, String path, Document xmlout, Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "CLOSING_ADJUSTMENT_ITEM");
		for (String group : groupings)
			insertClosingAdjustmentItem(xmlin, path + "CLOSING_ADJUSTMENT_ITEM/", group, xmlout, insertLevels(xmlout, parentElement, "CLOSING_ADJUSTMENT_ITEM"));
	}
	
	private void insertClosingCostFund(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "ClosingCostFundAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "FundsType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureSectionType", xmlin, path, group);
	}
	
	private void insertClosingCostFunds(Document xmlin, String path, Document xmlout, Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "CLOSING_COST_FUND");
		for (String group : groupings)
			insertClosingCostFund(xmlin, path + "CLOSING_COST_FUND/", group, xmlout, insertLevels(xmlout, parentElement, "CLOSING_COST_FUND"));
	}
	
	private void insertClosingInformation(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertClosingAdjustmentItems(xmlin, path + "CLOSING_ADJUSTMENT_ITEMS/", xmlout, insertLevels(xmlout, parentElement, "CLOSING_ADJUSTMENT_ITEMS"));
		insertClosingCostFunds(xmlin, path + "CLOSING_COST_FUNDS/", xmlout, insertLevels(xmlout, parentElement, "CLOSING_COST_FUNDS"));
		insertClosingInformationDetail(xmlin, path + "CLOSING_INFORMATION_DETAIL/", xmlout, insertLevels(xmlout, parentElement, "CLOSING_INFORMATION_DETAIL"));
		insertPrepaidItems(xmlin, path + "PREPAID_ITEMS/", xmlout, insertLevels(xmlout, parentElement, "PREPAID_ITEMS"));
		insertProrationItems(xmlin, path + "PRORATION_ITEMS/", xmlout, insertLevels(xmlout, parentElement, "PRORATION_ITEMS"));
	}
	
	private void insertClosingInformationDetail(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "CashFromBorrowerAtClosingAmount", xmlin, path);
		insertDataValue(xmlout, parentElement, "CashFromSellerAtClosingAmount", xmlin, path);
		insertDataValue(xmlout, parentElement, "CashToBorrowerAtClosingAmount", xmlin, path);
		insertDataValue(xmlout, parentElement, "CashToSellerAtClosingAmount", xmlin, path);
		insertDataValue(xmlout, parentElement, "ClosingAgentOrderNumberIdentifier", xmlin, path);
		insertDataValue(xmlout, parentElement, "ClosingDate", xmlin, path);
		insertDataValue(xmlout, parentElement, "ClosingRateSetDate", xmlin, path);
		insertDataValue(xmlout, parentElement, "CurrentRateSetDate", xmlin, path);
		insertDataValue(xmlout, parentElement, "DisbursementDate", xmlin, path);
	}

	private void insertCollateral(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertSubjectProperty(xmlin, path + "SUBJECT_PROPERTY/", xmlout, insertLevels(xmlout, parentElement, "SUBJECT_PROPERTY"));
	}

	private void insertCollaterals(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertCollateral(xmlin, path + "COLLATERAL/", xmlout, insertLevels(xmlout, parentElement, "COLLATERAL"));
	}

	private void insertConstruction(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "ConstructionLoanTotalTermMonthsCount", xmlin, path);
		insertDataValue(xmlout, parentElement, "ConstructionLoanType", xmlin, path);
		insertDataValue(xmlout, parentElement, "ConstructionPeriodNumberOfMonthsCount", xmlin, path);
	}

	private void insertContactPoint(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertContactPointEmail(xmlin, path + "CONTACT_POINT_EMAIL/", group, xmlout, insertLevels(xmlout, parentElement, "CONTACT_POINT_EMAIL"));
		insertContactPointTelephone(xmlin, path + "CONTACT_POINT_TELEPHONE/", group, xmlout, insertLevels(xmlout, parentElement, "CONTACT_POINT_TELEPHONE"));
	}

	private void insertContactPointEmail(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "ContactPointEmailValue", xmlin, path, group);
	}

	private void insertContactPointTelephone(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "ContactPointTelephoneValue", xmlin, path, group);
	}

	private void insertContactPoints(Document xmlin, String path, String groupPrefix, Document xmlout, Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "CONTACT_POINT", groupPrefix);
		for (String group : groupings)
			insertContactPoint(xmlin, path + "CONTACT_POINT/", group, xmlout, insertLevels(xmlout, parentElement, "CONTACT_POINT"));
	}

    private void insertDeal(Document xmlin, String path, Document xmlout, Element parentElement) {
        insertCollaterals(xmlin, path + "COLLATERALS/", xmlout, insertLevels(xmlout, parentElement, "COLLATERALS"));
        insertLiabilities(xmlin, path + "LIABILITIES/", xmlout, insertLevels(xmlout, parentElement, "LIABILITIES"));
        insertLoans(xmlin, path + "LOANS/", xmlout, insertLevels(xmlout, parentElement, "LOANS"));
        insertParties(xmlin, path + "PARTIES/", xmlout, insertLevels(xmlout, parentElement, "PARTIES"));
        insertRelationships(xmlin, path + "RELATIONSHIPS/", xmlout, insertLevels(xmlout, parentElement, "RELATIONSHIPS"));
	}
	
	private void insertDocument(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertAttributeValue(xmlout, parentElement, "MISMOReferenceModelIdentifier", xmlin, path);
        insertAuditTrailEntryDetail(xmlin, path + "AUDIT_TRAIL/AUDIT_TRAIL_ENTRIES/AUDIT_TRAIL_ENTRY/AUDIT_TRAIL_ENTRY_DETAIL/", xmlout, insertLevels(xmlout, parentElement, "AUDIT_TRAIL/AUDIT_TRAIL_ENTRIES/AUDIT_TRAIL_ENTRY/AUDIT_TRAIL_ENTRY_DETAIL"));
        insertDeal(xmlin, path + "DEAL_SETS/DEAL_SET/DEALS/DEAL/", xmlout, insertLevels(xmlout, parentElement, "DEAL_SETS/DEAL_SET/DEALS/DEAL"));
        insertSystemSignatures(xmlin, path + "SYSTEM_SIGNATORIES/", xmlout, insertLevels(xmlout, parentElement, "SYSTEM_SIGNATORIES"));
        insertSignatories(xmlin, path + "SIGNATORIES/", xmlout, insertLevels(xmlout, parentElement, "SIGNATORIES"));
        insertViews(xmlin, path + "VIEWS/", xmlout, insertLevels(xmlout, parentElement, "VIEWS"));
        insertAboutVersions(xmlin, path + "ABOUT_VERSIONS/", xmlout, insertLevels(xmlout, parentElement, "ABOUT_VERSIONS"));
        insertDocumentClassification(xmlin, path + "DOCUMENT_CLASSIFICATION/", xmlout, insertLevels(xmlout, parentElement, "DOCUMENT_CLASSIFICATION"));
	}
	
	private void insertDocumentClass(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "DocumentType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "DocumentTypeOtherDescription", xmlin, path, group);
	}
	
	private void insertDocumentClasses(Document xmlin, String path, Document xmlout, Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "DOCUMENT_CLASS");
		for (String group : groupings)
			insertDocumentClass(xmlin, path + "DOCUMENT_CLASS/", group, xmlout, insertLevels(xmlout, parentElement, "DOCUMENT_CLASS"));
	}
	
	private void insertDocumentClassification(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertDocumentClasses(xmlin, path + "DOCUMENT_CLASSES/", xmlout, insertLevels(xmlout, parentElement, "DOCUMENT_CLASSES"));
		insertDocumentClassificationDetail(xmlin, path + "DOCUMENT_CLASSIFICATION_DETAIL/", xmlout, insertLevels(xmlout, parentElement, "DOCUMENT_CLASSIFICATION_DETAIL"));
	}
	
	private void insertDocumentClassificationDetail(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "DocumentFormIssuingEntityNameType", xmlin, path);
		insertDataValue(xmlout, parentElement, "DocumentFormIssuingEntityVersionIdentifier", xmlin, path);
		insertExtension(xmlin, path + "EXTENSION/", null, xmlout, insertLevels(xmlout, parentElement, "EXTENSION"));
	}
	
	private void insertDocumentSpecificDataSet(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertIntegratedDisclosure(xmlin, path + "INTEGRATED_DISCLOSURE/", xmlout, insertLevels(xmlout, parentElement, "INTEGRATED_DISCLOSURE"));
	}
	
	private void insertEscrow(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertEscrowDetail(xmlin, path + "ESCROW_DETAIL/", xmlout, insertLevels(xmlout, parentElement, "ESCROW_DETAIL"));
		insertEscrowItems(xmlin, path + "ESCROW_ITEMS/", xmlout, insertLevels(xmlout, parentElement, "ESCROW_ITEMS"));
	}
	
	private void insertEscrowDetail(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "EscrowAggregateAccountingAdjustmentAmount", xmlin, path);
	}
	
	private void insertEscrowItem(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertEscrowItemDetail(xmlin, path + "ESCROW_ITEM_DETAIL/", group, xmlout, insertLevels(xmlout, parentElement, "ESCROW_ITEM_DETAIL"));
		insertEscrowItemPayments(xmlin, path + "ESCROW_ITEM_PAYMENTS/", group, xmlout, insertLevels(xmlout, parentElement, "ESCROW_ITEM_PAYMENTS"));
	}
	
	private void insertEscrowItemDetail(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertAttributeValue(xmlout, parentElement, "DisplayLabelText", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "EscrowCollectedNumberOfMonthsCount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "EscrowItemType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "EscrowItemTypeOtherDescription", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "EscrowMonthlyPaymentAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "FeePaidToType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "FeePaidToTypeOtherDescription", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureSectionType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "RegulationZPointsAndFeesIndicator", xmlin, path, group);
		insertExtension(xmlin, path + "EXTENSION/", null, xmlout, insertLevels(xmlout, parentElement, "EXTENSION"));
	}
	
	private void insertEscrowItemPayment(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "EscrowItemActualPaymentAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "EscrowItemPaymentPaidByType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "EscrowItemPaymentTimingType", xmlin, path, group);
	}
	
	private void insertEscrowItemPayments(Document xmlin, String path, String groupPrefix, Document xmlout, Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "ESCROW_ITEM_PAYMENT", groupPrefix);
		for (String group : groupings)
			insertEscrowItemPayment(xmlin, path + "ESCROW_ITEM_PAYMENT/", group, xmlout, insertLevels(xmlout, parentElement, "ESCROW_ITEM_PAYMENT"));
	}
	
	private void insertEscrowItems(Document xmlin, String path, Document xmlout, Element parentElement) {
		String[] groupings;
		groupings = findGroupings(xmlin, path, "ESCROW_ITEM");
		for (String group : groupings)
			insertEscrowItem(xmlin, path + "ESCROW_ITEM/", group, xmlout, insertLevels(xmlout, parentElement, "ESCROW_ITEM"));
	}
	
	private void insertEstimatedPropertyCost(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertEstimatedPropertyCostComponents(xmlin, path + "ESTIMATED_PROPERTY_COST_COMPONENTS/", xmlout, insertLevels(xmlout, parentElement, "ESTIMATED_PROPERTY_COST_COMPONENTS"));
		insertEstimatedPropertyCostDetail(xmlin, path + "ESTIMATED_PROPERTY_COST_DETAIL/", xmlout, insertLevels(xmlout, parentElement, "ESTIMATED_PROPERTY_COST_DETAIL"));
	}
	
	private void insertEstimatedPropertyCostComponent(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "ProjectedPaymentEscrowedType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ProjectedPaymentEstimatedTaxesInsuranceAssessmentComponentType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ProjectedPaymentEstimatedTaxesInsuranceAssessmentComponentTypeOtherDescription", xmlin, path, group);
	}
	
	private void insertEstimatedPropertyCostComponents(Document xmlin, String path, Document xmlout, Element parentElement) {
		String[] groupings;
		groupings = findGroupings(xmlin, path, "ESTIMATED_PROPERTY_COST_COMPONENT");
		for (String group : groupings)
			insertEstimatedPropertyCostComponent(xmlin, path + "ESTIMATED_PROPERTY_COST_COMPONENT/", group, xmlout, insertLevels(xmlout, parentElement, "ESTIMATED_PROPERTY_COST_COMPONENT"));
	}
	
	private void insertEstimatedPropertyCostDetail(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "ProjectedPaymentEstimatedTaxesInsuranceAssessmentTotalAmount", xmlin, path);
	}
	
	private void insertExecutionDetail(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "ActualSignatureType", xmlin, path);
		insertDataValue(xmlout, parentElement, "ActualSignatureTypeOtherDescription", xmlin, path);
		insertDataValue(xmlout, parentElement, "ExecutionDate", xmlin, path);
		insertDataValue(xmlout, parentElement, "ExecutionDatetime", xmlin, path);
	}
	
	private void insertExemption(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "AbilityToRepayExemptionReasonType", xmlin, path);
	}
	
	private void insertExtension(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertOther(xmlin, path + "OTHER/", group, xmlout, insertLevels(xmlout, parentElement, "OTHER"));
	}

	private void insertFee(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertFeeDetail(xmlin, path + "FEE_DETAIL/", group, xmlout, insertLevels(xmlout, parentElement, "FEE_DETAIL"));
		insertFeePaidTo(xmlin, path + "FEE_PAID_TO/", group, xmlout, insertLevels(xmlout, parentElement, "FEE_PAID_TO"));
		insertFeePayments(xmlin, path + "FEE_PAYMENTS/", group, xmlout, insertLevels(xmlout, parentElement, "FEE_PAYMENTS"));
	}
	
	private void insertFeeDetail(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertAttributeValue(xmlout, parentElement, "DisplayLabelText", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "FeeActualTotalAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "FeePaidToType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "FeePaidToTypeOtherDescription", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "FeePercentBasisType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "FeeTotalPercent", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "FeeType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "FeeTypeOtherDescription", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureSectionType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "OptionalCostIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "RegulationZPointsAndFeesIndicator", xmlin, path, group);
	}
	
	private void insertFeeInformation(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertFees(xmlin, path + "FEES/", xmlout, insertLevels(xmlout, parentElement, "FEES"));
		insertFeeSummaryDetail(xmlin, path + "FEES_SUMMARY/FEE_SUMMARY_DETAIL/", xmlout, insertLevels(xmlout, parentElement, "FEES_SUMMARY/FEE_SUMMARY_DETAIL"));
	}

	private void insertFeePaidTo(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertLegalEntity(xmlin, path + "LEGAL_ENTITY/", group, xmlout, insertLevels(xmlout, parentElement, "LEGAL_ENTITY"));
	}
	
	private void insertFeePayment(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "FeeActualPaymentAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "FeePaymentPaidByType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "FeePaymentPaidOutsideOfClosingIndicator", xmlin, path, group);
	}
	
	private void insertFeePayments(Document xmlin, String path, String groupPrefix, Document xmlout, Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "FEE_PAYMENT", groupPrefix);
		for (String group : groupings)
			insertFeePayment(xmlin, path + "FEE_PAYMENT/", group, xmlout, insertLevels(xmlout, parentElement, "FEE_PAYMENT"));
	}
	
	private void insertFees(Document xmlin, String path, Document xmlout, Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "FEE");
		for (String group : groupings)
			insertFee(xmlin, path + "FEE/", group, xmlout, insertLevels(xmlout, parentElement, "FEE"));
	}
	
	private void insertFeeSummaryDetail(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "APRPercent", xmlin, path);
		insertDataValue(xmlout, parentElement, "FeeSummaryTotalAmountFinancedAmount", xmlin, path);
		insertDataValue(xmlout, parentElement, "FeeSummaryTotalFinanceChargeAmount", xmlin, path);
		insertDataValue(xmlout, parentElement, "FeeSummaryTotalInterestPercent", xmlin, path);
		insertDataValue(xmlout, parentElement, "FeeSummaryTotalOfAllPaymentsAmount", xmlin, path);
	}
	
	private void insertForeclosure(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertForeclosureDetail(xmlin, path + "FORECLOSURE_DETAIL/", group, xmlout, insertLevels(xmlout, parentElement, "FORECLOSURE_DETAIL"));
	}
	
	private void insertForeclosureDetail(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "DeficiencyRightsPreservedIndicator", xmlin, path, group);
	}
	
	private void insertForeclosures(Document xmlin, String path, Document xmlout, Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "FORECLOSURE");
		for (String group : groupings)
			insertForeclosure(xmlin, path + "FORECLOSURE/", group, xmlout, insertLevels(xmlout, parentElement, "FORECLOSURE"));
	}

	private void insertForeignObject(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertLevels(xmlout, parentElement, "EmbeddedContentXML"); // Placeholder for Base64 document
		parentElement.appendChild(xmlout.createElement(addNamespace("MIMETypeIdentifier"))).appendChild(xmlout.createTextNode("application/pdf"));
		parentElement.appendChild(xmlout.createElement(addNamespace("ObjectEncodingType"))).appendChild(xmlout.createTextNode("Base64"));
		parentElement.appendChild(xmlout.createElement(addNamespace("ObjectName"))).appendChild(xmlout.createTextNode("ClosingDisclosure.pdf"));
	}

	private void insertHighCostMortgage(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "AveragePrimeOfferRatePercent", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "RegulationZExcludedBonaFideDiscountPointsIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "RegulationZExcludedBonaFideDiscountPointsPercent", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "RegulationZTotalAffiliateFeesAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "RegulationZTotalLoanAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "RegulationZTotalPointsAndFeesAmount", xmlin, path, group);
	}
	
	private void insertHighCostMortgages(Document xmlin, String path, Document xmlout, Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "HIGH_COST_MORTGAGE");
		for (String group : groupings)
			insertHighCostMortgage(xmlin, path + "HIGH_COST_MORTGAGE/", group, xmlout, insertLevels(xmlout, parentElement, "HIGH_COST_MORTGAGE"));
	}
	
	private void insertIndexRule(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "IndexType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "IndexTypeOtherDescription", xmlin, path, group);
	}
	
	private void insertIndexRules(Document xmlin, String path, Document xmlout, Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "INDEX_RULE");
		for (String group : groupings)
			insertIndexRule(xmlin, path + "INDEX_RULE/", group, xmlout, insertLevels(xmlout, parentElement, "INDEX_RULE"));
	}
	
	private void insertIndividual(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertContactPoints(xmlin, path + "CONTACT_POINTS/", group, xmlout, insertLevels(xmlout, parentElement, "CONTACT_POINTS"));
		insertName(xmlin, path + "NAME/", group, xmlout, insertLevels(xmlout, parentElement, "NAME"));
	}
	
	private void insertIntegratedDisclosure(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertCashToCloseItems(xmlin, path + "CASH_TO_CLOSE_ITEMS/", xmlout, insertLevels(xmlout, parentElement, "CASH_TO_CLOSE_ITEMS"));
		insertEstimatedPropertyCost(xmlin, path + "ESTIMATED_PROPERTY_COST/", xmlout, insertLevels(xmlout, parentElement, "ESTIMATED_PROPERTY_COST"));
		insertIntegratedDisclosureDetail(xmlin, path + "INTEGRATED_DISCLOSURE_DETAIL/", xmlout, insertLevels(xmlout, parentElement, "INTEGRATED_DISCLOSURE_DETAIL"));
		insertIntegratedDisclosureSectionSummaries(xmlin, path + "INTEGRATED_DISCLOSURE_SECTION_SUMMARIES/", xmlout, insertLevels(xmlout, parentElement, "INTEGRATED_DISCLOSURE_SECTION_SUMMARIES"));
		insertProjectedPayments(xmlin, path + "PROJECTED_PAYMENTS/", xmlout, insertLevels(xmlout, parentElement, "PROJECTED_PAYMENTS"));
	}
	
	private void insertIntegratedDisclosureDetail(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "FirstYearTotalEscrowPaymentAmount", xmlin, path);
		insertDataValue(xmlout, parentElement, "FirstYearTotalEscrowPaymentDescription", xmlin, path);
		insertDataValue(xmlout, parentElement, "FirstYearTotalNonEscrowPaymentAmount", xmlin, path);
		insertDataValue(xmlout, parentElement, "FirstYearTotalNonEscrowPaymentDescription", xmlin, path);
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureHomeEquityLoanIndicator", xmlin, path);
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureIssuedDate", xmlin, path);
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureLoanProductDescription", xmlin, path);
	}
	
	private void insertIntegratedDisclosureSectionSummaries(Document xmlin, String path, Document xmlout, Element parentElement) {
		String[] groupings;
		groupings = findGroupings(xmlin, path, "INTEGRATED_DISCLOSURE_SECTION_SUMMARY");
		if (groupings != null)
			for (String group : groupings)
				insertIntegratedDisclosureSectionSummary(xmlin, path + "INTEGRATED_DISCLOSURE_SECTION_SUMMARY/", group, xmlout, insertLevels(xmlout, parentElement, "INTEGRATED_DISCLOSURE_SECTION_SUMMARY"));
	}
	
	private void insertIntegratedDisclosureSectionSummary(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertIntegratedDisclosureSectionSummaryDetail(xmlin, path + "INTEGRATED_DISCLOSURE_SECTION_SUMMARY_DETAIL/", group, xmlout, insertLevels(xmlout, parentElement, "INTEGRATED_DISCLOSURE_SECTION_SUMMARY_DETAIL"));
		insertIntegratedDisclosureSubsectionPayments(xmlin, path + "INTEGRATED_DISCLOSURE_SUBSECTION_PAYMENTS/", group, xmlout, insertLevels(xmlout, parentElement, "INTEGRATED_DISCLOSURE_SUBSECTION_PAYMENTS"));
	}
	
	private void insertIntegratedDisclosureSectionSummaryDetail(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureSectionTotalAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureSectionType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureSubsectionType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureSubsectionTypeOtherDescription", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "LenderCreditToleranceCureAmount", xmlin, path, group);
	}
	
	private void insertIntegratedDisclosureSubsectionPayment(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureSubsectionPaidByType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureSubsectionPaymentAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureSubsectionPaymentTimingType", xmlin, path, group);
	}
	
	private void insertIntegratedDisclosureSubsectionPayments(Document xmlin, String path, String groupPrefix, Document xmlout, Element parentElement) {
		String[] groupings;
		groupings = findGroupings(xmlin, path, "INTEGRATED_DISCLOSURE_SUBSECTION_PAYMENT", groupPrefix);
		for (String group : groupings)
			insertIntegratedDisclosureSubsectionPayment(xmlin, path + "INTEGRATED_DISCLOSURE_SUBSECTION_PAYMENT/", group, xmlout, insertLevels(xmlout, parentElement, "INTEGRATED_DISCLOSURE_SUBSECTION_PAYMENT"));
	}
	
	private void insertInterestOnly(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "InterestOnlyTermMonthsCount", xmlin, path);
	}
	
	private void insertInterestRateAdjustment(Document xmlin, String path, Document xmlout, Element parentElement) {
        insertIndexRules(xmlin, path + "INDEX_RULES/", xmlout, insertLevels(xmlout, parentElement, "INDEX_RULES"));
        insertInterestRateLifetimeAdjustmentRule(xmlin, path + "INTEREST_RATE_LIFETIME_ADJUSTMENT_RULE/", xmlout, insertLevels(xmlout, parentElement, "INTEREST_RATE_LIFETIME_ADJUSTMENT_RULE"));
        insertInterestRatePerChangeAdjustmentRules(xmlin, path + "INTEREST_RATE_PER_CHANGE_ADJUSTMENT_RULES/", xmlout, insertLevels(xmlout, parentElement, "INTEREST_RATE_PER_CHANGE_ADJUSTMENT_RULES"));
		insertExtension(xmlin, path + "EXTENSION/", null, xmlout, insertLevels(xmlout, parentElement, "EXTENSION"));
	}
	
	private void insertInterestRateLifetimeAdjustmentRule(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "CeilingRatePercent", xmlin, path);
		insertDataValue(xmlout, parentElement, "CeilingRatePercentEarliestEffectiveMonthsCount", xmlin, path);
		insertDataValue(xmlout, parentElement, "FirstRateChangeMonthsCount", xmlin, path);
		insertDataValue(xmlout, parentElement, "FloorRatePercent", xmlin, path);
		insertDataValue(xmlout, parentElement, "MarginRatePercent", xmlin, path);
	}
	
	private void insertInterestRatePerChangeAdjustmentRule(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "AdjustmentRuleType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PerChangeMaximumIncreaseRatePercent", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PerChangeRateAdjustmentFrequencyMonthsCount", xmlin, path, group);
	}
	
	private void insertInterestRatePerChangeAdjustmentRules(Document xmlin, String path, Document xmlout, Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "INTEREST_RATE_PER_CHANGE_ADJUSTMENT_RULE");
		for (String group : groupings)
			insertInterestRatePerChangeAdjustmentRule(xmlin, path + "INTEREST_RATE_PER_CHANGE_ADJUSTMENT_RULE/", group, xmlout, insertLevels(xmlout, parentElement, "INTEREST_RATE_PER_CHANGE_ADJUSTMENT_RULE"));
	}
	
	private void insertLateChargeRule(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "LateChargeAmount", xmlin, path);
		insertDataValue(xmlout, parentElement, "LateChargeGracePeriodDaysCount", xmlin, path);
		insertDataValue(xmlout, parentElement, "LateChargeRatePercent", xmlin, path);
		insertDataValue(xmlout, parentElement, "LateChargeType", xmlin, path);
	}
	
	private void insertLegalEntity(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertLegalEntityDetail(xmlin, path + "LEGAL_ENTITY_DETAIL/", group, xmlout, insertLevels(xmlout, parentElement, "LEGAL_ENTITY_DETAIL"));
	}
	
	private void insertLegalEntityDetail(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "FullName", xmlin, path, group);
	}
	
	private void insertLiabilities(Document xmlin, String path, Document xmlout, Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "LIABILITY");
		for (String group : groupings)
			insertLiability(xmlin, path + "LIABILITY/", group, xmlout, insertLevels(xmlout, parentElement, "LIABILITY"));
	}
	
	private void insertLiability(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
        insertLiabilityDetail(xmlin, path + "LIABILITY_DETAIL/", group, xmlout, insertLevels(xmlout, parentElement, "LIABILITY_DETAIL"));
        insertLiabilityHolder(xmlin, path + "LIABILITY_HOLDER/", group, xmlout, insertLevels(xmlout, parentElement, "LIABILITY_HOLDER"));
        insertPayoff(xmlin, path + "PAYOFF/", group, xmlout, insertLevels(xmlout, parentElement, "PAYOFF"));
	}
	
	private void insertLiabilityDetail(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertAttributeValue(xmlout, parentElement, "DisplayLabelText", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "LiabilityDescription", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "LiabilityType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "LiabilityTypeOtherDescription", xmlin, path, group);
		insertExtension(xmlin, path + "EXTENSION/", group, xmlout, insertLevels(xmlout, parentElement, "EXTENSION"));
	}
	
	private void insertLiabilityHolder(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
        insertName(xmlin, path + "NAME/", group, xmlout, insertLevels(xmlout, parentElement, "NAME"));
	}
	
	private void insertLicense(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertLicenseDetail(xmlin, path + "LICENSE_DETAIL/", group, xmlout, insertLevels(xmlout, parentElement, "LICENSE_DETAIL"));
	}
	
	private void insertLicenseDetail(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "LicenseAuthorityLevelType", xmlin, path, group);
		Element element = insertDataValue(xmlout, parentElement, "LicenseIdentifier", xmlin, path, group);
		insertAttributeValue(xmlout, element, "IdentifierOwnerURI", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "LicenseIssueDate", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "LicenseIssuingAuthorityName", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "LicenseIssuingAuthorityStateCode", xmlin, path, group);
	}
	
	private void insertLicenses(Document xmlin, String path, String groupPrefix, Document xmlout, Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "LICENSE", groupPrefix);
		for (String group : groupings)
			insertLicense(xmlin, path + "LICENSE/", group, xmlout, insertLevels(xmlout, parentElement, "LICENSE"));
	}
	
	private void insertLoan(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertAdjustment(xmlin, path + "ADJUSTMENT/", xmlout, insertLevels(xmlout, parentElement, "ADJUSTMENT"));
		insertAmortizationRule(xmlin, path + "AMORTIZATION/AMORTIZATION_RULE/", xmlout, insertLevels(xmlout, parentElement, "AMORTIZATION/AMORTIZATION_RULE"));
		insertBuydown(xmlin, path + "BUYDOWN/", xmlout, insertLevels(xmlout, parentElement, "BUYDOWN"));
		insertClosingInformation(xmlin, path + "CLOSING_INFORMATION/", xmlout, insertLevels(xmlout, parentElement, "CLOSING_INFORMATION"));
		insertConstruction(xmlin, path + "CONSTRUCTION/", xmlout, insertLevels(xmlout, parentElement, "CONSTRUCTION"));
		insertDocumentSpecificDataSet(xmlin, path + "DOCUMENT_SPECIFIC_DATA_SETS/DOCUMENT_SPECIFIC_DATA_SET/", xmlout, insertLevels(xmlout, parentElement, "DOCUMENT_SPECIFIC_DATA_SETS/DOCUMENT_SPECIFIC_DATA_SET"));
		insertEscrow(xmlin, path + "ESCROW/", xmlout, insertLevels(xmlout, parentElement, "ESCROW"));
		insertFeeInformation(xmlin, path + "FEE_INFORMATION/", xmlout, insertLevels(xmlout, parentElement, "FEE_INFORMATION"));
		insertForeclosures(xmlin, path + "FORECLOSURES/", xmlout, insertLevels(xmlout, parentElement, "FORECLOSURES"));
		insertHighCostMortgages(xmlin, path + "HIGH_COST_MORTGAGES/", xmlout, insertLevels(xmlout, parentElement, "HIGH_COST_MORTGAGES"));
		insertInterestOnly(xmlin, path + "INTEREST_ONLY/", xmlout, insertLevels(xmlout, parentElement, "INTEREST_ONLY"));
		insertLateChargeRule(xmlin, path + "LATE_CHARGE/LATE_CHARGE_RULE/", xmlout, insertLevels(xmlout, parentElement, "LATE_CHARGE/LATE_CHARGE_RULE"));
		insertLoanDetail(xmlin, path + "LOAN_DETAIL/", xmlout, insertLevels(xmlout, parentElement, "LOAN_DETAIL"));
		insertLoanIdentifiers(xmlin, path + "LOAN_IDENTIFIERS/", xmlout, insertLevels(xmlout, parentElement, "LOAN_IDENTIFIERS"));
		insertLoanProduct(xmlin, path + "LOAN_PRODUCT/", xmlout, insertLevels(xmlout, parentElement, "LOAN_PRODUCT"));
		insertMaturityRule(xmlin, path + "MATURITY/MATURITY_RULE/", xmlout, insertLevels(xmlout, parentElement, "MATURITY/MATURITY_RULE"));
		insertMIDataDetail(xmlin, path + "MI_DATA/MI_DATA_DETAIL/", xmlout, insertLevels(xmlout, parentElement, "MI_DATA/MI_DATA_DETAIL"));
		insertNegativeAmortization(xmlin, path + "NEGATIVE_AMORTIZATION/", xmlout, insertLevels(xmlout, parentElement, "NEGATIVE_AMORTIZATION"));
		insertPayment(xmlin, path + "PAYMENT/", xmlout, insertLevels(xmlout, parentElement, "PAYMENT"));
		insertPrepaymentPenalty(xmlin, path + "PREPAYMENT_PENALTY/", xmlout, insertLevels(xmlout, parentElement, "PREPAYMENT_PENALTY"));
		insertQualifiedMortgage(xmlin, path + "QUALIFIED_MORTGAGE/", xmlout, insertLevels(xmlout, parentElement, "QUALIFIED_MORTGAGE"));
		insertRefinance(xmlin, path + "REFINANCE/", xmlout, insertLevels(xmlout, parentElement, "REFINANCE"));
		insertTermsOfLoan(xmlin, path + "TERMS_OF_LOAN/", xmlout, insertLevels(xmlout, parentElement, "TERMS_OF_LOAN"));
		insertUnderwriting(xmlin, path + "UNDERWRITING/", xmlout, insertLevels(xmlout, parentElement, "UNDERWRITING"));
	}
	
	private void insertLoanDetail(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "AssumabilityIndicator", xmlin, path);
		insertDataValue(xmlout, parentElement, "BalloonIndicator", xmlin, path);
		insertDataValue(xmlout, parentElement, "BalloonPaymentAmount", xmlin, path);
		insertDataValue(xmlout, parentElement, "BuydownTemporarySubsidyFundingIndicator", xmlin, path);
		insertDataValue(xmlout, parentElement, "ConstructionLoanIndicator", xmlin, path);
		insertDataValue(xmlout, parentElement, "DemandFeatureIndicator", xmlin, path);
		insertDataValue(xmlout, parentElement, "EscrowAbsenceReasonType", xmlin, path);
		insertDataValue(xmlout, parentElement, "EscrowIndicator", xmlin, path);
		insertDataValue(xmlout, parentElement, "InterestOnlyIndicator", xmlin, path);
		insertDataValue(xmlout, parentElement, "InterestRateIncreaseIndicator", xmlin, path);
		insertDataValue(xmlout, parentElement, "LoanAmountIncreaseIndicator", xmlin, path);
		insertDataValue(xmlout, parentElement, "MIRequiredIndicator", xmlin, path);
		insertDataValue(xmlout, parentElement, "NegativeAmortizationIndicator", xmlin, path);
		insertDataValue(xmlout, parentElement, "PaymentIncreaseIndicator", xmlin, path);
		insertDataValue(xmlout, parentElement, "PrepaymentPenaltyIndicator", xmlin, path);
		insertDataValue(xmlout, parentElement, "SeasonalPaymentFeatureIndicator", xmlin, path);
		insertDataValue(xmlout, parentElement, "TotalSubordinateFinancingAmount", xmlin, path);
	}
	
	private void insertLoanIdentifier(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "LoanIdentifier", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "LoanIdentifierType", xmlin, path, group);
	}
	
	private void insertLoanIdentifiers(Document xmlin, String path, Document xmlout, Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "LOAN_IDENTIFIER");
		for (String group : groupings)
			insertLoanIdentifier(xmlin, path + "LOAN_IDENTIFIER/", group, xmlout, insertLevels(xmlout, parentElement, "LOAN_IDENTIFIER"));
	}
	
	private void insertLoanPriceQuote(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertLoanPriceQuoteDetail(xmlin, path + "LOAN_PRICE_QUOTE_DETAIL/", group, xmlout, insertLevels(xmlout, parentElement, "LOAN_PRICE_QUOTE_DETAIL"));
	}
	
	private void insertLoanPriceQuoteDetail(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "LoanPriceQuoteInterestRatePercent", xmlin, path, group);
	}
	
	private void insertLoanPriceQuotes(Document xmlin, String path, Document xmlout, Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "LOAN_PRICE_QUOTE");
		for (String group : groupings)
			insertLoanPriceQuote(xmlin, path + "LOAN_PRICE_QUOTE/", group, xmlout, insertLevels(xmlout, parentElement, "LOAN_PRICE_QUOTE"));
	}
	
	private void insertLoanProduct(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertLoanPriceQuotes(xmlin, path + "LOAN_PRICE_QUOTES/", xmlout, insertLevels(xmlout, parentElement, "LOAN_PRICE_QUOTES"));
	}
	
	private void insertLoans(Document xmlin, String path, Document xmlout, Element parentElement) {
		// TODO iterate
		insertLoan(xmlin, path + "LOAN/", xmlout, insertLevels(xmlout, parentElement, "LOAN"));
	}
	
	private void insertMaturityRule(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "LoanMaturityPeriodCount", xmlin, path);
		insertDataValue(xmlout, parentElement, "LoanMaturityPeriodType", xmlin, path);
		insertDataValue(xmlout, parentElement, "LoanTermMaximumMonthsCount", xmlin, path);
	}
	
	private void insertMessage(Document xmlin, String path, Document xmlout, Element parentElement) {
		xmlout.getDocumentElement().setAttribute("xmlns:xsi", xsiURI);
		xmlout.getDocumentElement().setAttribute("xsi:schemaLocation", "http://www.mismo.org/residential/2009/schemas ../../../MISMO/V3.3.0_CR_2014-02/ReferenceModel_v3.3.0_B299/MISMO_3.3.0_B299.xsd");
		xmlout.getDocumentElement().setAttribute("xmlns:"+xlinkAlias, xlinkURI);
		xmlout.getDocumentElement().setAttribute("xmlns:"+mismoAlias, mismoURI);
		xmlout.getDocumentElement().setAttribute("xmlns:"+gseAlias, gseURI);
		insertAttributeValue(xmlout, parentElement, "MISMOReferenceModelIdentifier", xmlin, path);
		insertAboutVersions(xmlin, path + "ABOUT_VERSIONS/", xmlout, insertLevels(xmlout, xmlout.getDocumentElement(), "ABOUT_VERSIONS"));
		insertDocument(xmlin, path + "DOCUMENT_SETS/DOCUMENT_SET/DOCUMENTS/DOCUMENT/", xmlout, insertLevels(xmlout, xmlout.getDocumentElement(), "DOCUMENT_SETS/DOCUMENT_SET/DOCUMENTS/DOCUMENT"));
	}
	
	private void insertMIDataDetail(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "MICertificateIdentifier", xmlin, path);
		insertDataValue(xmlout, parentElement, "MICompanyNameType", xmlin, path);
		insertDataValue(xmlout, parentElement, "MICompanyNameTypeOtherDescription", xmlin, path);
		insertDataValue(xmlout, parentElement, "MIScheduledTerminationDate", xmlin, path);
	}
	
	private void insertName(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "FirstName", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "FullName", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "LastName", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "MiddleName", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "SuffixName", xmlin, path, group);
	}
	
	private void insertNegativeAmortization(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertNegativeAmortizationRule(xmlin, path + "NEGATIVE_AMORTIZATION_RULE/", xmlout, insertLevels(xmlout, parentElement, "NEGATIVE_AMORTIZATION_RULE"));
	}
	
	private void insertNegativeAmortizationRule(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "LoanNegativeAmortizationResolutionType", xmlin, path);
		insertDataValue(xmlout, parentElement, "LoanNegativeAmortizationResolutionTypeOtherDescription", xmlin, path);
		insertDataValue(xmlout, parentElement, "NegativeAmortizationLimitMonthsCount", xmlin, path);
		insertDataValue(xmlout, parentElement, "NegativeAmortizationMaximumLoanBalanceAmount", xmlin, path);
		insertDataValue(xmlout, parentElement, "NegativeAmortizationType", xmlin, path);
	}
	
	private void insertOther(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, gseAlias+":BuydownReflectedInNoteIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, gseAlias+":DocumentSignatureRequiredIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, gseAlias+":EscrowAccountRolloverAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, gseAlias+":IntegratedDisclosureSectionType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, gseAlias+":LiabilitySecuredBySubjectPropertyIndicator", xmlin, path, group);
		insertDataValue(xmlout, parentElement, gseAlias+":TotalOptionalPaymentCount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, gseAlias+":TotalStepCount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, gseAlias+":TotalStepPaymentCount", xmlin, path, group);
	}
	
	private void insertPartialPayment(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "PartialPaymentApplicationMethodType", xmlin, path, group);
	}
	
	private void insertPartialPayments(Document xmlin, String path, Document xmlout, Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "PARTIAL_PAYMENT");
		for (String group : groupings)
			insertPartialPayment(xmlin, path + "PARTIAL_PAYMENT/", group, xmlout, insertLevels(xmlout, parentElement, "PARTIAL_PAYMENT"));
	}
	
	private void insertParties(Document xmlin, String path, Document xmlout, Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "PARTY");
		for (String group : groupings)
			insertParty(xmlin, path + "PARTY/", group, xmlout, insertLevels(xmlout, parentElement, "PARTY"));
	}
	
	private void insertParty(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertAttributeValue(xmlout, parentElement, "SequenceNumber", xmlin, path, group);
		insertAttributeValue(xmlout, parentElement, xlinkAlias+":label", xmlin, path, group);
		insertIndividual(xmlin, path + "INDIVIDUAL/", group, xmlout, insertLevels(xmlout, parentElement, "INDIVIDUAL"));
		insertLegalEntity(xmlin, path + "LEGAL_ENTITY/", group, xmlout, insertLevels(xmlout, parentElement, "LEGAL_ENTITY"));
		insertAddresses(xmlin, path + "ADDRESSES/", group, xmlout, insertLevels(xmlout, parentElement, "ADDRESSES"));
		insertRoles(xmlin, path + "ROLES/", group, xmlout, insertLevels(xmlout, parentElement, "ROLES"));
	}
	
	private void insertPayment(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertPartialPayments(xmlin, path + "PARTIAL_PAYMENTS/", xmlout, insertLevels(xmlout, parentElement, "PARTIAL_PAYMENTS"));
		insertPaymentRule(xmlin, path + "PAYMENT_RULE/", xmlout, insertLevels(xmlout, parentElement, "PAYMENT_RULE"));
	}
	
	private void insertPaymentRule(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "FullyIndexedInitialPrincipalAndInterestPaymentAmount", xmlin, path);
		insertDataValue(xmlout, parentElement, "InitialPrincipalAndInterestPaymentAmount", xmlin, path);
		insertDataValue(xmlout, parentElement, "PartialPaymentAllowedIndicator", xmlin, path);
		insertDataValue(xmlout, parentElement, "PaymentFrequencyType", xmlin, path);
		insertDataValue(xmlout, parentElement, "PaymentOptionIndicator", xmlin, path);
		insertDataValue(xmlout, parentElement, "SeasonalPaymentPeriodEndMonth", xmlin, path);
		insertDataValue(xmlout, parentElement, "SeasonalPaymentPeriodStartMonth", xmlin, path);
		insertExtension(xmlin, path + "EXTENSION/", null, xmlout, insertLevels(xmlout, parentElement, "EXTENSION"));
	}
	
	private void insertPayoff(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "PayoffAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PayoffPrepaymentPenaltyAmount", xmlin, path, group);
	}
	
	private void insertPrepaidItem(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertPrepaidItemDetail(xmlin, path + "PREPAID_ITEM_DETAIL/", group, xmlout, insertLevels(xmlout, parentElement, "PREPAID_ITEM_DETAIL"));
		insertPrepaidItemPaidTo(xmlin, path + "PREPAID_ITEM_PAID_TO/", group, xmlout, insertLevels(xmlout, parentElement, "PREPAID_ITEM_PAID_TO"));
		insertPrepaidItemPayments(xmlin, path + "PREPAID_ITEM_PAYMENTS/", group, xmlout, insertLevels(xmlout, parentElement, "PREPAID_ITEM_PAYMENTS"));
	}
	
	private void insertPrepaidItemDetail(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertAttributeValue(xmlout, parentElement, "DisplayLabelText", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "FeePaidToType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "FeePaidToTypeOtherDescription", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureSectionType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PrepaidItemMonthsPaidCount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PrepaidItemPaidFromDate", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PrepaidItemPaidThroughDate", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PrepaidItemPerDiemAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PrepaidItemPerDiemCalculationMethodType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PrepaidItemType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PrepaidItemTypeOtherDescription", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "RegulationZPointsAndFeesIndicator", xmlin, path, group);
	}
	
	private void insertPrepaidItemPaidTo(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertLegalEntity(xmlin, path + "LEGAL_ENTITY/", group, xmlout, insertLevels(xmlout, parentElement, "LEGAL_ENTITY"));
	}
	
	private void insertPrepaidItemPayment(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "PrepaidItemActualPaymentAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PrepaidItemPaymentPaidByType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PrepaidItemPaymentTimingType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "RegulationZPointsAndFeesIndicator", xmlin, path, group);
	}
	
	private void insertPrepaidItemPayments(Document xmlin, String path, String groupPrefix, Document xmlout, Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "PREPAID_ITEM_PAYMENT", groupPrefix);
		for (String group : groupings)
			insertPrepaidItemPayment(xmlin, path + "PREPAID_ITEM_PAYMENT/", group, xmlout, insertLevels(xmlout, parentElement, "PREPAID_ITEM_PAYMENT"));
	}
	
	private void insertPrepaidItems(Document xmlin, String path, Document xmlout, Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "PREPAID_ITEM");
		for (String group : groupings)
			insertPrepaidItem(xmlin, path + "PREPAID_ITEM/", group, xmlout, insertLevels(xmlout, parentElement, "PREPAID_ITEM"));
	}
	
	private void insertPrepaymentPenalty(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertPrepaymentPenaltyLifetimeRule(xmlin, path + "PREPAYMENT_PENALTY_LIFETIME_RULE/", xmlout, insertLevels(xmlout, parentElement, "PREPAYMENT_PENALTY_LIFETIME_RULE"));
	}
	
	private void insertPrepaymentPenaltyLifetimeRule(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "PrepaymentPenaltyExpirationDate", xmlin, path);
		insertDataValue(xmlout, parentElement, "PrepaymentPenaltyExpirationMonthsCount", xmlin, path);
		insertDataValue(xmlout, parentElement, "PrepaymentPenaltyMaximumLifeOfLoanAmount", xmlin, path);
	}
	
	private void insertPrincipalAndInterestPaymentAdjustment(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertPrincipalAndInterestPaymentAdjustmentLimitedPaymentOptions(xmlin, path + "PRINCIPAL_AND_INTEREST_ADJUSTMENT_LIMITED_PAYMENT_OPTIONS/", xmlout, insertLevels(xmlout, parentElement, "PRINCIPAL_AND_INTEREST_ADJUSTMENT_LIMITED_PAYMENT_OPTIONS"));
		insertPrincipalAndInterestPaymentLifetimeAdjustmentRule(xmlin, path + "PRINCIPAL_AND_INTEREST_PAYMENT_LIFETIME_ADJUSTMENT_RULE/", xmlout, insertLevels(xmlout, parentElement, "PRINCIPAL_AND_INTEREST_PAYMENT_LIFETIME_ADJUSTMENT_RULE"));
		insertPrincipalAndInterestPaymentPerChangeAdjustmentRules(xmlin, path + "PRINCIPAL_AND_INTEREST_PAYMENT_PER_CHANGE_ADJUSTMENT_RULES/", xmlout, insertLevels(xmlout, parentElement, "PRINCIPAL_AND_INTEREST_PAYMENT_PER_CHANGE_ADJUSTMENT_RULES"));
	}
	
	private void insertPrincipalAndInterestPaymentAdjustmentLimitedPaymentOption(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "LimitedPrincipalAndInterestPaymentEffectiveDate", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "LimitedPrincipalAndInterestPaymentPeriodEndDate", xmlin, path, group);
	}
	
	private void insertPrincipalAndInterestPaymentAdjustmentLimitedPaymentOptions(Document xmlin, String path, Document xmlout, Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "PRINCIPAL_AND_INTEREST_ADJUSTMENT_LIMITED_PAYMENT_OPTION");
		for (String group : groupings)
			insertPrincipalAndInterestPaymentAdjustmentLimitedPaymentOption(xmlin, path + "PRINCIPAL_AND_INTEREST_ADJUSTMENT_LIMITED_PAYMENT_OPTION/", group, xmlout, insertLevels(xmlout, parentElement, "PRINCIPAL_AND_INTEREST_ADJUSTMENT_LIMITED_PAYMENT_OPTION"));
	}
	
	private void insertPrincipalAndInterestPaymentLifetimeAdjustmentRule(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "FirstPrincipalAndInterestPaymentChangeMonthsCount", xmlin, path);
		insertDataValue(xmlout, parentElement, "PrincipalAndInterestPaymentMaximumAmount", xmlin, path);
		insertDataValue(xmlout, parentElement, "PrincipalAndInterestPaymentMaximumAmountEarliestEffectiveMonthsCount", xmlin, path);
	}
	
	private void insertPrincipalAndInterestPaymentPerChangeAdjustmentRule(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "AdjustmentRuleType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PerChangeMaximumPrincipalAndInterestPaymentAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PerChangeMinimumPrincipalAndInterestPaymentAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PerChangePrincipalAndInterestPaymentAdjustmentFrequencyMonthsCount", xmlin, path, group);
	}
	
	private void insertPrincipalAndInterestPaymentPerChangeAdjustmentRules(Document xmlin, String path, Document xmlout, Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "PRINCIPAL_AND_INTEREST_PAYMENT_PER_CHANGE_ADJUSTMENT_RULE");
		for (String group : groupings)
			insertPrincipalAndInterestPaymentPerChangeAdjustmentRule(xmlin, path + "PRINCIPAL_AND_INTEREST_PAYMENT_PER_CHANGE_ADJUSTMENT_RULE/", group, xmlout, insertLevels(xmlout, parentElement, "PRINCIPAL_AND_INTEREST_PAYMENT_PER_CHANGE_ADJUSTMENT_RULE"));
	}
	
	private void insertProjectedPayment(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertAttributeValue(xmlout, parentElement, "SequenceNumber", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PaymentFrequencyType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ProjectedPaymentCalculationPeriodEndNumber", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ProjectedPaymentCalculationPeriodStartNumber", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ProjectedPaymentCalculationPeriodTermType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ProjectedPaymentEstimatedEscrowPaymentAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ProjectedPaymentEstimatedTotalMaximumPaymentAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ProjectedPaymentEstimatedTotalMinimumPaymentAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ProjectedPaymentMIPaymentAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ProjectedPaymentPrincipalAndInterestMaximumPaymentAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ProjectedPaymentPrincipalAndInterestMinimumPaymentAmount", xmlin, path, group);
	}
	
	private void insertProjectedPayments(Document xmlin, String path, Document xmlout, Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "PROJECTED_PAYMENT");
		for (String group : groupings)
			insertProjectedPayment(xmlin, path + "PROJECTED_PAYMENT/", group, xmlout, insertLevels(xmlout, parentElement, "PROJECTED_PAYMENT"));
	}
	
	private void insertPropertyDetail(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "PropertyEstimatedValueAmount", xmlin, path);
	}
	
	private void insertPropertyValuation(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertPropertyValuationDetail(xmlin, path + "PROPERTY_VALUATION_DETAIL/", group, xmlout, insertLevels(xmlout, parentElement, "PROPERTY_VALUATION_DETAIL"));
	}
	
	private void insertPropertyValuationDetail(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		Element element = insertDataValue(xmlout, parentElement, "AppraisalIdentifier", xmlin, path, group);
		insertAttributeValue(xmlout, element, "IdentifierOwnerURI", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PropertyValuationAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PropertyValuationMethodType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "PropertyValuationMethodTypeOtherDescription", xmlin, path, group);
	}
	
	private void insertPropertyValuations(Document xmlin, String path, Document xmlout, Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "PROPERTY_VALUATION");
		for (String group : groupings)
			insertPropertyValuation(xmlin, path + "PROPERTY_VALUATION/", group, xmlout, insertLevels(xmlout, parentElement, "PROPERTY_VALUATION"));
	}
	
	private void insertProrationItem(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertAttributeValue(xmlout, parentElement, "DisplayLabelText", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureSectionType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "IntegratedDisclosureSubsectionType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ProrationItemAmount", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ProrationItemPaidFromDate", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ProrationItemPaidThroughDate", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ProrationItemType", xmlin, path, group);
		insertDataValue(xmlout, parentElement, "ProrationItemTypeOtherDescription", xmlin, path, group);
	}
	
	private void insertProrationItems(Document xmlin, String path, Document xmlout, Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "PRORATION_ITEM");
		for (String group : groupings)
			insertProrationItem(xmlin, path + "PRORATION_ITEM/", group, xmlout, insertLevels(xmlout, parentElement, "PRORATION_ITEM"));
	}
	
	private void insertQualifiedMortgage(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertExemption(xmlin, path + "EXEMPTIONS/EXEMPTION/", xmlout, insertLevels(xmlout, parentElement, "EXEMPTIONS/EXEMPTION"));
		insertQualifiedMortgageDetail(xmlin, path + "QUALIFIED_MORTGAGE_DETAIL/", xmlout, insertLevels(xmlout, parentElement, "QUALIFIED_MORTGAGE_DETAIL"));
	}
	
	private void insertQualifiedMortgageDetail(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "AbilityToRepayMethodType", xmlin, path);
	}
	
	private void insertRealEstateAgent(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "RealEstateAgentType", xmlin, path, group);
	}
	
	private void insertRefinance(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "RefinanceSameLenderIndicator", xmlin, path);
	}
	
	private void insertRelationship(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertAttributeValue(xmlout, parentElement, "SequenceNumber", xmlin, path, group);
		insertAttributeValue(xmlout, parentElement, xlinkAlias+":from", xmlin, path, group);
		insertAttributeValue(xmlout, parentElement, xlinkAlias+":to", xmlin, path, group);
		insertAttributeValue(xmlout, parentElement, xlinkAlias+":arcrole", xmlin, path, group);
	}
	
	private void insertRelationships(Document xmlin, String path, Document xmlout, Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "RELATIONSHIP");
		for (String group : groupings)
			insertRelationship(xmlin, path + "RELATIONSHIP/", group, xmlout, insertLevels(xmlout, parentElement, "RELATIONSHIP"));
	}
	
	private void insertRole(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertAttributeValue(xmlout, parentElement, "SequenceNumber", xmlin, path, group);
		insertAttributeValue(xmlout, parentElement, xlinkAlias+":label", xmlin, path, group);
		insertRealEstateAgent(xmlin, path + "REAL_ESTATE_AGENT/", group, xmlout, insertLevels(xmlout, parentElement, "REAL_ESTATE_AGENT"));
		insertLicenses(xmlin, path + "LICENSES/", group, xmlout, insertLevels(xmlout, parentElement, "LICENSES"));
		insertRoleDetail(xmlin, path + "ROLE_DETAIL/", group, xmlout, insertLevels(xmlout, parentElement, "ROLE_DETAIL"));
	}
	
	private void insertRoleDetail(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "PartyRoleType", xmlin, path, group);
	}
	
	private void insertRoles(Document xmlin, String path, String groupPrefix, Document xmlout, Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "ROLE", groupPrefix);
		for (String group : groupings)
			insertRole(xmlin, path + "ROLE/", group, xmlout, insertLevels(xmlout, parentElement, "ROLE"));
	}
	
	private void insertSalesContractDetail(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "PersonalPropertyAmount", xmlin, path);
		insertDataValue(xmlout, parentElement, "PersonalPropertyIncludedIndicator", xmlin, path);
		insertDataValue(xmlout, parentElement, "RealPropertyAmount", xmlin, path);
		insertDataValue(xmlout, parentElement, "SalesContractAmount", xmlin, path);
	}
	
	private void insertSignatories(Document xmlin, String path, Document xmlout, Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "SIGNATORY");
		for (String group : groupings)
			insertSignatory(xmlin, path + "SIGNATORY/", group, xmlout, insertLevels(xmlout, parentElement, "SIGNATORY"));
	}
	
	private void insertSignatory(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertAttributeValue(xmlout, parentElement, "SequenceNumber", xmlin, path, group);
		insertAttributeValue(xmlout, parentElement, xlinkAlias+":label", xmlin, path, group);
        insertExecutionDetail(xmlin, path + "EXECUTION/EXECUTION_DETAIL/", xmlout, insertLevels(xmlout, parentElement, "EXECUTION/EXECUTION_DETAIL"));
	}
	
	private void insertSignature(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "XMLDigitalSignatureElement", xmlin, path, group);
	}
	
	private void insertSubjectProperty(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertAddress(xmlin, path + "ADDRESS/", null, xmlout, insertLevels(xmlout, parentElement, "ADDRESS"));
		insertUnparsedLegalDescription(xmlin, path + "LEGAL_DESCRIPTIONS/LEGAL_DESCRIPTION/UNPARSED_LEGAL_DESCRIPTIONS/UNPARSED_LEGAL_DESCRIPTION/", xmlout, insertLevels(xmlout, parentElement, "LEGAL_DESCRIPTIONS/LEGAL_DESCRIPTION/UNPARSED_LEGAL_DESCRIPTIONS/UNPARSED_LEGAL_DESCRIPTION"));
		insertPropertyDetail(xmlin, path + "PROPERTY_DETAIL/", xmlout, insertLevels(xmlout, parentElement, "PROPERTY_DETAIL"));
		insertPropertyValuations(xmlin, path + "PROPERTY_VALUATIONS/", xmlout, insertLevels(xmlout, parentElement, "PROPERTY_VALUATIONS"));
		insertSalesContractDetail(xmlin, path + "SALES_CONTRACTS/SALES_CONTRACT/SALES_CONTRACT_DETAIL/", xmlout, insertLevels(xmlout, parentElement, "SALES_CONTRACTS/SALES_CONTRACT/SALES_CONTRACT_DETAIL"));
	}
	
	private void insertSystemSignatures(Document xmlin, String path, Document xmlout, Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "SYSTEM_SIGNATURE");
		for (String group : groupings)
			insertSignature(xmlin, path + "SYSTEM_SIGNATURE/", group, xmlout, insertLevels(xmlout, parentElement, "SYSTEM_SIGNATURE"));
	}
	
	private void insertTermsOfLoan(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "AssumedLoanAmount", xmlin, path);
		insertDataValue(xmlout, parentElement, "DisclosedFullyIndexedRatePercent", xmlin, path);
		insertDataValue(xmlout, parentElement, "LienPriorityType", xmlin, path);
		insertDataValue(xmlout, parentElement, "LoanPurposeType", xmlin, path);
		insertDataValue(xmlout, parentElement, "MortgageType", xmlin, path);
		insertDataValue(xmlout, parentElement, "MortgageTypeOtherDescription", xmlin, path);
		insertDataValue(xmlout, parentElement, "NoteAmount", xmlin, path);
		insertDataValue(xmlout, parentElement, "NoteRatePercent", xmlin, path);
		insertDataValue(xmlout, parentElement, "WeightedAverageInterestRatePercent", xmlin, path);
	}
	
	private void insertUnderwriting(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertAutomatedUnderwritings(xmlin, path + "AUTOMATED_UNDERWRITINGS/", xmlout, insertLevels(xmlout, parentElement, "AUTOMATED_UNDERWRITINGS"));
		insertUnderwritingDetail(xmlin, path + "UNDERWRITING_DETAIL/", xmlout, insertLevels(xmlout, parentElement, "UNDERWRITING_DETAIL"));
	}
	
	private void insertUnderwritingDetail(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "LoanManualUnderwritingIndicator", xmlin, path);
	}
	
	private void insertUnparsedLegalDescription(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertDataValue(xmlout, parentElement, "UnparsedLegalDescription", xmlin, path);
	}
	
	private void insertView(Document xmlin, String path, String group, Document xmlout, Element parentElement) {
		insertAttributeValue(xmlout, parentElement, "SequenceNumber", xmlin, path, group);
		insertViewFile(xmlin, path + "VIEW_FILES/VIEW_FILE/", xmlout, insertLevels(xmlout, parentElement, "VIEW_FILES/VIEW_FILE"));
	}
	
	private void insertViewFile(Document xmlin, String path, Document xmlout, Element parentElement) {
		insertForeignObject(xmlin, path + "FOREIGN_OBJECT/", xmlout, insertLevels(xmlout, parentElement, "FOREIGN_OBJECT"));
	}
	
	private void insertViews(Document xmlin, String path, Document xmlout, Element parentElement) {
		String[] groupings = findGroupings(xmlin, path, "VIEW");
		for (String group : groupings)
			insertView(xmlin, path + "VIEW/", group, xmlout, insertLevels(xmlout, parentElement, "VIEW"));
	}

	private static void usage() {
		System.err.println("Usage: ConstructUCDXml <csv filename>|<xml filename> [<csv filename> ...]");
	}

}
