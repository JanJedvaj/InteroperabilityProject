package hr.algebra.books.controller;

import hr.algebra.books.model.Book;
import hr.algebra.books.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class GraphQLController {

    private final BookRepository repository;

    @QueryMapping
    public List<Book> getAllBooks() {
        return repository.findAll();
    }

    @QueryMapping
    public Book getBookById(@Argument Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Knjiga nije pronađena: " + id));
    }

    @MutationMapping
    public Book createBook(
        @Argument String title,
        @Argument String description,
        @Argument Integer pageCount,
        @Argument String excerpt,
        @Argument String publishDate) {

        Book entity = Book.builder()
            .title(title)
            .description(description)
            .pageCount(pageCount != null ? pageCount : 0)
            .excerpt(excerpt)
            .publishDate(publishDate != null ? Instant.parse(publishDate) : null)
            .build();

        return repository.save(entity);
    }

    @MutationMapping
    public Book updateBook(
        @Argument Long id,
        @Argument String title,
        @Argument String description,
        @Argument Integer pageCount,
        @Argument String excerpt,
        @Argument String publishDate) {

        Book book = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Knjiga nije pronađena: " + id));
        book.setTitle(title);
        book.setDescription(description);
        book.setPageCount(pageCount != null ? pageCount : 0);
        book.setExcerpt(excerpt);
        book.setPublishDate(publishDate != null ? Instant.parse(publishDate) : null);
        return repository.save(book);
    }

    @MutationMapping
    public String deleteBook(@Argument Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Knjiga nije pronađena: " + id);
        }
        repository.deleteById(id);
        return "Knjiga " + id + " obrisana";
    }
}
