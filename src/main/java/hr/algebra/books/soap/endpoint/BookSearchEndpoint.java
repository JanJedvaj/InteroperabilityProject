package hr.algebra.books.soap.endpoint;

import hr.algebra.books.book.dto.BookDto;
import hr.algebra.books.book.service.BookService;
import hr.algebra.books.soap.dto.SearchBooksByTermRequest;
import hr.algebra.books.soap.dto.SearchBooksByTermResponse;
import hr.algebra.books.soap.dto.SoapBook;
import hr.algebra.books.soap.service.BooksXmlGenerator;
import hr.algebra.books.soap.service.XPathFilterService;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.List;

@Endpoint
public class BookSearchEndpoint {

    private static final String NAMESPACE = "http://algebra.hr/books/soap";

    private final BookService bookService;
    private final BooksXmlGenerator xmlGenerator;
    private final XPathFilterService xpathFilter;

    public BookSearchEndpoint(BookService bookService,
                               BooksXmlGenerator xmlGenerator,
                               XPathFilterService xpathFilter) {
        this.bookService  = bookService;
        this.xmlGenerator = xmlGenerator;
        this.xpathFilter  = xpathFilter;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "searchBooksByTermRequest")
    @ResponsePayload
    public SearchBooksByTermResponse search(@RequestPayload SearchBooksByTermRequest request) throws Exception {
        List<BookDto> allBooks = bookService.findAll();
        Document doc = xmlGenerator.generate(allBooks);

        String term = request.getTerm() == null ? "" : request.getTerm().trim();
        List<Node> matchingNodes = xpathFilter.filter(doc, term);

        SearchBooksByTermResponse response = new SearchBooksByTermResponse();
        for (Node node : matchingNodes) {
            SoapBook book = new SoapBook(
                parseLong(xpathFilter.childText(node, "id")),
                xpathFilter.childText(node, "title"),
                xpathFilter.childText(node, "description"),
                parseInt(xpathFilter.childText(node, "pageCount")),
                xpathFilter.childText(node, "excerpt"),
                xpathFilter.childText(node, "publishDate")
            );
            response.getBooks().add(book);
        }
        return response;
    }

    private Long parseLong(String s) {
        try { return Long.parseLong(s); } catch (NumberFormatException e) { return 0L; }
    }

    private int parseInt(String s) {
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return 0; }
    }
}
