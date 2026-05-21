package hr.algebra.books.xmlvalidation.api;

import hr.algebra.books.xmlvalidation.dto.ValidationReport;
import hr.algebra.books.xmlvalidation.service.XmlValidationService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ValidationController {

    private final XmlValidationService validationService;

    public ValidationController(XmlValidationService validationService) {
        this.validationService = validationService;
    }

    /**
     * Validates an XML document against book.xsd.
     * POST body must be a well-formed XML document (Content-Type: application/xml).
     * Returns a JSON report with valid flag, error list (line/col/severity/message), and XML preview.
     */
    @PostMapping(value = "/validate-xml",
                 consumes = MediaType.APPLICATION_XML_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ValidationReport validate(@RequestBody byte[] xml) {
        return validationService.validate(xml);
    }
}
