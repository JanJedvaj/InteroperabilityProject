package hr.algebra.books.soap;

import hr.algebra.books.book.dto.BookDto;
import hr.algebra.books.soap.service.BooksXmlGenerator;
import hr.algebra.books.soap.service.XPathFilterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for XPathFilterService + BooksXmlGenerator — no Spring context needed.
 * Verifies case-insensitive search including Croatian diacritic folding.
 */
class XPathFilterServiceTest {

    private BooksXmlGenerator generator;
    private XPathFilterService filter;

    private static final List<BookDto> BOOKS = List.of(
            new BookDto(1L, "Clean Code", "A handbook of agile craftsmanship", 431, null, null),
            new BookDto(2L, "Šibenik Guide", "Turistički vodič kroz Šibenik", 120, null, null),
            new BookDto(3L, "Design Patterns", "Gang of Four classic", 395, null, null)
    );

    @BeforeEach
    void setUp() {
        generator = new BooksXmlGenerator();
        filter = new XPathFilterService();
    }

    @Test
    void exactTitleMatch_returnsBook() throws Exception {
        Document doc = generator.generate(BOOKS);
        List<Node> result = filter.filter(doc, "Clean Code");
        assertThat(result).hasSize(1);
        assertThat(filter.childText(result.get(0), "title")).isEqualTo("Clean Code");
    }

    @Test
    void partialTitleMatch_caseInsensitive_returnsBook() throws Exception {
        Document doc = generator.generate(BOOKS);
        List<Node> result = filter.filter(doc, "clean");
        assertThat(result).hasSize(1);
    }

    @Test
    void descriptionMatch_returnsBook() throws Exception {
        Document doc = generator.generate(BOOKS);
        List<Node> result = filter.filter(doc, "gang of four");
        assertThat(result).hasSize(1);
        assertThat(filter.childText(result.get(0), "title")).isEqualTo("Design Patterns");
    }

    @Test
    void croatianDiacriticTitle_matchesWithoutDiacritics() throws Exception {
        // The XPath translate() normalizes ŠĐČĆŽ → šđčćž but does NOT strip them.
        // Searching with the diacritic should still match.
        Document doc = generator.generate(BOOKS);
        List<Node> result = filter.filter(doc, "šibenik");
        assertThat(result).hasSize(1);
        assertThat(filter.childText(result.get(0), "title")).isEqualTo("Šibenik Guide");
    }

    @Test
    void uppercaseSearch_matchesLowercaseTitle() throws Exception {
        Document doc = generator.generate(BOOKS);
        List<Node> result = filter.filter(doc, "DESIGN");
        assertThat(result).hasSize(1);
    }

    @Test
    void noMatch_returnsEmptyList() throws Exception {
        Document doc = generator.generate(BOOKS);
        List<Node> result = filter.filter(doc, "nonexistentterm12345");
        assertThat(result).isEmpty();
    }

    @Test
    void emptyTerm_matchesAllBooks() throws Exception {
        Document doc = generator.generate(BOOKS);
        // Empty term: contains("", "") is always true
        List<Node> result = filter.filter(doc, "");
        assertThat(result).hasSize(BOOKS.size());
    }

    @Test
    void childText_extractsCorrectValue() throws Exception {
        Document doc = generator.generate(BOOKS);
        List<Node> result = filter.filter(doc, "Clean Code");
        assertThat(result).hasSize(1);
        assertThat(filter.childText(result.get(0), "id")).isEqualTo("1");
        assertThat(filter.childText(result.get(0), "pageCount")).isEqualTo("431");
    }
}
