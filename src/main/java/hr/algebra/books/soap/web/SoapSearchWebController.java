package hr.algebra.books.soap.web;

import hr.algebra.books.book.dto.BookDto;
import hr.algebra.books.book.service.BookService;
import hr.algebra.books.soap.service.BooksXmlGenerator;
import hr.algebra.books.soap.service.XPathFilterService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/soap-search")
public class SoapSearchWebController {

    private final BookService bookService;
    private final BooksXmlGenerator xmlGenerator;
    private final XPathFilterService xpathFilter;

    public SoapSearchWebController(BookService bookService,
                                   BooksXmlGenerator xmlGenerator,
                                   XPathFilterService xpathFilter) {
        this.bookService = bookService;
        this.xmlGenerator = xmlGenerator;
        this.xpathFilter = xpathFilter;
    }

    @GetMapping
    public String form(Model model) {
        model.addAttribute("term", "");
        model.addAttribute("results", List.of());
        return "soap/search";
    }

    @PostMapping
    public String search(@RequestParam String term, Model model) {
        List<BookDto> results = List.of();
        String error = null;
        try {
            List<BookDto> all = bookService.findAll();
            Document doc = xmlGenerator.generate(all);
            List<Node> nodes = xpathFilter.filter(doc, term);

            List<BookDto> filtered = new ArrayList<>();
            for (Node node : nodes) {
                filtered.add(new BookDto(
                        parseLong(xpathFilter.childText(node, "id")),
                        xpathFilter.childText(node, "title"),
                        xpathFilter.childText(node, "description"),
                        parseInt(xpathFilter.childText(node, "pageCount")),
                        xpathFilter.childText(node, "excerpt"),
                        parseInstant(xpathFilter.childText(node, "publishDate"))
                ));
            }
            results = filtered;
        } catch (Exception e) {
            error = "Search failed: " + e.getMessage();
        }

        model.addAttribute("term", term);
        model.addAttribute("results", results);
        if (error != null) model.addAttribute("error", error);
        return "soap/search";
    }

    private Long parseLong(String s) {
        try { return Long.parseLong(s); } catch (Exception e) { return null; }
    }

    private int parseInt(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return 0; }
    }

    private Instant parseInstant(String s) {
        if (s == null || s.isBlank()) return null;
        try { return Instant.parse(s); } catch (Exception e) { return null; }
    }
}
