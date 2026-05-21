package hr.algebra.books.importer.web;

import hr.algebra.books.importer.dto.ImportResult;
import hr.algebra.books.importer.service.ImportOrchestrator;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/import")
public class ImportWebController {

    private final ImportOrchestrator orchestrator;

    public ImportWebController(ImportOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @GetMapping
    public String importPage() {
        return "importer/import";
    }

    @PreAuthorize("hasRole('FULL')")
    @PostMapping
    public String doImport(@RequestPart("xmlFile") MultipartFile xmlFile,
                           @RequestPart("jsonFile") MultipartFile jsonFile,
                           Model model) {
        try {
            ImportResult result = orchestrator.importFiles(
                    xmlFile.getBytes(),
                    jsonFile.getBytes()
            );
            model.addAttribute("result", result);
        } catch (Exception e) {
            model.addAttribute("error", "Upload failed: " + e.getMessage());
        }
        return "importer/import";
    }
}
