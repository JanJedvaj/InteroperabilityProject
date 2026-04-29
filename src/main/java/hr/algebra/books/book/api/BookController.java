package hr.algebra.books.book.api;

import hr.algebra.books.book.dto.BookDto;
import hr.algebra.books.book.service.BookService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<BookDto> list(@RequestParam(value = "term", required = false) String term) {
        if (term == null || term.isBlank()) {
            return bookService.findAll();
        }
        return bookService.search(term);
    }

    @GetMapping("/{id}")
    public BookDto get(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @PostMapping
    public ResponseEntity<BookDto> create(@Valid @RequestBody BookDto dto) {
        BookDto created = bookService.create(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public BookDto update(@PathVariable Long id, @Valid @RequestBody BookDto dto) {
        return bookService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        bookService.delete(id);
    }
}
