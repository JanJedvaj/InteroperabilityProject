package hr.algebra.books.book.service;

import hr.algebra.books.book.dto.BookDto;
import java.util.List;

public interface BookService {

    List<BookDto> findAll();

    BookDto findById(Long id);

    BookDto create(BookDto dto);

    BookDto update(Long id, BookDto dto);

    void delete(Long id);

    List<BookDto> search(String term);
}
