package hr.algebra.books.book.dto;

import hr.algebra.books.book.domain.Book;

public final class BookMapper {

    private BookMapper() {
    }

    public static BookDto toDto(Book book) {
        return new BookDto(
                book.getId(),
                book.getTitle(),
                book.getDescription(),
                book.getPageCount(),
                book.getExcerpt(),
                book.getPublishDate()
        );
    }

    public static Book toEntity(BookDto dto) {
        return new Book(
                dto.id(),
                dto.title(),
                dto.description(),
                dto.pageCount(),
                dto.excerpt(),
                dto.publishDate()
        );
    }
}
