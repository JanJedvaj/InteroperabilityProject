package hr.algebra.books.home;

import hr.algebra.books.book.service.BookService;
import hr.algebra.books.book.service.BookServiceRouter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeWebController {

    private final BookService bookService;
    private final BookServiceRouter router;

    public HomeWebController(BookService bookService, BookServiceRouter router) {
        this.bookService = bookService;
        this.router = router;
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("currentMode", router.currentMode().name());
        model.addAttribute("bookCount", bookService.findAll().size());
        return "home/index";
    }
}
