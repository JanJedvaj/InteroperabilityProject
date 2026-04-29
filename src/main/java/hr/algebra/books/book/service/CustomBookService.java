package hr.algebra.books.book.service;

import hr.algebra.books.book.domain.Book;
import hr.algebra.books.book.dto.BookDto;
import hr.algebra.books.book.dto.BookMapper;
import hr.algebra.books.book.repository.BookRepository;
import hr.algebra.books.common.exception.NotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomBookService implements BookService {

    private final BookRepository repository;

    public CustomBookService(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDto> findAll() {
        return repository.findAll().stream().map(BookMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BookDto findById(Long id) {
        return repository.findById(id)
                .map(BookMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Book not found: " + id));
    }

    @Override
    public BookDto create(BookDto dto) {
        Book entity = new Book(
                null,
                dto.title(),
                dto.description(),
                dto.pageCount(),
                dto.excerpt(),
                dto.publishDate()
        );
        return BookMapper.toDto(repository.save(entity));
    }

    @Override
    public BookDto update(Long id, BookDto dto) {
        Book existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found: " + id));
        existing.setTitle(dto.title());
        existing.setDescription(dto.description());
        existing.setPageCount(dto.pageCount());
        existing.setExcerpt(dto.excerpt());
        existing.setPublishDate(dto.publishDate());
        return BookMapper.toDto(repository.save(existing));
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Book not found: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDto> search(String term) {
        return repository
                .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(term, term)
                .stream()
                .map(BookMapper::toDto)
                .toList();
    }
}
