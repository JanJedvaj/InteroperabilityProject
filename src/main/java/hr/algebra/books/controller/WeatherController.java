package hr.algebra.books.controller;

import hr.algebra.books.grpc.generated.CityRequest;
import hr.algebra.books.grpc.generated.TemperatureResponse;
import hr.algebra.books.grpc.generated.WeatherServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    @GetMapping("/{cityName}")
    public ResponseEntity<?> getTemperature(@PathVariable String cityName) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 9091)
                .usePlaintext()
                .build();

        try {
            WeatherServiceGrpc.WeatherServiceBlockingStub stub =
                    WeatherServiceGrpc.newBlockingStub(channel);

            CityRequest request = CityRequest.newBuilder()
                    .setCityName(cityName)
                    .build();

            TemperatureResponse response = stub.getTemperature(request);

            if (response.getCitiesList().isEmpty()) {
                return ResponseEntity.ok(Map.of("message", response.getMessage()));
            }

            List<Map<String, String>> cities = response.getCitiesList()
                    .stream()
                    .map(city -> Map.of(
                            "city", city.getCity(),
                            "temperature", city.getTemperature(),
                            "description", city.getDescription()
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "message", response.getMessage(),
                    "results", cities
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        } finally {
            channel.shutdown();
        }
    }
}
