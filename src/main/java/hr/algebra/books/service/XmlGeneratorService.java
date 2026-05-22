package hr.algebra.books.service;

import hr.algebra.books.model.Book;
import hr.algebra.books.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.List;

@Service
@RequiredArgsConstructor
public class XmlGeneratorService {

    private final BookRepository repository;

    public static final String XML_FILE_PATH = "src/main/resources/xml/books.xml";

    public void generateXmlFromDatabase() throws Exception {
        List<Book> books = repository.findAll();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();

        Element root = document.createElement("books");
        document.appendChild(root);

        for (Book book : books) {
            Element bookEl = document.createElement("book");

            appendElement(document, bookEl, "id",
                String.valueOf(book.getId()));
            appendElement(document, bookEl, "title",
                book.getTitle());
            appendElement(document, bookEl, "description",
                book.getDescription() != null ? book.getDescription() : "");
            appendElement(document, bookEl, "pageCount",
                String.valueOf(book.getPageCount()));
            appendElement(document, bookEl, "excerpt",
                book.getExcerpt() != null ? book.getExcerpt() : "");
            appendElement(document, bookEl, "publishDate",
                book.getPublishDate() != null ? book.getPublishDate().toString() : "");

            root.appendChild(bookEl);
        }

        File xmlFile = new File(XML_FILE_PATH);
        xmlFile.getParentFile().mkdirs();

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(xmlFile);
        transformer.transform(source, result);
    }

    private void appendElement(Document doc, Element parent,
                                String tagName, String value) {
        Element el = doc.createElement(tagName);
        el.setTextContent(value);
        parent.appendChild(el);
    }
}
