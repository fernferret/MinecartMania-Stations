package com.afforess.minecartmaniastation;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.afforess.minecartmaniacore.MinecartManiaWorld;
import com.afforess.minecartmaniacore.config.MinecartManiaConfigurationParser;
import com.afforess.minecartmaniacore.config.SettingParser;

public class StationSettingParser implements SettingParser{
	private static final double version = 1.0;
	
	public boolean isUpToDate(Document document) {
		try {
			NodeList list = document.getElementsByTagName("version");
			Double version = MinecartManiaConfigurationParser.toDouble(list.item(0).getChildNodes().item(0).getNodeValue(), 0);
			return version == StationSettingParser.version;
		}
		catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean read(Document document) {
		Object value;
		NodeList list;
		String setting;
		
		try {
			setting = "IntersectionPrompts";
			list = document.getElementsByTagName(setting);
			value = MinecartManiaConfigurationParser.toInt(list.item(0).getChildNodes().item(0).getNodeValue(), 0);
			MinecartManiaWorld.getConfiguration().put(setting, value);
			
			setting = "StationSignParsingMethod";
			list = document.getElementsByTagName(setting);
			value = MinecartManiaConfigurationParser.toInt(list.item(0).getChildNodes().item(0).getNodeValue(), 0);
			MinecartManiaWorld.getConfiguration().put(setting, value);
			
			setting = "StationCommandSavesAfterUse";
			list = document.getElementsByTagName(setting);
			value = MinecartManiaConfigurationParser.toBool(list.item(0).getChildNodes().item(0).getNodeValue());
			MinecartManiaWorld.getConfiguration().put(setting, value);
		}
		catch (Exception e) {
			return false;
		}
		
		return true;
	}

	@Override
	public boolean write(File configuration) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			//root elements
			Document doc = docBuilder.newDocument();
			doc.setXmlStandalone(true);
			Element rootElement = doc.createElement("MinecartManiaConfiguration");
			doc.appendChild(rootElement);
			
			Element setting = doc.createElement("version");
			setting.appendChild(doc.createTextNode("1.0"));
			rootElement.appendChild(setting);
			
			setting = doc.createElement("IntersectionPrompts");
			Comment comment = doc.createComment("If set to 0, Will prompt users for their intended direction when a player reaches an intersection. If set to 1, will prompt users only if the intersection has a station block underneath. If set to 2 players will never be prompted at intersections.");
			setting.appendChild(doc.createTextNode("0"));
			rootElement.appendChild(setting);
			rootElement.insertBefore(comment,setting);
			
			setting = doc.createElement("StationSignParsingMethod");
			comment = doc.createComment("0 - simple parsing with no pattern matching. 1 - simple pattern matching. 2 - full regex parsing.");
			setting.appendChild(doc.createTextNode("0"));
			rootElement.appendChild(setting);
			rootElement.insertBefore(comment,setting);
			
			setting = doc.createElement("StationCommandSavesAfterUse");
			comment = doc.createComment("After passing one intersection, the /st command will not be cleared.");
			setting.appendChild(doc.createTextNode("false"));
			rootElement.appendChild(setting);
			rootElement.insertBefore(comment,setting);
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(configuration);
			transformer.transform(source, result);
		}
		catch (Exception e) { return false; }
		return true;
	}
}
