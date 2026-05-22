package hr.algebra.books.service;

import hr.algebra.books.dto.BookDTO;
import hr.algebra.books.model.Book;
import hr.algebra.books.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository repository;

    @Value("${api.mode}")
    private String apiMode;

    @Value("${api.public-base-url}")
    private String publicBaseUrl;

    private boolean isCustomMode() {
        return "custom".equalsIgnoreCase(apiMode);
    }

    public List<Book> getAll() {
        if (isCustomMode()) {
            return repository.findAll();
        } else {
            return getAllPublic();
        }
    }

    @SuppressWarnings("unchecked")
    private List<Book> getAllPublic() {
        RestTemplate restTemplate = new RestTemplate();
        String url = publicBaseUrl + "/Books";
        ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
        List<Map<String, Object>> items = response.getBody();
        if (items == null) return List.of();
        return items.stream().map(this::mapPublicBook).toList();
    }

    public Book getById(Long id) {
        if (isCustomMode()) {
            return repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Knjiga nije pronađena: " + id));
        } else {
            return getByIdPublic(id);
        }
    }

    @SuppressWarnings("unchecked")
    private Book getByIdPublic(Long id) {
        RestTemplate restTemplate = new RestTemplate();
        String url = publicBaseUrl + "/Books/" + id;
        Map<String, Object> item = restTemplate.getForObject(url, Map.class);
        if (item == null) throw new RuntimeException("Knjiga nije pronađena: " + id);
        return mapPublicBook(item);
    }

    public Book create(BookDTO dto) {
        if (isCustomMode()) {
            return createCustom(dto);
        } else {
            return createPublic(dto);
        }
    }

    private Book createCustom(BookDTO dto) {
        Book entity = Book.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .pageCount(dto.getPageCount() != null ? dto.getPageCount() : 0)
                .excerpt(dto.getExcerpt())
                .publishDate(dto.getPublishDate() != null ? Instant.parse(dto.getPublishDate()) : null)
                .build();
        return repository.save(entity);
    }

    @SuppressWarnings("unchecked")
    private Book createPublic(BookDTO dto) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("id", 0);
        body.put("title", dto.getTitle());
        body.put("description", dto.getDescription());
        body.put("pageCount", dto.getPageCount() != null ? dto.getPageCount() : 0);
        body.put("excerpt", dto.getExcerpt());
        body.put("publishDate", dto.getPublishDate());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        String url = publicBaseUrl + "/Books";
        Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);
        return response != null ? mapPublicBook(response) : Book.builder().title(dto.getTitle()).build();
    }

    public Book update(Long id, BookDTO dto) {
        if (isCustomMode()) {
            return updateCustom(id, dto);
        } else {
            return updatePublic(id, dto);
        }
    }

    private Book updateCustom(Long id, BookDTO dto) {
        Book book = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Knjiga nije pronađena: " + id));
        book.setTitle(dto.getTitle());
        book.setDescription(dto.getDescription());
        book.setPageCount(dto.getPageCount() != null ? dto.getPageCount() : 0);
        book.setExcerpt(dto.getExcerpt());
        book.setPublishDate(dto.getPublishDate() != null ? Instant.parse(dto.getPublishDate()) : null);
        return repository.save(book);
    }

    @SuppressWarnings("unchecked")
    private Book updatePublic(Long id, BookDTO dto) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("id", id);
        body.put("title", dto.getTitle());
        body.put("description", dto.getDescription());
        body.put("pageCount", dto.getPageCount() != null ? dto.getPageCount() : 0);
        body.put("excerpt", dto.getExcerpt());
        body.put("publishDate", dto.getPublishDate());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        String url = publicBaseUrl + "/Books/" + id;
        restTemplate.put(url, entity);

        return Book.builder()
            .id(id)
            .title(dto.getTitle())
            .description(dto.getDescription())
            .pageCount(dto.getPageCount() != null ? dto.getPageCount() : 0)
            .excerpt(dto.getExcerpt())
            .build();
    }

    public void delete(Long id) {
        if (isCustomMode()) {
            if (!repository.existsById(id)) {
                throw new RuntimeException("Knjiga nije pronađena: " + id);
            }
            repository.deleteById(id);
        } else {
            RestTemplate restTemplate = new RestTemplate();
            String url = publicBaseUrl + "/Books/" + id;
            restTemplate.delete(url);
        }
    }

    private Book mapPublicBook(Map<String, Object> item) {
        return Book.builder()
            .id(item.get("id") != null ? Long.valueOf(item.get("id").toString()) : null)
            .title((String) item.get("title"))
            .description((String) item.get("description"))
            .pageCount(item.get("pageCount") != null ? Integer.parseInt(item.get("pageCount").toString()) : 0)
            .excerpt((String) item.get("excerpt"))
            .publishDate(item.get("publishDate") != null ? Instant.parse(item.get("publishDate").toString()) : null)
            .build();
    }
}
