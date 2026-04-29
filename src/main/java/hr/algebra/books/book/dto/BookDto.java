package hr.algebra.books.book.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;

public record BookDto(
        Long id,
        @NotBlank String title,
        String description,
        @Min(1) int pageCount,
        String excerpt,
        Instant publishDate
) {
}
