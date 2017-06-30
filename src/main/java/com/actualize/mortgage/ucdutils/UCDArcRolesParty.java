package com.actualize.mortgage.ucdutils;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class UCDArcRolesParty {
	private static class PartyEntry {
		public final String partyRoleType;
		public final boolean isOrganization;
		public final int sequenceNumber;
		public final String linkName;
		
		public PartyEntry(String prt, boolean isorg, int snum, String ln) {
			partyRoleType = prt;
			isOrganization = isorg;
			sequenceNumber = snum;
			linkName = ln;
		}
	}

	private static final PartyEntry[] PARTY;
	
	static ArrayList<PartyEntry> partyEntryInit() {
		ArrayList<PartyEntry> p = new ArrayList<>();
		p.add(new PartyEntry("NotePayTo",       true,   1, "PARTY1_ROLE1"));
		p.add(new PartyEntry("NotePayTo",       false,  2, "PARTY2_ROLE1"));
		p.add(new PartyEntry("MortgageBroker",  true,   3, "PARTY3_ROLE1"));
		p.add(new PartyEntry("MortgageBroker",  false,  4, "PARTY4_ROLE1"));
		p.add(new PartyEntry("RealEstateAgent", true,   5, "PARTY5_ROLE1"));
		p.add(new PartyEntry("RealEstateAgent", false,  6, "PARTY6_ROLE1"));
		p.add(new PartyEntry("RealEstateAgent", true,   7, "PARTY7_ROLE1"));
		p.add(new PartyEntry("RealEstateAgent", false,  8, "PARTY8_ROLE1"));
		p.add(new PartyEntry("ClosingAgent",    true,   9, "PARTY9_ROLE1"));
		p.add(new PartyEntry("ClosingAgent",    false, 10, "PARTY10_ROLE1"));
		p.add(new PartyEntry("Borrower",        false, 11, "PARTY11_ROLE1"));
		p.add(new PartyEntry("Borrower",        false, 12, "PARTY12_ROLE1"));
		p.add(new PartyEntry("Seller",          false, 13, "PARTY13_ROLE1"));
		p.add(new PartyEntry("Seller",          false, 14, "PARTY14_ROLE1"));
		return p;
	}

	private static String partyGetLinkName(int sequenceNumber) {
		for (int i = 0; i < PARTY.length; i++)
			if (PARTY[i].sequenceNumber == sequenceNumber)
				return PARTY[i].linkName;
		return "";
	}

	private static int partyMaxSequenceNumber() {
		int max = 0;
		for (int i = 0; i < PARTY.length; i++)
			if (PARTY[i].sequenceNumber > max)
				max = PARTY[i].sequenceNumber;
		return max;
	}

	private static class RelationshipEntry {
		public final String arcRoleName;
		public final int sequenceNumber;
		public final int fromSequenceNumber;
		public final int toSequenceNumber;
		
		public RelationshipEntry(String arcRoleName, int sequenceNumber, int fromSequenceNumber, int toSequenceNumber) {
			this.arcRoleName = arcRoleName;
			this.sequenceNumber = sequenceNumber;
			this.fromSequenceNumber = fromSequenceNumber;
			this.toSequenceNumber = toSequenceNumber;
		}
	}

	private static final RelationshipEntry[] RELATIONSHIP;
	
	static ArrayList<RelationshipEntry> relationshipEntryInit() {
		ArrayList<RelationshipEntry> p = new ArrayList<>();
		p.add(new RelationshipEntry("urn:fdc:mismo.org:2009:residential/ROLE_IsEmployedBy_ROLE", 1,  2, 1));
		p.add(new RelationshipEntry("urn:fdc:mismo.org:2009:residential/ROLE_IsEmployedBy_ROLE", 2,  4, 3));
		p.add(new RelationshipEntry("urn:fdc:mismo.org:2009:residential/ROLE_IsEmployedBy_ROLE", 3,  6, 5));
		p.add(new RelationshipEntry("urn:fdc:mismo.org:2009:residential/ROLE_IsEmployedBy_ROLE", 4,  8, 7));
		p.add(new RelationshipEntry("urn:fdc:mismo.org:2009:residential/ROLE_IsEmployedBy_ROLE", 5, 10, 9));
		return p;
	}

	private static final XPath XPATH = XPathFactory.newInstance().newXPath();
	private static final String MISMO = "mismo";
	private static final String XLINK = "xlink";	

	static {
		ArrayList<PartyEntry> p = partyEntryInit();
		PARTY = p.toArray(new PartyEntry[p.size()]);
		ArrayList<RelationshipEntry> r = relationshipEntryInit();
		RELATIONSHIP = r.toArray(new RelationshipEntry[r.size()]);
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
		Element documentElement = document.getDocumentElement();
		try {
			TreeMap<Integer, Element> orderedParties = orderParties(documentElement);
			insertRoleAnnotations(orderedParties);
			insertRelationships(document, orderedParties);
		} catch (XPathExpressionException e) {
			// TODO log bad xpath expression
		}
	}

	private TreeMap<Integer, Element> orderParties(Element documentElement) throws XPathExpressionException {
		NodeList partyNodeList = getNodeList(documentElement, addNamespace("DOCUMENT_SETS/DOCUMENT_SET/DOCUMENTS/DOCUMENT/DEAL_SETS/DEAL_SET/DEALS/DEAL/PARTIES/PARTY", MISMO));
		int overflowIndex = partyMaxSequenceNumber() + 1;
		TreeMap<Integer, Element> orderedParties = new TreeMap<>();
		for (int i = 0; i < partyNodeList.getLength(); i++) {
			Element partyElement = (Element)partyNodeList.item(i);
			int index = findSequenceNumber(partyElement, orderedParties);
			if (index == 0)  // not in PartyEntry table, append to the end
				orderedParties.put(overflowIndex++, partyElement);
			else
				orderedParties.put(index, partyElement);
		}
		return orderedParties;
	}
	
	private int findSequenceNumber(Element partyElement, TreeMap<Integer, Element> orderedParties) {
		try {
			String partyRoleType = getTextContent(partyElement, addNamespace("ROLES/ROLE/ROLE_DETAIL/PartyRoleType", MISMO));
			boolean isOrganization = getNodeList(partyElement, addNamespace("INDIVIDUAL", MISMO)).getLength() == 0;
			for (int i = 0; i < PARTY.length; i++) {
				boolean matching = PARTY[i].partyRoleType.equals(partyRoleType) && PARTY[i].isOrganization == isOrganization;
				boolean emptySlot = orderedParties.get(PARTY[i].sequenceNumber) == null;
				if (matching && emptySlot)
					return PARTY[i].sequenceNumber;
			}
		}
		catch (XPathExpressionException e) {
			// TODO log bad xpath expression, e.g. can't determine either to party role type or if the party is an organization
		}
		return 0;
	}
	
	private void insertRoleAnnotations(TreeMap<Integer, Element> orderedParties) {
		for (Integer key : orderedParties.keySet()) {
			Element partyElement = orderedParties.get(key);
			try {
				NodeList role = (NodeList)getNodeList(partyElement, addNamespace("ROLES/ROLE", MISMO));
				if (role.getLength() > 0) {
					Element roleElement = (Element)role.item(0);
					roleElement.setAttribute("SequenceNumber", key.toString());
					if (key < PARTY.length)
						roleElement.setAttribute(XLINK + ":label", partyGetLinkName(key));
					else
						roleElement.removeAttribute(XLINK + ":label");
				}
			} catch (XPathExpressionException e) {
				orderedParties.remove(key);  // can tag with a label, so don't include in the linking
				// TODO log bad xpath expression
			}
			partyElement.getParentNode().appendChild(partyElement); // move the node to the proper order in the XML
		}
	}

	private void insertRelationships(Document doc, TreeMap<Integer, Element> orderedParties) {
		Element relationshipsElement = null;
		try {
			relationshipsElement = getRelationshipsElement(doc);
		} catch (XPathExpressionException e) {
			// TODO log bad xpath expression
		}
		if (relationshipsElement == null)
			return;
		for (int i = 0; i < RELATIONSHIP.length; i++) {
			if (orderedParties.get(RELATIONSHIP[i].fromSequenceNumber) != null && orderedParties.get(RELATIONSHIP[i].toSequenceNumber) != null) {
				Element element = doc.createElement(MISMO + ":RELATIONSHIP");
				relationshipsElement.appendChild(element);
				element.setAttribute("SequenceNumber", "" + RELATIONSHIP[i].sequenceNumber);
				element.setAttribute(XLINK+":arcrole", RELATIONSHIP[i].arcRoleName);
				element.setAttribute(XLINK+":from", partyGetLinkName(RELATIONSHIP[i].fromSequenceNumber));
				element.setAttribute(XLINK+":to", partyGetLinkName(RELATIONSHIP[i].toSequenceNumber));
			}
		}
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
