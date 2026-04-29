package hr.algebra.books.book.repository;

import hr.algebra.books.book.domain.Book;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);
}
