package com.actualize.mortgage.xmlutils;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Class <code>XMLXslt </code> execute as main class which is responsible for creating xml xslt format
 * 
 */
public class XMLXslt {
	static String xmlIn = "<?xml version=\"1.0\"?>\n" +
            "<?xml-stylesheet type=\"text/xml\" href=\"17-2.xsl\"?>\n" +
            "<PERIODIC_TABLE>\n" +
            "  <ATOM STATE=\"GAS\">\n" +
            "    <NAME>Hydrogen</NAME>\n" +
            "    <SYMBOL>H</SYMBOL>\n" +
            "    <ATOMIC_NUMBER>1</ATOMIC_NUMBER>\n" +
            "    <ATOMIC_WEIGHT>1.00794</ATOMIC_WEIGHT>\n" +
            "    <BOILING_POINT UNITS=\"Kelvin\">20.28</BOILING_POINT>\n" +
            "    <MELTING_POINT UNITS=\"Kelvin\">13.81</MELTING_POINT>\n" +
            "    <DENSITY UNITS=\"grams/cubic centimeter\">\n" +
            "      <!-- At 300K, 1 atm -->\n" +
            "      0.0000899\n" +
            "    </DENSITY>\n" +
            "  </ATOM>\n" +
            "  <ATOM STATE=\"GAS\">\n" +
            "    <NAME>Helium</NAME>\n" +
            "    <SYMBOL>He</SYMBOL>\n" +
            "    <ATOMIC_NUMBER>2</ATOMIC_NUMBER>\n" +
            "    <ATOMIC_WEIGHT>4.0026</ATOMIC_WEIGHT>\n" +
            "    <BOILING_POINT UNITS=\"Kelvin\">4.216</BOILING_POINT>\n" +
            "    <MELTING_POINT UNITS=\"Kelvin\">0.95</MELTING_POINT>\n" +
            "    <DENSITY UNITS=\"grams/cubic centimeter\"><!-- At 300K -->\n" +
            "      0.0001785\n" +
            "    </DENSITY>\n" +
            "  </ATOM>\n" +
            "</PERIODIC_TABLE>";
    static String xsl = "<?xml version=\"1.0\"?>\n" +
            "<xsl:stylesheet version=\"1.0\" \n" +
            "          xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\n" +
            "  <xsl:template match=\"PERIODIC_TABLE\">\n" +
            "    <html>\n" +
            "      <xsl:apply-templates/>\n" +
            "    </html>\n" +
            "  </xsl:template>\n" +
            "  <xsl:template match=\"ATOM\">\n" +
            "    <P>\n" +
            "      <xsl:apply-templates/>\n" +
            "    </P>\n" +
            "  </xsl:template>\n" +
            "</xsl:stylesheet>";
    
}
