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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
public class Autotagging
{
	public static String PATH= "Files/";
//	public static String PATH= "/home/tharun/NLP/";
	
	public static ArrayList<String> landmarksList = new ArrayList<String>();
	public static ArrayList<String> directionsList = new ArrayList<String>();
	public static String TAG_TYPE_LANDMARK = "landmark";
	public static String TAG_TYPE_DIRECTIONS = "directions";
	
	public static void main(String args[])  
	{
		
		String routeDescription = "You are standing with your back to the south entrance to the Quick Stop. Turn left so you are walking east." +
								  "On your left you will pass the ATM machines which make distinctive sounds, and the campus post office and mailbox. " +
								  " You will pass the entrance to the financial aid office on your right and several bulletin boards." +
								  " Continue walking east and passing offices, the barber shop, and the copy center" +
								  " as you walk down this long hall. Towards the eastern end of the building, you" +
								  " will come to a wide open area on your left. Turn left and walk a little north." +
								  " Pass Taco Time on your left, and look for a small opening on your lift. This" +
								  " opening will have a cashier counter on your right. Turn left and enter the world" +
								  "of the Hub. You will find a wide variety of food stations around a semicircle.";
		loadLandMarks();
		String[] routeDescSentenses= sentenseDetection(routeDescription);
		for(String s: routeDescSentenses)
		{	
				stringTokeniser(s);
				System.out.println("__________________________________________________________________");
		}
		
	}
	
	public static void namedEntityRecongition(String[] tokens) throws FileNotFoundException
	{
		InputStream modelIn = new FileInputStream(PATH+"en-ner-location.bin");
//		InputStream modelIn = new FileInputStream("C:/Users/tharun/Dropbox/NLP");
		try {
		  TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
		  NameFinderME nameFinder = new NameFinderME(model);
		  System.out.println("Entered");
		  String sentence[] = new String[]{
				    "Donald",
				    "Daniel",
				    "is",
				    "61",
				    "years",
				    "old",
				    ".",
				    "lives",
				    "in",
				    "san jose",
				    "jose"
				    };

				Span nameSpans[] = nameFinder.find(tokens);
				for(Span s : nameSpans)
				{
					System.out.println("Found entity: " + Arrays.toString(Span.spansToStrings(nameSpans, tokens)));
//					System.out.println(s);
				}
				
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
	
	private static String[] sentenseDetection(String description)
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
		//Sentence Detection API
//		System.out.println("Sentence Detection API\n"+"\n");
		SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
		String sentences[] = sentenceDetector.sentDetect(description);
		
//		for(String s : sentences)
//		{
//			System.out.println(s);
//		}
		
		return sentences;
		
	}
	
	
	private static void stringTokeniser(String sentence)
	{
		InputStream modelIn = null;
		 TokenizerModel tModel = null;
		
		try {
			 modelIn = new FileInputStream(PATH+"en-token.bin");

		    tModel = new TokenizerModel(modelIn);
		    
		    Tokenizer tokenizer = new TokenizerME(tModel);
			
			String tokens[] = tokenizer.tokenize(sentence);
			namedEntityRecongition(tokens);
			partsOfSpeechTagger(tokens);
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
	
	private static void partsOfSpeechTagger( String[] tokens)
	{
		InputStream modelIn = null;
		ArrayList<String> nounsList = new ArrayList<String>();
		nounsList.add("NN");
		nounsList.add("NNS");
		nounsList.add("NNP");
		nounsList.add("NNPS");
		
		
		try {
		  modelIn = new FileInputStream(PATH+"en-pos-maxent.bin");
		  POSModel model = new POSModel(modelIn);
		  
		  POSTaggerME tagger = new POSTaggerME(model);
		  
//		  String sent[] = new String[]{"Most", "large", "cities", "in", "the", "San jose","old main hill" ,"Animal science","had",
//                  "morning", "and", "afternoon", "newspapers", "."};
//		  String sent[] = new String[] {"You", "are", "standing"," with", "your","back","to", "the"," south ","entrance",
//				  "to", "the","Quick","Stop "};
		  
		  
		  String tags[] = tagger.tag(tokens);
		  
		  for(int i=0;i<tags.length;i++)
		  {
			  if(nounsList.contains((tags[i]).toUpperCase())) 
			  {
				  System.out.println(tokens[i] +"--->"+tags[i]);
			  
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
	
//	private static void namedEntityTrainer() throws IOException
//	{
//		
//		System.out.println("Entered namedEntityTrainer");
//		FileReader fileReader = new FileReader("/home/tharun/NLP/train.txt");
//		ObjectStream<String> fileStream = new PlainTextByLineStream(fileReader);
//		ObjectStream<NameSample> sampleStream = new NameSampleDataStream(fileStream);
//		TokenNameFinderModel model = NameFinderME.train("pt-br", "train", sampleStream, Collections.<String, Object>emptyMap());
//		  NameFinderME nfm = new NameFinderME(model);
//		  
//		  String sentence[] = new String[]{
//				    "Donald",
//				    "Daniel",
//				    "is",
//				    "61m",
//				    "50kg",
//				    "old",
//				    "."
//				    };
//		  
//
//				Span nameSpans[] = nfm.find(sentence);
//				for(Span s : nameSpans)
//				{
//					System.out.println("Found namedEntityTrainer: " + Arrays.toString(Span.spansToStrings(nameSpans, sentence)));
////					System.out.println(s);
//				}
//				
//						nfm.clearAdaptiveData();
//		}
//		  
		  
	
	private static void loadLandMarks()
	{
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try 
		{
			 builder = builderFactory.newDocumentBuilder();
			 Document document  = builder.parse(PATH+"Autotagging.xml");
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
//				           System.out.println(theAttribute.getNodeName() + "=" + theAttribute.getNodeValue());
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
			 
//			 for(String s: landmarksList)
//			 System.out.println(s);
//			 
//			 
//			 System.out.println("____________________________________________");
//			 
//			 for(String s:directionsList)
//				 System.out.println(s);
			 
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
			}
			else if(tagType.equalsIgnoreCase(TAG_TYPE_DIRECTIONS))
			{	
				String directions = eElement.getElementsByTagName(sTag).item(i).getChildNodes().item(0).getNodeValue();
					directionsList.add(directions);
			}
		}
	    }
	 

}
