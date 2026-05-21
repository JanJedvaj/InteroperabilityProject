package hr.algebra.books.importer.service;

import hr.algebra.books.book.dto.BookDto;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.time.Instant;

@Service
public class XmlImportService {

    /**
     * JAXB-unmarshals a validated book XML document into a BookDto.
     * Assumes the XML has already been validated against book.xsd.
     */
    public BookDto parse(byte[] xml) throws Exception {
        JAXBContext ctx = JAXBContext.newInstance(XmlBook.class);
        XmlBook xb = (XmlBook) ctx.createUnmarshaller().unmarshal(new ByteArrayInputStream(xml));
        return xb.toDto();
    }

    @XmlRootElement(name = "book")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class XmlBook {
        private Long id;
        private String title;
        private String description;
        private int pageCount;
        private String excerpt;
        private String publishDate;

        public BookDto toDto() {
            Instant ts = null;
            if (publishDate != null && !publishDate.isBlank()) {
                try { ts = Instant.parse(publishDate); } catch (Exception ignored) {}
            }
            return new BookDto(null, title, description, pageCount, excerpt, ts);
        }
    }
}
