import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javafx.scene.Node;

public class Account {
	private static String name;
	private static Comparator<StoredGame> comparator;
    private static PriorityQueue<StoredGame> favorites;
    private static NodeList favNodeList;
    
    private static PriorityQueue<StoredGame> beaten;
    private static NodeList beatenNodeList;
    
    private static PriorityQueue<StoredGame> backLog;
    private static NodeList backLogNodeList;
    
    private static PriorityQueue<StoredGame> top4;
    private static NodeList top4NodeList;
    
    
    
    public Account() {
    	this("Your Account");
    }
    
    public Account(String name) {
    	comparator = new GameListComparator();
        favorites = new PriorityQueue<StoredGame>(100, comparator);
        beaten = new PriorityQueue<StoredGame>(100, comparator);
        backLog = new PriorityQueue<StoredGame>(100, comparator);
        top4 = new PriorityQueue<StoredGame>(100, comparator);
        this.name = name;
        //loadFavorites();
      	loadLists();
    }
    
    public PriorityQueue<StoredGame> getFavorites() {
    	return favorites;
    }
    
    public PriorityQueue<StoredGame> getBeaten() {
    	return beaten;
    }
    
    public PriorityQueue<StoredGame> getBackLog() {
    	return backLog;
    }
    
    public PriorityQueue<StoredGame> getTop4() {
    	return top4;
    }
    
