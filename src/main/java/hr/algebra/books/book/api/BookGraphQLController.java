package hr.algebra.books.book.api;

import hr.algebra.books.book.dto.BookDto;
import hr.algebra.books.book.service.BookService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.util.List;

@Controller
public class BookGraphQLController {

    private final BookService bookService;

    public BookGraphQLController(BookService bookService) {
        this.bookService = bookService;
    }

    @QueryMapping
    public List<BookDto> books(@Argument String term) {
        if (term == null || term.isBlank()) return bookService.findAll();
        return bookService.search(term);
    }

    @QueryMapping
    public BookDto book(@Argument Long id) {
        return bookService.findById(id);
    }

    @PreAuthorize("hasRole('FULL')")
    @MutationMapping
    public BookDto createBook(@Argument BookInput input) {
        return bookService.create(input.toDto());
    }

    @PreAuthorize("hasRole('FULL')")
    @MutationMapping
    public BookDto updateBook(@Argument Long id, @Argument BookInput input) {
        return bookService.update(id, input.toDto());
    }

    @PreAuthorize("hasRole('FULL')")
    @MutationMapping
    public boolean deleteBook(@Argument Long id) {
        bookService.delete(id);
        return true;
    }

    // -------------------------------------------------------------------------
    // Input type matching GraphQL BookInput — publishDate as ISO-8601 String
    // -------------------------------------------------------------------------
    public record BookInput(String title, String description, int pageCount,
                            String excerpt, String publishDate) {
        BookDto toDto() {
            Instant ts = null;
            if (publishDate != null && !publishDate.isBlank()) {
                try { ts = Instant.parse(publishDate); } catch (Exception ignored) {}
            }
            return new BookDto(null, title, description, pageCount, excerpt, ts);
        }
    }
}
