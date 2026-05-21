package hr.algebra.books.importer.validation;

import hr.algebra.books.importer.dto.ValidationError;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Validates an XML document against book.xsd (single source of truth in schema/xsd/).
 */
@Component
public class XsdValidator {

    public List<ValidationError> validate(byte[] xml) {
        CollectingErrorHandler handler = new CollectingErrorHandler();
        try {
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = sf.newSchema(new ClassPathResource("schema/xsd/book.xsd").getURL());
            Validator validator = schema.newValidator();
            validator.setErrorHandler(handler);
            validator.validate(new StreamSource(new ByteArrayInputStream(xml)));
        } catch (SAXException e) {
            // fatal — already captured
        } catch (Exception e) {
            handler.getErrors().add(new ValidationError(-1, -1, "FATAL", e.getMessage()));
        }
        return handler.getErrors();
    }
}
