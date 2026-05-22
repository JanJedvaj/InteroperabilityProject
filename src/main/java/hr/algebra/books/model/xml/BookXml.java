package hr.algebra.books.model.xml;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "book", propOrder = {
    "id", "title", "description", "pageCount", "excerpt", "publishDate"
})
public class BookXml {
    @XmlElement(required = true)
    private Long id;

    @XmlElement(required = true)
    private String title;

    @XmlElement
    private String description;

    @XmlElement(required = true)
    private int pageCount;

    @XmlElement
    private String excerpt;

    @XmlElement
    private String publishDate;
}
