package hr.algebra.books.common.seed;

import hr.algebra.books.book.domain.Book;
import hr.algebra.books.book.repository.BookRepository;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSeeder.class);

    private final BookRepository bookRepository;

    public DatabaseSeeder(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public void run(String... args) {
        if (bookRepository.count() > 0) {
            log.info("Books already present ({}), skipping seed.", bookRepository.count());
            return;
        }

        bookRepository.saveAll(List.of(
                new Book(
                        null,
                        "Lorem ipsum dolor sit",
                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt.",
                        100,
                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit...",
                        Instant.parse("2024-01-15T00:00:00Z")
                ),
                new Book(
                        null,
                        "Consectetur adipiscing elit",
                        "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                        250,
                        "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua...",
                        Instant.parse("2024-03-22T00:00:00Z")
                ),
                new Book(
                        null,
                        "Ut enim ad minim veniam",
                        "Quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
                        384,
                        "Quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo...",
                        Instant.parse("2024-07-09T00:00:00Z")
                )
        ));

        log.info("Seeded {} books.", bookRepository.count());
    }
}
