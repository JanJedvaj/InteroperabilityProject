package hr.algebra.books.book.service;

import hr.algebra.books.book.dto.BookDto;
import hr.algebra.books.common.config.AppProperties;
import hr.algebra.books.common.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Instant;
import java.util.List;

/**
 * BookService adapter that delegates to the public FakeRESTAPI.
 * Used by BookServiceRouter when mode == PUBLIC.
 */
@Service
public class PublicBookService implements BookService {

    private final WebClient webClient;
    private final String baseUrl;

    public PublicBookService(WebClient.Builder webClientBuilder, AppProperties props) {
        this.baseUrl = props.api().publicBaseUrl() + "/Books";
        this.webClient = webClientBuilder.build();
    }

    @Override
    public List<BookDto> findAll() {
        return webClient.get().uri(baseUrl)
                .retrieve()
                .bodyToFlux(FakeBook.class)
                .map(FakeBook::toDto)
                .collectList()
                .block();
    }

    @Override
    public BookDto findById(Long id) {
        try {
            return webClient.get().uri(baseUrl + "/" + id)
                    .retrieve()
                    .bodyToMono(FakeBook.class)
                    .map(FakeBook::toDto)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            throw new NotFoundException("Book not found: " + id);
        }
    }

    @Override
    public BookDto create(BookDto dto) {
        return webClient.post().uri(baseUrl)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(FakeBook.class)
                .map(FakeBook::toDto)
                .block();
    }

    @Override
    public BookDto update(Long id, BookDto dto) {
        try {
            return webClient.put().uri(baseUrl + "/" + id)
                    .bodyValue(dto)
                    .retrieve()
                    .bodyToMono(FakeBook.class)
                    .map(FakeBook::toDto)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            throw new NotFoundException("Book not found: " + id);
        }
    }

    @Override
    public void delete(Long id) {
        try {
            webClient.delete().uri(baseUrl + "/" + id)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            throw new NotFoundException("Book not found: " + id);
        }
    }

    @Override
    public List<BookDto> search(String term) {
        String lower = term.toLowerCase();
        return findAll().stream()
                .filter(b -> (b.title() != null && b.title().toLowerCase().contains(lower))
                        || (b.description() != null && b.description().toLowerCase().contains(lower)))
                .toList();
    }

    // -------------------------------------------------------------------------
    // Local DTO matching FakeRESTAPI JSON shape.
    // publishDate arrives without timezone ("0001-01-01T00:00:00"), so we append Z.
    // -------------------------------------------------------------------------
    private record FakeBook(Long id, String title, String description,
                            int pageCount, String excerpt, String publishDate) {
        BookDto toDto() {
            Instant ts = null;
            if (publishDate != null && !publishDate.isBlank()) {
                try {
                    String iso = publishDate.endsWith("Z") ? publishDate : publishDate + "Z";
                    ts = Instant.parse(iso);
                } catch (Exception ignored) {}
            }
            return new BookDto(id, title, description, pageCount, excerpt, ts);
        }
    }
}
