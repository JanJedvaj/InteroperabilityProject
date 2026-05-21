package hr.algebra.books.importer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.algebra.books.book.dto.BookDto;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JsonImportService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Jackson-deserializes a validated book JSON document into a BookDto.
     * Assumes the JSON has already been validated against book.schema.json.
     */
    public BookDto parse(byte[] json) throws Exception {
        JsonBook jb = objectMapper.readValue(json, JsonBook.class);
        return jb.toDto();
    }

    // Local class — publishDate is String to tolerate both "2024-01-15T00:00:00Z" and null
    private static class JsonBook {
        public Long id;
        public String title;
        public String description;
        public int pageCount;
        public String excerpt;
        public String publishDate;

        BookDto toDto() {
            Instant ts = null;
            if (publishDate != null && !publishDate.isBlank()) {
                try {
                    String iso = publishDate.endsWith("Z") ? publishDate : publishDate + "Z";
                    ts = Instant.parse(iso);
                } catch (Exception ignored) {}
            }
            return new BookDto(null, title, description, pageCount, excerpt, ts);
        }
    }
}
