package hr.algebra.books.soap.service;

import hr.algebra.books.book.dto.BookDto;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.List;

@Component
public class BooksXmlGenerator {

    public Document generate(List<BookDto> books) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        Element root = doc.createElement("books");
        doc.appendChild(root);

        for (BookDto b : books) {
            Element book = doc.createElement("book");
            addChild(doc, book, "id", b.id() == null ? "" : b.id().toString());
            addChild(doc, book, "title", b.title());
            addChild(doc, book, "description", b.description());
            addChild(doc, book, "pageCount", String.valueOf(b.pageCount()));
            addChild(doc, book, "excerpt", b.excerpt());
            addChild(doc, book, "publishDate", b.publishDate() == null ? "" : b.publishDate().toString());
            root.appendChild(book);
        }

        return doc;
    }

    private void addChild(Document doc, Element parent, String name, String value) {
        Element el = doc.createElement(name);
        el.setTextContent(value == null ? "" : value);
        parent.appendChild(el);
    }
}
