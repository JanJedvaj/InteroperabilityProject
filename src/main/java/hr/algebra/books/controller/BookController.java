package hr.algebra.books.controller;

import hr.algebra.books.dto.BookDTO;
import hr.algebra.books.model.Book;
import hr.algebra.books.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @Value("${api.mode}")
    private String apiMode;

    @GetMapping("/books")
    public ResponseEntity<List<Book>> getAll() {
        return ResponseEntity.ok(bookService.getAll());
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(bookService.getById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/books")
    public ResponseEntity<Book> create(@RequestBody BookDTO dto) {
        return ResponseEntity.ok(bookService.create(dto));
    }

    @PutMapping("/books/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody BookDTO dto) {
        try {
            return ResponseEntity.ok(bookService.update(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        try {
            bookService.delete(id);
            return ResponseEntity.ok(Map.of("message", "Knjiga obrisana"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/mode")
    public ResponseEntity<Map<String, String>> getMode() {
        return ResponseEntity.ok(Map.of("mode", apiMode));
    }
}
