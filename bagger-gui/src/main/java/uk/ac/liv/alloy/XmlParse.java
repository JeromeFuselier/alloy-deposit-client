package uk.ac.liv.alloy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlParse {

	public static ArrayList<String> parseSD(String xml) {
		Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        
        ArrayList<String> res = new ArrayList<String>();
        
        res.add("default");
        
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
	        dom = db.parse(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));
            
            NodeList coll_nodes = dom.getElementsByTagName("collection");
            
            for (int idx = 0 ; idx < coll_nodes.getLength() ; idx++) {
            	Node coll_node = coll_nodes.item(idx);
            	
            	NodeList childs = coll_node.getChildNodes();
            	
            	for (int idx2 = 0 ; idx2 < childs.getLength() ; idx2++) {
            		Node child = childs.item(idx2);
            		
            		if (child.getNodeName() == "atom:title") {
            			String document_id = child.getTextContent().replace("Collection ", "");
            			if (!document_id.equals("default"))
            				res.add(document_id);
            		}
            		
            	}
            }
        
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}

}
