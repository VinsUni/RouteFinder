package com.tharun.routes;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

/**
 * HTML to plain-text. This example program demonstrates the use of jsoup to convert HTML input to lightly-formatted
 * plain-text. That is divergent from the general goal of jsoup's .text() methods, which is to get clean data from a
 * scrape.
 * <p/>
 * Note that this is a fairly simplistic formatter -- for real world use you'll want to embrace and extend.
 *
 * 
 */
public class HtmlToPlainText 
{
    public static void main(String... args) throws IOException {
//        Validate.isTrue(args.length == 1, "usage: supply url to fetch");
//        String url = args[0];

    		String ROUTEDESCRIPTIONS_PATH = "RouteDescriptions/";
    	String ROUTELANDMARKS_PATH = "RouteLandmarks/";
    	  WriteXMLFile writeXmlfileObj = new WriteXMLFile();
    	  
    	  int listofRoutes[] = {11,13,14,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,
    			  38,39,42,43,44,45,46,47,48,49,51,52,53,54,55,56,57,58,59,60,62,63,64,65,66,67,68,
    			  69,71,72,73,74,75,76,77,78,79,82,83,87,88,90,92,93,94,95,96,97,99,100,101,102,103,104,105,
    			  106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122,123,124,125,126,127,
    			  128,130,131,132,133,134,136,137,138,139,140,142,143,144,145,146,147,148,149,150,151,152,
    			  153,154,155,156,157,158,159,160,161,162,167,164,165,168,171,172,173,174,175,178,180,181,
    			  192,193,196,197,198,199,201,202,203,204,208,209,210,211,212,217,218,219,222,224,225};
    	  for(int i : listofRoutes)
    	  {
    		  String url = "http://en.wikipedia.org/wiki/" ;
    		  String endString ="Utah_State_Route_";
    		    
    		  // fetch the specified URL and parse to a HTML DOM
		        Document doc = Jsoup.connect(url+endString+i).get();
		
		        HtmlToPlainText formatter = new HtmlToPlainText();
		        String plainText = formatter.getPlainText(doc);
		        String ROUTE_DESCRIPTION = "Route description";
		        String HISTORY = "History";
		        int firstIndex = plainText.indexOf(ROUTE_DESCRIPTION);
		        int requiredStartText = plainText.indexOf(ROUTE_DESCRIPTION, ++firstIndex);
		        System.out.println(requiredStartText);
		        
		        int endIndex = plainText.indexOf(HISTORY, requiredStartText);
		        System.out.println(endIndex);
		       
		        String output = plainText.substring(requiredStartText, endIndex);
		      
		        writeXmlfileObj.writeToXmlFile(ROUTELANDMARKS_PATH,endString+i+".xml", getTagValues(output));
//		        System.out.println(Arrays.toString(getTagValues(output).toArray()));
//		        System.out.println(output);
		        String noHTMLString = output.replaceAll("\\<.*?\\>", "").replaceAll("\\[.*?\\]", "");
//		        System.out.println(noHTMLString);
		        writeXmlfileObj.writeToFile(ROUTEDESCRIPTIONS_PATH,endString+i+".txt",noHTMLString);
        
//        System.out.println(plainText);
    }
    }
   
	private static final Pattern TAG_REGEX = Pattern.compile("<http://.*/(.+?)>");


    private static List<String> getTagValues(final String str) {
        final List<String> tagValues = new ArrayList<String>();
        final Matcher matcher = TAG_REGEX.matcher(str);
        while (matcher.find()) {
            tagValues.add(matcher.group(1));
        }
        return tagValues;
    }
    /**
     * Format an Element to plain-text
     * @param element the root element to format
     * @return formatted text
     */
    public String getPlainText(Element element) {
        FormattingVisitor formatter = new FormattingVisitor();
        NodeTraversor traversor = new NodeTraversor(formatter);
        traversor.traverse(element); // walk the DOM, and call .head() and .tail() for each node

        return formatter.toString();
    }

    // the formatting rules, implemented in a breadth-first DOM traverse
    private class FormattingVisitor implements NodeVisitor {
        private static final int maxWidth = 80;
        private int width = 0;
        private StringBuilder accum = new StringBuilder(); // holds the accumulated text

        // hit when the node is first seen
        public void head(Node node, int depth) {
            String name = node.nodeName();
            if (node instanceof TextNode)
                append(((TextNode) node).text()); // TextNodes carry all user-readable text in the DOM.
            else if (name.equals("li"))
                append("\n * ");
        }

        // hit when all of the node's children (if any) have been visited
        public void tail(Node node, int depth) {
            String name = node.nodeName();
            if (name.equals("br"))
                append("\n");
            else if (StringUtil.in(name, "p", "h1", "h2", "h3", "h4", "h5"))
                append("\n\n");
            else if (name.equals("a"))
                append(String.format(" <%s>", node.absUrl("href")));
        }

        // appends text to the string builder with a simple word wrap method
        private void append(String text) {
            if (text.startsWith("\n"))
                width = 0; // reset counter if starts with a newline. only from formats above, not in natural text
            if (text.equals(" ") &&
                    (accum.length() == 0 || StringUtil.in(accum.substring(accum.length() - 1), " ", "\n")))
                return; // don't accumulate long runs of empty spaces

            if (text.length() + width > maxWidth) { // won't fit, needs to wrap
                String words[] = text.split("\\s+");
                for (int i = 0; i < words.length; i++) {
                    String word = words[i];
                    boolean last = i == words.length - 1;
                    if (!last) // insert a space if not the last word
                        word = word + " ";
                    if (word.length() + width > maxWidth) { // wrap and reset counter
                        accum.append("\n").append(word);
                        width = word.length();
                    } else {
                        accum.append(word);
                        width += word.length();
                    }
                }
            } else { // fits as is, without need to wrap text
                accum.append(text);
                width += text.length();
            }
        }

        public String toString() {
            return accum.toString();
        }
        
        
    }
}