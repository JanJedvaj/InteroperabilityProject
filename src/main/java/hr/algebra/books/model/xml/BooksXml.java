package hr.algebra.books.model.xml;

import jakarta.xml.bind.annotation.*;
import lombok.Data;
import java.util.List;

@Data
@XmlRootElement(name = "books")
@XmlAccessorType(XmlAccessType.FIELD)
public class BooksXml {
    @XmlElement(name = "book")
    private List<BookXml> books;
}
