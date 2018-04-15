package com.tqbdev.funcs;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class LoadDicFromXML {
	static public HashMap<String, String> loadFromFile(String fileName)
			throws ParserConfigurationException, SAXException, IOException {
		HashMap<String, String> res = new HashMap<String, String>();

		File inputFile = new File(fileName);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(inputFile);
		doc.getDocumentElement().normalize();

		NodeList nList = doc.getElementsByTagName("record");

		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;

				String word = eElement.getElementsByTagName("word").item(0).getTextContent();
				String meaning = eElement.getElementsByTagName("meaning").item(0).getTextContent();
				res.put(word, meaning);
			}
		}

		return res;
	}
}
