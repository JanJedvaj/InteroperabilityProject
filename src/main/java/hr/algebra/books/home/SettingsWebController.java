package hr.algebra.books.home;

import hr.algebra.books.book.service.ApiMode;
import hr.algebra.books.book.service.BookServiceRouter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SettingsWebController {

    private final BookServiceRouter router;

    public SettingsWebController(BookServiceRouter router) {
        this.router = router;
    }

    @GetMapping("/settings")
    public String settingsPage(Model model) {
        model.addAttribute("currentMode", router.currentMode().name());
        return "home/settings";
    }

    @PreAuthorize("hasRole('FULL')")
    @PostMapping("/settings")
    public String applySettings(@RequestParam String mode,
                                 RedirectAttributes redirectAttributes) {
        try {
            router.switchTo(ApiMode.valueOf(mode.toUpperCase()));
            redirectAttributes.addFlashAttribute("success", "Switched to " + mode + " mode.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Unknown mode: " + mode);
        }
        return "redirect:/settings";
    }
}