    public PriorityQueue<StoredGame> getList(String listName) {
    	File fXmlFile = new File("accounts.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			
			doc.getDocumentElement().normalize();
			
			Element table = doc.getDocumentElement();
			NodeList listsList = (NodeList) table.getChildNodes().item(3);
			NodeList favoritesList = null;
			
			for (int i = 1; i < listsList.getLength(); i += 2) {
				String lName = listsList.item(i).getNodeName();
				if (lName.equals(listName)) {
					favoritesList = (NodeList) listsList.item(i);
				}
			}
			
			PriorityQueue list = new PriorityQueue<StoredGame>(100, comparator);
			
			for (int i = 1; i < favNodeList.getLength(); i+=2) {
				//Node game = favNodeList.item(i);
				String title = favNodeList.item(i).getTextContent().trim();
				
				System.out.println("SUCK " + title + " " + favorites.size());
				
				
				list.add((new StoredGame(title, i)));
			}
			
			return list;
			
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
		return null;
    }
    
    public boolean isInFavorites(String title, String listName) {
    	File fXmlFile = new File("accounts.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			
			doc.getDocumentElement().normalize();
			
			Element table = doc.getDocumentElement();
			NodeList listsList = (NodeList) table.getChildNodes().item(3);
			NodeList favoritesList = null;
			
			for (int i = 1; i < listsList.getLength(); i += 2) {
				String lName = listsList.item(i).getNodeName();
				if (lName.equals(listName)) {
					favoritesList = (NodeList) listsList.item(i);
				}
			}

			for (int i = 1; i < favoritesList.getLength(); i += 2) {
		        String favName = favoritesList.item(i).getTextContent().trim();
		        if (title.equals(favName)) {
		        	return true;
		        }
		    }
			
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
		
    }
    
    public static void loadLists() {
    	loadFavorites();
    	loadBeaten();
    	loadBackLog();
    	loadTop4();
    }
    
    public static void loadFavorites() {
    	
    	try {
    		favorites.clear();
    		
    		File fXmlFile = new File("accounts.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
	
			doc.getDocumentElement().normalize();
			
			//favNodeList = doc.getElementsByTagName("Favorites");
			Element table = doc.getDocumentElement();
			name = table.getChildNodes().item(1).getTextContent().trim();
			NodeList listsList = (NodeList) table.getChildNodes().item(3);
			favNodeList = (NodeList) listsList.item(1);
			
			
			
			//storedName.getElementsByTagName(name).getLength();
			//int lengtth = favNodeList.getLength();
			
			
			for (int i = 1; i < favNodeList.getLength(); i+=2) {
				//Node game = favNodeList.item(i);
				String title = favNodeList.item(i).getTextContent().trim();
				
				//System.out.println("FAVORITES: " + title + " " + favorites.size());
				
				favorites.add((new StoredGame(title, i)));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public static void loadBeaten() {
    	
    	try {
    		beaten.clear();
    		
    		File fXmlFile = new File("accounts.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
	
			doc.getDocumentElement().normalize();
			
			//favNodeList = doc.getElementsByTagName("Favorites");
			Element table = doc.getDocumentElement();
			NodeList listsList = (NodeList) table.getChildNodes().item(3);
			beatenNodeList = (NodeList) listsList.item(3);
			
			
			
			//storedName.getElementsByTagName(name).getLength();
			//int lengtth = favNodeList.getLength();
			
			
			for (int i = 1; i < beatenNodeList.getLength(); i+=2) {
				//Node game = favNodeList.item(i);
				String title = beatenNodeList.item(i).getTextContent().trim();
				
				//System.out.println("BEATEN: " + title + " " + beaten.size());
				
				beaten.add((new StoredGame(title, i)));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	public static void loadBackLog() {
		
		try {
			backLog.clear();
			
			File fXmlFile = new File("accounts.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
	
			doc.getDocumentElement().normalize();
			
			//favNodeList = doc.getElementsByTagName("Favorites");
			Element table = doc.getDocumentElement();
			NodeList listsList = (NodeList) table.getChildNodes().item(3);
			backLogNodeList = (NodeList) listsList.item(5);
			
			
			
			//storedName.getElementsByTagName(name).getLength();
			//int lengtth = favNodeList.getLength();
			
			
			for (int i = 1; i < backLogNodeList.getLength(); i+=2) {
				//Node game = favNodeList.item(i);
				String title = backLogNodeList.item(i).getTextContent().trim();
				
				//System.out.println("BACKLOG " + title + " " + backLog.size());
				
				backLog.add((new StoredGame(title, i)));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void loadTop4() {
    	
    	try {
    		top4.clear();
    		
    		File fXmlFile = new File("accounts.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
	
			doc.getDocumentElement().normalize();
			
			//favNodeList = doc.getElementsByTagName("Favorites");
			Element table = doc.getDocumentElement();
			NodeList listsList = (NodeList) table.getChildNodes().item(3);
			top4NodeList = (NodeList) listsList.item(7);
			
			
			
			//storedName.getElementsByTagName(name).getLength();
			//int lengtth = favNodeList.getLength();
			
			
			for (int i = 1; i < top4NodeList.getLength(); i+=2) {
				//Node game = favNodeList.item(i);
				String title = top4NodeList.item(i).getTextContent().trim();
				
				//System.out.println("TOP4: " + title + " " + top4.size());
				
				top4.add((new StoredGame(title, i)));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public static void removeFromList(String title, String listName) throws SAXException, IOException, ParserConfigurationException, TransformerException {
    	File fXmlFile = new File("accounts.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		
		
		doc.getDocumentElement().normalize();
		Element table = doc.getDocumentElement();
		//NodeList favs = table.getChildNodes().item(0).getTextContent();
		
		NodeList listsList = (NodeList) table.getChildNodes().item(3);
		
		//To get to the actual games
		//System.out.println("Help: " + table.getChildNodes().item(3).getNodeName()/*.getChildNodes().item(1).getChildNodes().item(3).getTextContent()*/);
		
		NodeList favoritesList = null;
		
		for (int i = 1; i < listsList.getLength(); i += 2) {
			String lName = listsList.item(i).getNodeName(); 
			
			
			if (lName.equals(listName)) {
				//System.out.println(lName);
				favoritesList = (NodeList) listsList.item(i);
			}
			
		}
		
		for (int i = 1; i < favoritesList.getLength(); i += 2) {
	        String favName = favoritesList.item(i).getTextContent().trim();
	        if (title.equals(favName)) {
	        	
	        	favoritesList.item(i).getParentNode().removeChild(favoritesList.item(i));
	        	
	        	TransformerFactory transformerFactory = TransformerFactory.newInstance();
	            Transformer transformer = transformerFactory.newTransformer();
	            DOMSource source = new DOMSource(doc);
	            StreamResult result = new StreamResult(new File("accounts.xml"));
	            transformer.transform(source, result);

	        }
	    }
    }
    
    public static void addToList(String title, String listName) throws TransformerException, ParserConfigurationException, SAXException, IOException {
		File fXmlFile = new File("accounts.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);

		
		doc.getDocumentElement().normalize();
		
		NodeList listsList = doc.getElementsByTagName("lists");
		
		NodeList favoritesList = doc.getElementsByTagName(listName);
		
		if (favoritesList.item(0) == null) {
			Element newList = doc.createElement(listName);
			listsList.item(0).appendChild(newList);
			

	         //favorites.appendChild(name);
	         
	     
		} 
		
		Element name = doc.createElement("name");
		
        name.appendChild(doc.createTextNode(title));

        //favorites.appendChild(name);
        
        favoritesList.item(0).appendChild(name);
        
		
		
		
         TransformerFactory factory = TransformerFactory.newInstance();
         Transformer transformer = factory.newTransformer();
         DOMSource domSource = new DOMSource(doc);
         
         transformer.setOutputProperty(OutputKeys.INDENT, "yes");
         transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
         
         
         StreamResult streamResult = new StreamResult(new File("accounts.xml"));
         transformer.transform(domSource, streamResult);
	}

    /*public PriorityQueue<StoredGame> getList(String name) {
    	
    }*/
    
    public String[] getStats() {
    	String[] results = new String[4];
    	
    	results[0] = name;
		results[1] = "" + favorites.size();
		results[2] = "" + beaten.size();
		results[3] = "" + backLog.size();
			
		return results;
    }
    
    //bueue.add(new StoredGame(search("Super Mario Odyssey", root).firstElement(), 2));
    //bueue.add(new StoredGame(search("L.A. Noire", root).firstElement(), 3));
    //bueue.add(new StoredGame(search("Stardew Valley", root).firstElement(), 21));
    
    //System.out.println(bueue.remove().getGame().getName());
    //System.out.println(bueue.remove().getGame().getName());
    //System.out.println(bueue.remove().getGame().getName());
}
