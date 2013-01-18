package com.tharun.routes;
 
 
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

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
 
public class WriteXMLFile {
 
	public  void writeToXmlFile(String path,String fileName, List<String> landmarks) {
 
	  try {
 
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
 
		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("NamedEntities");
		doc.appendChild(rootElement);
 
		// staff elements
		Element staff = doc.createElement("Entity");
		rootElement.appendChild(staff);
 
		// set attribute to staff element
		Attr attr = doc.createAttribute("type");
		attr.setValue("landmark");
		staff.setAttributeNode(attr);
 
		 
		
		for(String s: landmarks)
		{
			Element name = doc.createElement("Name");
			name.appendChild(doc.createTextNode(s));
			staff.appendChild(name);
		}
		
 
	 
 
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(path+"/"+fileName));
 
		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);
 
		transformer.transform(source, result);
  
	  } catch (ParserConfigurationException pce) {
		pce.printStackTrace();
	  } catch (TransformerException tfe) {
		tfe.printStackTrace();
	  }
	}
	
	
	 public void writeToFile(String path,
				String fileName, String noHTMLString) {
	    	try{
	  		  // Create file 
	  		  FileWriter fstream = new FileWriter(path+fileName);
	  		  BufferedWriter out = new BufferedWriter(fstream);
	  		  out.write(noHTMLString);
	  		  //Close the output stream
	  		  out.close();
	  		  }catch (Exception e){//Catch exception if any
	  		  System.err.println("Error: " + e.getMessage());
	  		  }
			
		}

 
}