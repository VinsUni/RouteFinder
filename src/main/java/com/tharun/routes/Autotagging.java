package com.tharun.routes;
/**
 * @author Tharun Tej Tammineni
 */

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

import org.hibernate.Query;
import org.hibernate.Session;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.tharun.persistence.HibernateUtil;
public class Autotagging
{
	public static String PATH= "Files/";
	public static String LANDMARKS_PATH = "RouteLandmarks/";
//	public static String PATH= "/home/tharun/NLP/";
	
	public static ArrayList<String> landmarksList = new ArrayList<String>();
	public static ArrayList<String> directionsList = new ArrayList<String>();
	public static String TAG_TYPE_LANDMARK = "landmark";
	public static String TAG_TYPE_DIRECTIONS = "directions";
	public static Trie landmarksTree = null;
	public static Trie directionsTree = null;
	
//	 Method is used to extract the landmarks from sentenses.
	public static void extractLandMarks(List list)
	{
		for(int i=20; i< list.size();i++)
		{
			landmarksTree = new Trie();
			directionsTree = new Trie();
			RouteSentenses routeSentenses= (RouteSentenses) list.get(i);
			loadLandMarks(routeSentenses.getRouteIdUrl().substring(routeSentenses.getRouteIdUrl().lastIndexOf("/")+1));
			System.out.println(routeSentenses.getSentenses());
			
			stringTokeniser(routeSentenses.getSentenses(),routeSentenses.getSentenseId());
			System.out.println("__________________________________________________________________");
			
		}
	}
	
	public static void namedEntityRecongition(String[] tokens, Integer routeSentenseId) throws FileNotFoundException
	{
		InputStream modelIn = new FileInputStream(PATH+"en-ner-location.bin");
 		try {
		  TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
		  NameFinderME nameFinder = new NameFinderME(model);
		  System.out.println("Entered");
		  				Span nameSpans[] = nameFinder.find(tokens);
		  				String entity = "";
		  		for(Span s : nameSpans)
				{
					 entity = Arrays.toString(Span.spansToStrings(nameSpans, tokens));
					System.out.println("Found entity: " +entity.substring(1,entity.length()-1));
					System.out.println("Route Id "+ routeSentenseId);
				
//					System.out.println(s);
				}
		 if(!entity.equals("") && entity != null)
			 writeLandmarksToDb(entity.substring(1,entity.length()-1).split(","),routeSentenseId);
				
						nameFinder.clearAdaptiveData();
		}
		catch (IOException e) {
		  e.printStackTrace();
		}
		finally {
		  if (modelIn != null) {
		    try {
		      modelIn.close();
		    }
		    catch (IOException e) {
		    }
		  }
		}
	}
	
	public static String[] sentenseDetection(String description)
	{
		InputStream modelIn = null;
	    SentenceModel model =null;
		 
		try {
			modelIn = new FileInputStream(PATH+"en-sent.bin");
			model = new SentenceModel(modelIn);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
		  e.printStackTrace();
		}
		finally {
		  if (modelIn != null) {
		    try {
		      modelIn.close();
		    }
		    catch (IOException e) {
		    }
		  }
		}
		SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
		String sentences[] = sentenceDetector.sentDetect(description);
		

		return sentences;
		
	}
	
	
	private static void stringTokeniser(String sentence, Integer routeSentenseId)
	{
		InputStream modelIn = null;
		 TokenizerModel tModel = null;
		
		try {
			 modelIn = new FileInputStream(PATH+"en-token.bin");

		    tModel = new TokenizerModel(modelIn);
		    
		    Tokenizer tokenizer = new TokenizerME(tModel);
			
			String tokens[] = tokenizer.tokenize(sentence);
			namedEntityRecongition(tokens,routeSentenseId);
			partsOfSpeechTagger(tokens,routeSentenseId);
//			for(String s : tokens)
//			{
//				System.out.println(s);
//			}
		}
		catch (IOException e) {
		  e.printStackTrace();
		}
		finally {
		  if (modelIn != null) {
		    try {
		      modelIn.close();
		    }
		    catch (IOException e) {
		    }
		  }
		}
	}
	
	private static void partsOfSpeechTagger( String[] tokens, Integer routeSentenseId)
	{
		InputStream modelIn = null;
		ArrayList<String> nounsList = new ArrayList<String>();
		nounsList.add("NN");
		nounsList.add("NNS");
		nounsList.add("NNP");
		nounsList.add("NNPS");
		
		
		try 
		{
		  modelIn = new FileInputStream(PATH+"en-pos-maxent.bin");
		  POSModel model = new POSModel(modelIn);
		  
		  POSTaggerME tagger = new POSTaggerME(model);
		  String tags[] = tagger.tag(tokens);
		  String word = "";
		  ArrayList<String> landmarks = new ArrayList<String>();
		  for(int i=0;i<tags.length;i++)
		  {
			  
			  if(nounsList.contains((tags[i]).toUpperCase())) 
			  {
				  word += tokens[i];
				 if( landmarksTree.search(word.toLowerCase()))
				 {
					 System.out.println(word);
					
					 String[] words = new String[] {word};
					 writeLandmarksToDb(words,routeSentenseId);
					 word = "";
				 }   
				 else
				{
					 landmarks =  landmarksTree.getWordsMatchingPrefix(word, landmarksTree);
					 
					   if(landmarks.size() > 0)
					   {
						   if(landmarks.contains(word.toLowerCase()))
						   {
							   System.out.println(word);
							   String[] words = new String[] {word};
								 writeLandmarksToDb(words,routeSentenseId);
							   word = "";
						   }
						   else
						   {
//							   System.out.println(word);
							   word += " ";
						   }
					   }
					   else
					   {
//						   System.out.println(word);
						   word = "";
					   }
					 
					 
				 }

			  
			  }
		  }

		}
		catch (IOException e) {
		  // Model loading failed, handle the error
		  e.printStackTrace();
		}
		finally {
		  if (modelIn != null) {
		    try {
		      modelIn.close();
		    }
		    catch (IOException e) {
		    }
		  }
		}
	}
	

		  
	
