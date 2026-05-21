package hr.algebra.books.book.web;

import hr.algebra.books.book.dto.BookDto;
import hr.algebra.books.book.service.BookService;
import hr.algebra.books.book.service.BookServiceRouter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/books")
public class BookWebController {

    private final BookService bookService;
    private final BookServiceRouter router;

    public BookWebController(BookService bookService, BookServiceRouter router) {
        this.bookService = bookService;
        this.router = router;
    }

    // ─── List / Search ───────────────────────────────────────────────────────

    @GetMapping
    public String list(@RequestParam(required = false) String term, Model model) {
        List<BookDto> books;
        if (term != null && !term.isBlank()) {
            books = bookService.search(term);
        } else {
            books = bookService.findAll();
        }
        model.addAttribute("books", books);
        model.addAttribute("term", term != null ? term : "");
        model.addAttribute("currentMode", router.currentMode().name());
        return "book/list";
    }

    // ─── Detail ───────────────────────────────────────────────────────────────

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("book", bookService.findById(id));
        return "book/detail";
    }

    // ─── Create ───────────────────────────────────────────────────────────────

    @PreAuthorize("hasRole('FULL')")
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("bookForm", new BookForm());
        model.addAttribute("editMode", false);
        return "book/form";
    }

    @PreAuthorize("hasRole('FULL')")
    @PostMapping
    public String create(@Valid @ModelAttribute BookForm bookForm,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("editMode", false);
            return "book/form";
        }
        BookDto created = bookService.create(bookForm.toDto());
        redirectAttributes.addFlashAttribute("success",
                "Book \"" + created.title() + "\" created successfully.");
        return "redirect:/books";
    }

    // ─── Edit ─────────────────────────────────────────────────────────────────

    @PreAuthorize("hasRole('FULL')")
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        BookDto dto = bookService.findById(id);
        model.addAttribute("bookForm", BookForm.from(dto));
        model.addAttribute("editMode", true);
        return "book/form";
    }

    @PreAuthorize("hasRole('FULL')")
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute BookForm bookForm,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("editMode", true);
            return "book/form";
        }
        BookDto updated = bookService.update(id, bookForm.toDto());
        redirectAttributes.addFlashAttribute("success",
                "Book \"" + updated.title() + "\" updated successfully.");
        return "redirect:/books";
    }

    // ─── Delete ───────────────────────────────────────────────────────────────

    @PreAuthorize("hasRole('FULL')")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        bookService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Book deleted.");
        return "redirect:/books";
    }

    // ─── BookForm ─────────────────────────────────────────────────────────────

    /**
     * Form-backing POJO for the create/edit form.
     * publishDate is held as a string (datetime-local format) for HTML input binding,
     * then converted to Instant when producing the DTO.
     */
    public static class BookForm {

        private Long id;

        @NotBlank(message = "Title is required")
        private String title;

        private String description;

        @Min(value = 1, message = "Page count must be at least 1")
        private int pageCount = 1;

        private String excerpt;

        /** HTML datetime-local format: yyyy-MM-dd'T'HH:mm */
        private String publishDate;

        public BookForm() {}

        /** Factory — populate from an existing DTO for the edit form. */
        public static BookForm from(BookDto dto) {
            BookForm f = new BookForm();
            f.id = dto.id();
            f.title = dto.title();
            f.description = dto.description();
            f.pageCount = dto.pageCount();
            f.excerpt = dto.excerpt();
            if (dto.publishDate() != null) {
                // Format as datetime-local (no seconds / timezone suffix)
                f.publishDate = LocalDateTime.ofInstant(dto.publishDate(), ZoneOffset.UTC)
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
            }
            return f;
        }

        /** Convert form data back to a DTO (id is nullable for creates). */
        public BookDto toDto() {
            Instant instant = null;
            if (publishDate != null && !publishDate.isBlank()) {
                try {
                    instant = LocalDateTime.parse(publishDate,
                            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))
                            .toInstant(ZoneOffset.UTC);
                } catch (Exception ignored) {
                    // leave null if unparseable
                }
            }
            return new BookDto(id, title, description, pageCount, excerpt, instant);
        }

        // ── Getters / Setters (required by Thymeleaf binding) ─────────────

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public int getPageCount() { return pageCount; }
        public void setPageCount(int pageCount) { this.pageCount = pageCount; }

        public String getExcerpt() { return excerpt; }
        public void setExcerpt(String excerpt) { this.excerpt = excerpt; }

        public String getPublishDate() { return publishDate; }
        public void setPublishDate(String publishDate) { this.publishDate = publishDate; }
    }
}
