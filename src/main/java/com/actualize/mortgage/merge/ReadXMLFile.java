package com.actualize.mortgage.merge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Class <code>ReadXMLFile </code> execute as main class which reads the xml file. 
 * 
 *
 */
public class ReadXMLFile {

	List<Integer> index = new ArrayList<Integer>();
	List<List<String>> expected_list_str = new ArrayList<List<String>>();
	static private String rootNode = "mismo:INTEGRATED_DISCLOSURE_SECTION_SUMMARY_DETAIL";
	static private String expectedvalue = "OriginationCharges";
	static  String fileName_loac=null;

/**
 * 
 * @param parentNode parent node from xml file
 * @param childNode child node of parent from xml file.
 * @param filename file location
 * @return list of node values
 */
	public List<List<String>> getXMlNodeAndValue(String parentNode,
			String childNode, String filename) {

		final List<String> list = new ArrayList<String>();
		final List<Map<String, List<String>>> map_list = new ArrayList<Map<String, List<String>>>();
		try {

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			DefaultHandler handler = new DefaultHandler() {

				boolean trackRootElement = false;

				public void startElement(String uri, String localName,
						String qName, Attributes attributes)
						throws SAXException {

					if (qName.equalsIgnoreCase(rootNode)) {

						list.add(qName);
						trackRootElement = true;

					} else if (trackRootElement) {

						list.add(qName);
					}

				}

				public void endElement(String uri, String localName,
						String qName) throws SAXException {
					if (qName.equalsIgnoreCase(rootNode)) {

						list.add(qName);
						trackRootElement = false;
					} else if (trackRootElement) {

						list.add(qName);
					}

				}

				public void characters(char ch[], int start, int length)
						throws SAXException {
					if (trackRootElement
							&& new String(ch, start, length).trim().length() > 0) {

						list.add(new String(ch, start, length));
					}
				}
			};

			saxParser.parse(filename, handler);
			for (int i = 0; i < list.size(); i++) {
				String s = list.get(i);
				if (s.equalsIgnoreCase(rootNode)) {
					index.add(i);

				}
			}
			// 1,14,15,18
			Object obj[] = new Object[index.size()];
			for (int k = 0; k < index.size(); k++) {
				List<String> sub_list = list.subList((index.get(k) + 1),
						index.get(++k));
				Map<String, List<String>> map = new HashMap<String, List<String>>();
				map.put(rootNode, sub_list);
				map_list.add(map);
				for (int cnt = 0; cnt < sub_list.size(); cnt++) {
					String result = sub_list.get(cnt);
					if (result.equalsIgnoreCase(childNode)) {
						expected_list_str.add(sub_list);
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return expected_list_str;
	}
}
