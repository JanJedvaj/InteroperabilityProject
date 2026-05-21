package hr.algebra.books.graphql.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Controller
@RequestMapping("/graphql-ui")
public class GraphQLWebController {

    private static final String SAMPLE_QUERY = "{\n  books {\n    id\n    title\n    pageCount\n  }\n}";

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public GraphQLWebController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8181").build();
    }

    @GetMapping
    public String form(Model model) {
        model.addAttribute("query", SAMPLE_QUERY);
        model.addAttribute("result", "");
        return "graphql/playground";
    }

    @PostMapping
    public String execute(@RequestParam String query, Model model) {
        String result;
        try {
            // POST to /graphql without auth (read-only queries work without token in playground)
            Map<String, String> body = Map.of("query", query);
            String raw = webClient.post()
                    .uri("/graphql")
                    .header("Content-Type", "application/json")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            // Pretty-print the JSON response
            Object parsed = objectMapper.readValue(raw, Object.class);
            result = objectMapper.writeValueAsString(parsed);
        } catch (Exception e) {
            result = "Error: " + e.getMessage();
        }
        model.addAttribute("query", query);
        model.addAttribute("result", result);
        return "graphql/playground";
    }
}
