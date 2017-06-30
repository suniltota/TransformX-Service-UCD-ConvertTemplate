package com.actualize.mortgage.ucdutils;

import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class UCDArcRolesSignatory {
	private static final XPath XPATH = XPathFactory.newInstance().newXPath();
	private static final String MISMO = "mismo";
	private static final String XLINK = "xlink";

	static {
		NamespaceContext ctx = new NamespaceContext() {
			public String getNamespaceURI(String prefix) {
		        return MISMO.equals(prefix) ? "http://www.mismo.org/residential/2009/schemas" : (XLINK.equals(prefix) ? "http://www.w3.org/1999/xlink" : null); 
		    }
		    public String getPrefix(String uri) {
		    	throw new UnsupportedOperationException();
		    }
			@SuppressWarnings("rawtypes")
			public Iterator getPrefixes(String val) {
				throw new UnsupportedOperationException();
		    }
		};
		XPATH.setNamespaceContext(ctx);
	}

	public void normalize(Document document) {
		try {
			Element relationshipsNode = getRelationshipsElement(document);
			Element documentElement = document.getDocumentElement();
			NodeList partyNodeList = getNodeList(documentElement, addNamespace("DOCUMENT_SETS/DOCUMENT_SET/DOCUMENTS/DOCUMENT/DEAL_SETS/DEAL_SET/DEALS/DEAL/PARTIES/PARTY", MISMO));
			NodeList signatoryNodeList = getNodeList(documentElement, addNamespace("DOCUMENT_SETS/DOCUMENT_SET/DOCUMENTS/DOCUMENT/SIGNATORIES/SIGNATORY", MISMO));
			int signatoryIndex = 0;
			for (int i = 0; i < partyNodeList.getLength() && signatoryIndex < signatoryNodeList.getLength(); i++) {
				Element partyElement = (Element)partyNodeList.item(i);
				Element signatoryElement = (Element)signatoryNodeList.item(signatoryIndex);
				String partyRoleType = getTextContent(partyElement, addNamespace("ROLES/ROLE/ROLE_DETAIL/PartyRoleType", MISMO));
				if ("Borrower".equals(partyRoleType)) {
					int signatorySequenceNumber = signatoryIndex + 1;
					signatoryElement.setAttribute("SequenceNumber", "" + signatorySequenceNumber);
					signatoryElement.setAttribute(XLINK+":label", getSignatoryLinkName(signatorySequenceNumber));
					Element relationshipElement = document.createElement(MISMO + ":RELATIONSHIP");
					int relationshipSequenceNumber = signatoryIndex + 6;
					relationshipsNode.appendChild(relationshipElement);
					relationshipElement.setAttribute("SequenceNumber", "" + relationshipSequenceNumber);
					relationshipElement.setAttribute(XLINK+":arcrole", "urn:fdc:mismo.org:2009:residential/ROLE_IsAssociatedWith_SIGNATORY");
					relationshipElement.setAttribute(XLINK+":from", getPartyLinkName(signatorySequenceNumber));
					relationshipElement.setAttribute(XLINK+":to", getSignatoryLinkName(signatorySequenceNumber));
					signatoryIndex++;
				}
			}
		}
		catch (XPathExpressionException e) {
			// TODO log bad xpath expression, e.g. can't determine either to party role type or if the party is an organization
		}
	}
	
	private String getPartyLinkName(int sequenceNumber) {
		return "PARTY" + getPartyNumber(sequenceNumber) + "_ROLE1";
	}
	
	private int getPartyNumber(int sequenceNumber) {
		if (sequenceNumber < 3)
			return 10 + sequenceNumber; // 11, 12
		return 12 + sequenceNumber; // 15, 16, ...
	}
	
	private String getSignatoryLinkName(int sequenceNumber) {
		return "SIGNATORY_" + sequenceNumber;
	}
	
	Element getRelationshipsElement(Document doc) throws XPathExpressionException {
		Element documentElement = doc.getDocumentElement();
		NodeList parentNodeList = getNodeList(documentElement, addNamespace("DOCUMENT_SETS/DOCUMENT_SET/DOCUMENTS/DOCUMENT", MISMO));
		if (parentNodeList.getLength() == 0)
			return null;
		Element parentNode = (Element)parentNodeList.item(0);
		NodeList relationshipsNodeList = getNodeList(parentNode, addNamespace("RELATIONSHIPS", MISMO));
		Element relationshipsNode;
		if (relationshipsNodeList.getLength() > 0)
			relationshipsNode = (Element)relationshipsNodeList.item(0);
		else {
			relationshipsNode = doc.createElement(MISMO + ":RELATIONSHIPS");
			parentNode.appendChild(relationshipsNode);
		}
		return relationshipsNode;
	}
	
	private String addNamespace(String path, String namespace) {
		String[] nodes = path.split("/");
		for (int i = 0; i < nodes.length; i++)
			nodes[i] = "*[local-name()='" + nodes[i] + "']";
//			nodes[i] = (nodes[i].indexOf(':') == -1 && !"".equals(namespace) && !"".equals(nodes[i]) ? namespace + ":" : "") + nodes[i];		
		return String.join("/", nodes);
	}
	
	private NodeList getNodeList(Element element, String expression) throws XPathExpressionException {
		return (NodeList)XPATH.evaluate(expression, element, XPathConstants.NODESET);
	}
	
	private String getTextContent(Element element, String expression) throws XPathExpressionException {
		NodeList nodeList = getNodeList(element, expression);
		return nodeList.getLength() > 0 ? ((Element)nodeList.item(0)).getTextContent() : "";
	}
}
