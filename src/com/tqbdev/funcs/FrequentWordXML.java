package com.tqbdev.funcs;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class FrequentWordXML {
	private static boolean checkFileXMLExist(String filePathString) {
		File f = new File(filePathString);
		return f.exists() && !f.isDirectory();
	}

	public static void addWord(String word, String fileName)
			throws ParserConfigurationException, TransformerException, SAXException, IOException, ParseException {
		if (word == null || word == "" || word.length() == 0) {
			return;
		}
		
		Date now = new Date();

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc;

		if (!checkFileXMLExist(fileName)) { // Create new XML file
			doc = docBuilder.newDocument();

			// root element
			Element rootElement = doc.createElement("history");
			doc.appendChild(rootElement);

			// Date element
			Element date = doc.createElement("date");
			rootElement.appendChild(date);

			// setting attribute to date element
			Attr attr = doc.createAttribute("value");
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			String formattedDate = formatter.format(now);
			attr.setValue(formattedDate);
			date.setAttributeNode(attr);

			// word element
			Element wordElement = doc.createElement("word");
			wordElement.appendChild(doc.createTextNode(word));
			date.appendChild(wordElement);
		} else { // Modify XML file
			File inputFile = new File(fileName);
			doc = docBuilder.parse(inputFile);

			NodeList dates = doc.getElementsByTagName("date");

			boolean checkExist = false;

			for (int i = 0; i < dates.getLength(); i++) {
				Node nNode = dates.item(i);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;

					SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
					Date dateValue = parser.parse(eElement.getAttribute("value"));

					LocalDate first = dateValue.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					LocalDate second = now.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

					if (first.compareTo(second) == 0) {
						checkExist = true;

						// word element
						Element wordElement = doc.createElement("word");
						wordElement.appendChild(doc.createTextNode(word));

						nNode.appendChild(wordElement);
					}
				}
			}

			if (checkExist == false) {
				Node root = doc.getElementsByTagName("history").item(0);

				// Date element
				Element date = doc.createElement("date");
				root.appendChild(date);

				// setting attribute to date element
				Attr attr = doc.createAttribute("value");
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				String formattedDate = formatter.format(now);
				attr.setValue(formattedDate);
				date.setAttributeNode(attr);

				// word element
				Element wordElement = doc.createElement("word");
				wordElement.appendChild(doc.createTextNode(word));
				date.appendChild(wordElement);
			}
		}

		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(fileName));
		transformer.transform(source, result);
	}

	public static HashMap<String, Integer> getListFrequent(Date from, Date to, String fileName)
			throws ParseException, ParserConfigurationException, SAXException, IOException {
		HashMap<String, Integer> listFrequent = new HashMap<>();

		File inputFile = new File(fileName);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(inputFile);
		doc.getDocumentElement().normalize();

		NodeList nList = doc.getElementsByTagName("date");

		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;

				SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
				Date dateValue = parser.parse(eElement.getAttribute("value"));

				LocalDate first = dateValue.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				LocalDate second = from.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				LocalDate thid = to.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

				if (first.compareTo(second) >= 0 && first.compareTo(thid) <= 0) {
					NodeList insideList = eElement.getElementsByTagName("word");

					for (int i = 0; i < insideList.getLength(); i++) {
						Node insideNode = insideList.item(i);

						if (insideNode.getNodeType() == Node.ELEMENT_NODE) {
							Element iElement = (Element) insideNode;

							String word = iElement.getTextContent();
							if (listFrequent.containsKey(word)) {
								listFrequent.put(word, listFrequent.get(word) + 1);
							} else {
								listFrequent.put(word, 1);
							}
						}
					}
				}
			}
		}

		return listFrequent;
	}
}
