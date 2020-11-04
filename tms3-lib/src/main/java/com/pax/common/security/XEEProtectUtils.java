package com.pax.common.security;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;

import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class XEEProtectUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(XEEProtectUtils.class);

	private XEEProtectUtils() {
		// Utility
	}

	public static DocumentBuilderFactory createDocumentBuilderFactory() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setExpandEntityReferences(false);
		return dbf;
	}

	public static XMLInputFactory newXMLInputFactory() {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, false);
		factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
		factory.setProperty(XMLInputFactory.SUPPORT_DTD, false); // 会完全禁止DTD
		return factory;
	}

	public static XMLReader createXMLReader() throws SAXException {
		XMLReader xmlReader = XMLReaderFactory.createXMLReader();
		setFeature(xmlReader, "http://apache.org/xml/features/disallow-doctype-decl", true);
		setFeature(xmlReader, "http://xml.org/sax/features/external-general-entities", false);
		setFeature(xmlReader, "http://xml.org/sax/features/external-parameter-entities", false);
		return xmlReader;
	}

	private static void setFeature(XMLReader xmlReader, String feature, boolean value) {
		try {
			xmlReader.setFeature(feature, value);
		} catch (SAXException e) {
			LOGGER.debug("Exception occurred, XXE may still possible", e);
		}
	}

	public static SAXReader createSAXReader() {
		SAXReader reader = new SAXReader();
		setFeature(reader, "http://apache.org/xml/features/disallow-doctype-decl", true);
		setFeature(reader, "http://xml.org/sax/features/external-general-entities", false);
		setFeature(reader, "http://xml.org/sax/features/external-parameter-entities", false);
		return reader;
	}

	private static void setFeature(SAXReader reader, String feature, boolean value) {
		try {
			reader.setFeature(feature, value);
		} catch (SAXException e) {
			LOGGER.debug("Exception occurred, XXE may still possible", e);
		}
	}
}
