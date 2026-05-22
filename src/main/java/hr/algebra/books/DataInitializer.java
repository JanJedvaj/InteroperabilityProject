package hr.algebra.books;

import hr.algebra.books.model.Book;
import hr.algebra.books.model.User;
import hr.algebra.books.repository.BookRepository;
import hr.algebra.books.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            userRepository.save(User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .role("FULL_ACCESS")
                    .build());

            userRepository.save(User.builder()
                    .username("readonly")
                    .password(passwordEncoder.encode("readonly123"))
                    .role("READ_ONLY")
                    .build());

            System.out.println("Inicijalni korisnici kreirani: admin / readonly");
        }

        if (bookRepository.count() == 0) {
            bookRepository.save(Book.builder()
                    .title("Clean Code")
                    .description("A Handbook of Agile Software Craftsmanship")
                    .pageCount(464)
                    .excerpt("Even bad code can function. But if code isn't clean, it can bring a development organization to its knees.")
                    .publishDate(Instant.parse("2008-08-01T00:00:00Z"))
                    .build());

            bookRepository.save(Book.builder()
                    .title("Code Complete")
                    .description("A Practical Handbook of Software Construction")
                    .pageCount(960)
                    .excerpt("Widely considered one of the best practical guides to programming.")
                    .publishDate(Instant.parse("2004-06-09T00:00:00Z"))
                    .build());

            bookRepository.save(Book.builder()
                    .title("The Pragmatic Programmer")
                    .description("Your Journey to Mastery")
                    .pageCount(352)
                    .excerpt("Straight from the trenches, The Pragmatic Programmer cuts through the increasing specialization.")
                    .publishDate(Instant.parse("2019-09-23T00:00:00Z"))
                    .build());

            System.out.println("Inicijalne knjige kreirane: 3 knjige");
        }
    }
}
