package hr.algebra.books.book.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.Instant;

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(length = 4000)
    private String description;

    @Column(name = "page_count", nullable = false)
    private int pageCount;

    @Column(length = 4000)
    private String excerpt;

    @Column(name = "publish_date")
    private Instant publishDate;

    protected Book() {
    }

    public Book(Long id, String title, String description, int pageCount, String excerpt, Instant publishDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.pageCount = pageCount;
        this.excerpt = excerpt;
        this.publishDate = publishDate;
    }

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

    public Instant getPublishDate() { return publishDate; }
    public void setPublishDate(Instant publishDate) { this.publishDate = publishDate; }
}
