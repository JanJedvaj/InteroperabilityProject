package hr.algebra.books.importer.api;

import hr.algebra.books.importer.dto.ImportResult;
import hr.algebra.books.importer.service.ImportOrchestrator;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
public class ImportController {

    private final ImportOrchestrator orchestrator;

    public ImportController(ImportOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    /**
     * Accepts a multipart upload of xmlFile + jsonFile.
     * Both are validated (XSD and JSON Schema); if both pass, both books are persisted.
     * Returns 200 with saved IDs on success, 422 with error details on validation failure.
     */
    @PreAuthorize("hasRole('FULL')")
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImportResult> importBooks(
            @RequestPart("xmlFile") MultipartFile xmlFile,
            @RequestPart("jsonFile") MultipartFile jsonFile) throws Exception {

        ImportResult result = orchestrator.importFiles(xmlFile.getBytes(), jsonFile.getBytes());

        if (result.hasErrors()) {
            return ResponseEntity.unprocessableEntity().body(result);
        }
        return ResponseEntity.ok(result);
    }
}
