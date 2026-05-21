package hr.algebra.books.weather.web;

import hr.algebra.books.grpc.client.DhmzClient;
import hr.algebra.books.grpc.parser.model.Grad;
import hr.algebra.books.grpc.parser.model.Hrvatska;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/weather")
public class WeatherWebController {

    private final DhmzClient dhmzClient;

    public WeatherWebController(DhmzClient dhmzClient) {
        this.dhmzClient = dhmzClient;
    }

    @GetMapping
    public String form(Model model) {
        model.addAttribute("cityName", "");
        model.addAttribute("results", List.of());
        return "weather/lookup";
    }

    @PostMapping
    public String lookup(@RequestParam(defaultValue = "") String cityName, Model model) {
        List<WeatherEntry> results = new ArrayList<>();
        String error = null;

        try {
            Hrvatska data = dhmzClient.fetchData();
            String lower = cityName.toLowerCase(Locale.forLanguageTag("hr-HR"));
            String measuredAt = data.getDatumTermin() != null ? data.getDatumTermin() : "";

            for (Grad grad : data.getGradovi()) {
                if (grad.getGradIme() == null) continue;
                if (!cityName.isBlank() &&
                        !grad.getGradIme().toLowerCase(Locale.forLanguageTag("hr-HR")).contains(lower)) continue;
                if (grad.getPodaci() == null || grad.getPodaci().getTemp() == null) continue;

                try {
                    double tempC = Double.parseDouble(grad.getPodaci().getTemp().trim());
                    results.add(new WeatherEntry(grad.getGradIme(), tempC, measuredAt));
                } catch (NumberFormatException ignored) {}
            }
        } catch (Exception e) {
            error = "Failed to fetch weather data: " + e.getMessage();
        }

        model.addAttribute("cityName", cityName);
        model.addAttribute("results", results);
        if (error != null) model.addAttribute("error", error);
        return "weather/lookup";
    }

    public record WeatherEntry(String city, double tempC, String measuredAt) {}
}
