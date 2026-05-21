package hr.algebra.books.xmlvalidation.web;

import hr.algebra.books.xmlvalidation.dto.ValidationReport;
import hr.algebra.books.xmlvalidation.service.XmlValidationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/validation")
public class ValidationWebController {

    private final XmlValidationService validationService;

    public ValidationWebController(XmlValidationService validationService) {
        this.validationService = validationService;
    }

    @GetMapping
    public String form(Model model) {
        model.addAttribute("xmlInput", "");
        return "xmlvalidation/report";
    }

    @PostMapping
    public String validate(@RequestParam(defaultValue = "") String xmlInput, Model model) {
        ValidationReport report = validationService.validate(
                xmlInput.getBytes(StandardCharsets.UTF_8));
        model.addAttribute("report", report);
        model.addAttribute("xmlInput", xmlInput);
        return "xmlvalidation/report";
    }
}
