package hr.algebra.books.book.api;

import hr.algebra.books.book.service.ApiMode;
import hr.algebra.books.book.service.BookServiceRouter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/mode")
public class ApiModeController {

    private final BookServiceRouter router;

    public ApiModeController(BookServiceRouter router) {
        this.router = router;
    }

    @GetMapping
    public ResponseEntity<Map<String, String>> getMode() {
        return ResponseEntity.ok(Map.of("mode", router.currentMode().name()));
    }

    @PreAuthorize("hasRole('FULL')")
    @PutMapping
    public ResponseEntity<Map<String, String>> switchMode(@RequestParam String mode) {
        router.switchTo(ApiMode.valueOf(mode.toUpperCase()));
        return ResponseEntity.ok(Map.of("mode", router.currentMode().name()));
    }
}
