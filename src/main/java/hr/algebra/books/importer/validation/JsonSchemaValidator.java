package hr.algebra.books.importer.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import hr.algebra.books.importer.dto.ValidationError;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
public class JsonSchemaValidator {

    private final JsonSchema schema;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonSchemaValidator() throws Exception {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
        try (InputStream is = getClass().getResourceAsStream("/schema/json/book.schema.json")) {
            this.schema = factory.getSchema(is);
        }
    }

    public List<ValidationError> validate(byte[] json) {
        try {
            JsonNode node = objectMapper.readTree(json);
            return schema.validate(node).stream()
                    .map(ValidationMessage::getMessage)
                    .map(msg -> new ValidationError(-1, -1, "ERROR", msg))
                    .toList();
        } catch (Exception e) {
            return List.of(new ValidationError(-1, -1, "FATAL", "Invalid JSON: " + e.getMessage()));
        }
    }
}
