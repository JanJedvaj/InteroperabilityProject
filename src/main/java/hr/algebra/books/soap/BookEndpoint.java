package hr.algebra.books.soap;

import hr.algebra.books.service.JakartaXmlValidationService;
import hr.algebra.books.service.XmlGeneratorService;
import hr.algebra.books.soap.generated.BookItem;
import hr.algebra.books.soap.generated.SearchBooksRequest;
import hr.algebra.books.soap.generated.SearchBooksResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.util.List;

@Endpoint
@RequiredArgsConstructor
public class BookEndpoint {

    private static final String NAMESPACE_URI = "http://algebra.hr/books/soap";

    private final XmlGeneratorService xmlGeneratorService;
    private final JakartaXmlValidationService jakartaXmlValidationService;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "searchBooksRequest")
    @ResponsePayload
    public SearchBooksResponse searchBooks(
            @RequestPayload SearchBooksRequest request) throws Exception {

        xmlGeneratorService.generateXmlFromDatabase();

        List<String> validationErrors = jakartaXmlValidationService
            .validateBooksXml(XmlGeneratorService.XML_FILE_PATH);

        if (!validationErrors.isEmpty()) {
            String errorMessage = "XML validacija nije prošla:\n" +
                String.join("\n", validationErrors);
            throw new RuntimeException(errorMessage);
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(
            new File(XmlGeneratorService.XML_FILE_PATH)
        );

        String term = request.getTerm();
        XPath xPath = XPathFactory.newInstance().newXPath();
        String expression = String.format(
            "//book[contains(title, '%s') or contains(description, '%s')]",
            term, term
        );

        NodeList nodes = (NodeList) xPath.evaluate(
            expression, document, XPathConstants.NODESET
        );

        SearchBooksResponse response = new SearchBooksResponse();
        for (int i = 0; i < nodes.getLength(); i++) {
            org.w3c.dom.Element el = (org.w3c.dom.Element) nodes.item(i);

            BookItem item = new BookItem();
            item.setId(Long.parseLong(getTagValue(el, "id")));
            item.setTitle(getTagValue(el, "title"));
            item.setDescription(getTagValue(el, "description"));
            String pc = getTagValue(el, "pageCount");
            item.setPageCount(pc.isEmpty() ? 0 : Integer.parseInt(pc));
            item.setExcerpt(getTagValue(el, "excerpt"));
            item.setPublishDate(getTagValue(el, "publishDate"));

            response.getBooks().add(item);
        }

        return response;
    }

    private String getTagValue(org.w3c.dom.Element element, String tagName) {
        var nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return "";
    }
}
