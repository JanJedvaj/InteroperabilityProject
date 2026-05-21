package hr.algebra.books.importer.service;

import hr.algebra.books.book.dto.BookDto;
import hr.algebra.books.book.service.BookService;
import hr.algebra.books.importer.dto.ImportResult;
import hr.algebra.books.importer.dto.ValidationError;
import hr.algebra.books.importer.validation.JsonSchemaValidator;
import hr.algebra.books.importer.validation.XsdValidator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ImportOrchestrator {

    private final XsdValidator xsdValidator;
    private final JsonSchemaValidator jsonSchemaValidator;
    private final XmlImportService xmlImportService;
    private final JsonImportService jsonImportService;
    private final BookService bookService;

    public ImportOrchestrator(XsdValidator xsdValidator,
                               JsonSchemaValidator jsonSchemaValidator,
                               XmlImportService xmlImportService,
                               JsonImportService jsonImportService,
                               BookService bookService) {
        this.xsdValidator = xsdValidator;
        this.jsonSchemaValidator = jsonSchemaValidator;
        this.xmlImportService = xmlImportService;
        this.jsonImportService = jsonImportService;
        this.bookService = bookService;
    }

    /**
     * Validates both files first; persists only if both pass.
     * Returns saved IDs on success or the full error lists on failure.
     */
    public ImportResult importFiles(byte[] xmlBytes, byte[] jsonBytes) {
        List<ValidationError> xmlErrors = xsdValidator.validate(xmlBytes);
        List<ValidationError> jsonErrors = jsonSchemaValidator.validate(jsonBytes);

        if (!xmlErrors.isEmpty() || !jsonErrors.isEmpty()) {
            return new ImportResult(List.of(), xmlErrors, jsonErrors);
        }

        List<Long> savedIds = new ArrayList<>();
        try {
            BookDto xmlBook = xmlImportService.parse(xmlBytes);
            BookDto jsonBook = jsonImportService.parse(jsonBytes);
            savedIds.add(bookService.create(xmlBook).id());
            savedIds.add(bookService.create(jsonBook).id());
        } catch (Exception e) {
            return new ImportResult(List.of(),
                    List.of(new ValidationError(-1, -1, "FATAL", "Import failed: " + e.getMessage())),
                    List.of());
        }

        return new ImportResult(savedIds, List.of(), List.of());
    }
}
