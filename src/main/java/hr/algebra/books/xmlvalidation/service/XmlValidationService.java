package hr.algebra.books.xmlvalidation.service;

import hr.algebra.books.xmlvalidation.dto.ValidationReport;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@Service
public class XmlValidationService {

    /**
     * Validates the given XML bytes against book.xsd.
     * Returns a full report including all errors found (not just the first).
     */
    public ValidationReport validate(byte[] xml) {
        CollectingErrorHandler handler = new CollectingErrorHandler();
        try {
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = sf.newSchema(new ClassPathResource("schema/xsd/book.xsd").getURL());
            Validator validator = schema.newValidator();
            validator.setErrorHandler(handler);
            validator.validate(new StreamSource(new ByteArrayInputStream(xml)));
        } catch (SAXException e) {
            // Fatal parse error — already captured by the handler
        } catch (Exception e) {
            handler.getErrors().add(
                new hr.algebra.books.xmlvalidation.dto.ValidationError(-1, -1, "FATAL", e.getMessage())
            );
        }

        String preview = new String(xml, StandardCharsets.UTF_8);
        if (preview.length() > 600) {
            preview = preview.substring(0, 600) + "\n... (truncated)";
        }

        return new ValidationReport(handler.isValid(), handler.getErrors(), preview);
    }
}
