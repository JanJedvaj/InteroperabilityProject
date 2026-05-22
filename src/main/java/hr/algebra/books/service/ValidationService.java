package hr.algebra.books.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ValidationService {

    public List<String> validateXml(String xmlContent) {
        List<String> errors = new ArrayList<>();
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new ClassPathResource("xsd/book.xsd").getURL());
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new StringReader(xmlContent)));
        } catch (SAXException e) {
            errors.add("XML validacijska greška: " + e.getMessage());
        } catch (IOException e) {
            errors.add("Greška pri čitanju XSD sheme: " + e.getMessage());
        }
        return errors;
    }

    public List<String> validateJson(String jsonContent) {
        List<String> errors = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
            JsonSchema schema = factory.getSchema(
                new ClassPathResource("json/book-schema.json").getURL().toURI()
            );
            JsonNode jsonNode = mapper.readTree(jsonContent);
            Set<ValidationMessage> violations = schema.validate(jsonNode);
            for (ValidationMessage msg : violations) {
                errors.add("JSON validacijska greška: " + msg.getMessage());
            }
        } catch (Exception e) {
            errors.add("Greška pri JSON validaciji: " + e.getMessage());
        }
        return errors;
    }
}
