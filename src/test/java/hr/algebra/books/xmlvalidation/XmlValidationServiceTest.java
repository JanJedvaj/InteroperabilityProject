package hr.algebra.books.xmlvalidation;

import hr.algebra.books.xmlvalidation.dto.ValidationReport;
import hr.algebra.books.xmlvalidation.service.XmlValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for XmlValidationService — no Spring context needed.
 * Validates against book.xsd (classpath:schema/xsd/book.xsd).
 */
class XmlValidationServiceTest {

    private XmlValidationService service;

    @BeforeEach
    void setUp() {
        service = new XmlValidationService();
    }

    @Test
    void validBook_reportsNoErrors() {
        String xml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <book>
                    <title>Clean Code</title>
                    <description>A handbook of agile software craftsmanship</description>
                    <pageCount>431</pageCount>
                    <excerpt>Chapter 1</excerpt>
                    <publishDate>2008-08-11T00:00:00Z</publishDate>
                </book>
                """;

        ValidationReport report = service.validate(xml.getBytes(StandardCharsets.UTF_8));

        assertThat(report.valid()).isTrue();
        assertThat(report.errors()).isEmpty();
    }

    @Test
    void missingTitle_reportsError() {
        String xml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <book>
                    <pageCount>100</pageCount>
                </book>
                """;

        ValidationReport report = service.validate(xml.getBytes(StandardCharsets.UTF_8));

        assertThat(report.valid()).isFalse();
        assertThat(report.errors()).isNotEmpty();
    }

    @Test
    void zeroPageCount_reportsError() {
        String xml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <book>
                    <title>Bad Book</title>
                    <pageCount>0</pageCount>
                </book>
                """;

        ValidationReport report = service.validate(xml.getBytes(StandardCharsets.UTF_8));

        assertThat(report.valid()).isFalse();
        assertThat(report.errors()).isNotEmpty();
    }

    @Test
    void emptyTitle_reportsError() {
        String xml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <book>
                    <title></title>
                    <pageCount>100</pageCount>
                </book>
                """;

        ValidationReport report = service.validate(xml.getBytes(StandardCharsets.UTF_8));

        assertThat(report.valid()).isFalse();
        assertThat(report.errors()).isNotEmpty();
    }

    @Test
    void malformedXml_reportsError() {
        String xml = "<book><title>Oops</title>";  // unclosed tags

        ValidationReport report = service.validate(xml.getBytes(StandardCharsets.UTF_8));

        assertThat(report.valid()).isFalse();
        assertThat(report.errors()).isNotEmpty();
    }

    @Test
    void emptyInput_reportsError() {
        ValidationReport report = service.validate(new byte[0]);

        assertThat(report.valid()).isFalse();
        assertThat(report.errors()).isNotEmpty();
    }

    @Test
    void validBookWithoutOptionalFields_reportsNoErrors() {
        // Only title and pageCount are required
        String xml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <book>
                    <title>Minimal Book</title>
                    <pageCount>1</pageCount>
                </book>
                """;

        ValidationReport report = service.validate(xml.getBytes(StandardCharsets.UTF_8));

        assertThat(report.valid()).isTrue();
        assertThat(report.errors()).isEmpty();
    }

    @Test
    void previewIsIncludedInReport() {
        String xml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <book>
                    <title>Preview Test</title>
                    <pageCount>10</pageCount>
                </book>
                """;

        ValidationReport report = service.validate(xml.getBytes(StandardCharsets.UTF_8));

        assertThat(report.xmlPreview()).contains("Preview Test");
    }
}
