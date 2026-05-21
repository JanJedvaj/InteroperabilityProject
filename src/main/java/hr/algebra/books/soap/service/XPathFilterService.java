package hr.algebra.books.soap.service;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class XPathFilterService {

    // Croatian alphabet including diacritics for translate()
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZŠĐČĆŽ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyzšđčćž";

    /**
     * Filters books in the DOM by case-insensitive term match on title or description.
     * Uses XPath translate() to handle Croatian diacritics, then returns matching book elements.
     */
    public List<Node> filter(Document doc, String term) throws Exception {
        String lowerTerm = term.toLowerCase(Locale.forLanguageTag("hr-HR"));

        String titleExpr   = "translate(title,'" + UPPER + "','" + LOWER + "')";
        String descExpr    = "translate(description,'" + UPPER + "','" + LOWER + "')";
        String expression  = "//book[contains(" + titleExpr + ",'" + lowerTerm + "') " +
                             "or contains(" + descExpr + ",'" + lowerTerm + "')]";

        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList nodes = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);

        List<Node> result = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            result.add(nodes.item(i));
        }
        return result;
    }

    /** Extracts text content of a named child element from a node. */
    public String childText(Node node, String childName) {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (childName.equals(child.getNodeName())) {
                return child.getTextContent();
            }
        }
        return "";
    }
}