	private static void loadLandMarks(String fileName)
	{
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try 
		{
			 builder = builderFactory.newDocumentBuilder();
//			 Document document  = builder.parse(PATH+"Autotagging.xml");
			 Document document  = builder.parse(LANDMARKS_PATH+fileName+".xml");
			 System.out.println(LANDMARKS_PATH+fileName+".xml");
			 Element rootElement = document.getDocumentElement();
		 	
		 	System.out.println("Root Element :" + document.getDocumentElement().getNodeName());
		 	  NodeList nodes = rootElement.getElementsByTagName("Entity");
		 	   
			 for(int i=0; i<nodes.getLength(); i++)
			 {
				   Node node = nodes.item(i);
				   Element fstElmnt = (Element) node;
				   NamedNodeMap attributes = fstElmnt.getAttributes();
				   for (int a = 0; a < attributes.getLength(); a++) 
				   {
				           Node theAttribute = attributes.item(a);
				           System.out.println(theAttribute.getNodeName() + "=" + theAttribute.getNodeValue());
				           if(theAttribute.getNodeValue().equalsIgnoreCase(TAG_TYPE_LANDMARK))
				           {
				        	   Element eElement = (Element) node;
							   getTagValue("Name", eElement,TAG_TYPE_LANDMARK) ;
					       }
				           else if (theAttribute.getNodeValue().equalsIgnoreCase(TAG_TYPE_DIRECTIONS))
				           {
				        	   Element eElement = (Element) node;
							   getTagValue("Direction", eElement,TAG_TYPE_DIRECTIONS) ;
					       }
				   }
			  }
			 
	 
		}catch(Exception e)
		{
			
		}
		 
	}
	
	 private static void getTagValue(String sTag, Element eElement, String tagType) 
	  {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
		int elementsLenght=eElement.getElementsByTagName(sTag).getLength();
		for(int i=0; i <elementsLenght;i++)
		{
			if(tagType.equalsIgnoreCase(TAG_TYPE_LANDMARK))
			{	
				String landmark = eElement.getElementsByTagName(sTag).item(i).getChildNodes().item(0).getNodeValue();
				
				landmarksList.add(landmark);
				
				landmarksTree.insert(landmark.toUpperCase());
			}
			else if(tagType.equalsIgnoreCase(TAG_TYPE_DIRECTIONS))
			{	
				String directions = eElement.getElementsByTagName(sTag).item(i).getChildNodes().item(0).getNodeValue();
					directionsList.add(directions);
					
					directionsTree.insert(directions.toUpperCase());
			}
		}
	    }
	 
	 private static void writeLandmarksToDb(String[] landmarks, Integer routeSentenseIds)
	 {
		 if(landmarks.length >0 && landmarks != null)
		 {	 
				 for(String currentLandmark :landmarks)
				 {
					 Session session = HibernateUtil.getSessionFactory().openSession();
					 Query query = session.createQuery("from RouteLandmarks where landmark = :landmarkName ");
					 query.setParameter("landmarkName", currentLandmark);
					 List list = query.list();
					 if(list.size() > 0)
					 {
						 // Update the particular record.
						 
						 for(int i=0; i< list.size();i++)
							{
								RouteLandmarks rd = (RouteLandmarks) list.get(i);
								
								 String existingRouteSentenseIds = rd.getSentenseIds();
								 existingRouteSentenseIds += ","+routeSentenseIds;
								 Query updateQuery = session.createQuery("update RouteLandmarks set sentenseIds = :ids where landmark = :landmarkName");
								
								 updateQuery.setParameter("landmarkName", currentLandmark);
								 updateQuery.setParameter("ids", existingRouteSentenseIds);
								 int result = updateQuery.executeUpdate();
							}
						 
					 }
					 else
					 {
						 //Insert
						 
						 	Session sessionInsert = HibernateUtil.getSessionFactory().openSession();
							sessionInsert.beginTransaction();
							RouteLandmarks routeLandmarks = new RouteLandmarks();
						  
							routeLandmarks.setLandmark(currentLandmark.trim());
							routeLandmarks.setSentenseIds(String.valueOf(routeSentenseIds));
		
							sessionInsert.save(routeLandmarks);
							sessionInsert.getTransaction().commit();
					 }
				 }
		 }
	 }
	 

}
