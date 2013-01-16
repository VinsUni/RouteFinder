package com.tharun.routes;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

import java.io.IOException;

/**
 * HTML to plain-text. This example program demonstrates the use of jsoup to convert HTML input to lightly-formatted
 * plain-text. That is divergent from the general goal of jsoup's .text() methods, which is to get clean data from a
 * scrape.
 * <p/>
 * Note that this is a fairly simplistic formatter -- for real world use you'll want to embrace and extend.
 *
 * 
 */
public class HtmlToPlainText {
    public static void main(String... args) throws IOException {
//        Validate.isTrue(args.length == 1, "usage: supply url to fetch");
//        String url = args[0];
    	String url = "http://en.wikipedia.org/wiki/Utah_State_Route_201";

        // fetch the specified URL and parse to a HTML DOM
        Document doc = Jsoup.connect(url).get();

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
        String noHTMLString = output.replaceAll("\\<.*?\\>", "").replaceAll("\\[.*?\\]", "");
        System.out.println(noHTMLString);
        
        
//        System.out.println(plainText);
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