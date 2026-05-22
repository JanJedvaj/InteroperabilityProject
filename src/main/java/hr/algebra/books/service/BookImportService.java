package hr.algebra.books.service;

import hr.algebra.books.dto.BookDTO;
import hr.algebra.books.model.Book;
import hr.algebra.books.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookImportService {

    private final BookRepository repository;

    public Book save(BookDTO dto) {
        Book entity = Book.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .pageCount(dto.getPageCount() != null ? dto.getPageCount() : 0)
                .excerpt(dto.getExcerpt())
                .publishDate(dto.getPublishDate() != null ? Instant.parse(dto.getPublishDate()) : null)
                .build();
        return repository.save(entity);
    }

    public List<Book> getAll() {
        return repository.findAll();
    }

    public Book getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Knjiga nije pronađena: " + id));
    }
}
