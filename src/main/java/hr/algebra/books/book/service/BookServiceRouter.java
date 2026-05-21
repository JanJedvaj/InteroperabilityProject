package hr.algebra.books.book.service;

import hr.algebra.books.book.dto.BookDto;
import hr.algebra.books.common.config.AppProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Runtime switch between the local DB (CustomBookService) and FakeRESTAPI (PublicBookService).
 * Marked @Primary so every injection point that asks for BookService gets this router.
 * Switching happens via switchTo(ApiMode) with no restart required — uses AtomicReference.
 */
@Primary
@Component
public class BookServiceRouter implements BookService {

    private final CustomBookService custom;
    private final PublicBookService pub;
    private final AtomicReference<BookService> current = new AtomicReference<>();

    public BookServiceRouter(CustomBookService custom, PublicBookService pub, AppProperties props) {
        this.custom = custom;
        this.pub = pub;
        ApiMode initial = parseMode(props.api().mode());
        this.current.set(initial == ApiMode.PUBLIC ? pub : custom);
    }

    public void switchTo(ApiMode mode) {
        current.set(mode == ApiMode.PUBLIC ? pub : custom);
    }

    public ApiMode currentMode() {
        return current.get() == pub ? ApiMode.PUBLIC : ApiMode.CUSTOM;
    }

    @Override public List<BookDto> findAll()               { return current.get().findAll(); }
    @Override public BookDto findById(Long id)             { return current.get().findById(id); }
    @Override public BookDto create(BookDto dto)           { return current.get().create(dto); }
    @Override public BookDto update(Long id, BookDto dto)  { return current.get().update(id, dto); }
    @Override public void delete(Long id)                  { current.get().delete(id); }
    @Override public List<BookDto> search(String term)     { return current.get().search(term); }

    private ApiMode parseMode(String mode) {
        try { return ApiMode.valueOf(mode.toUpperCase()); } catch (Exception e) { return ApiMode.CUSTOM; }
    }
}
