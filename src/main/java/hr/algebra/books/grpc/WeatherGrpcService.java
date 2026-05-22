package hr.algebra.books.grpc;

import hr.algebra.books.grpc.generated.CityRequest;
import hr.algebra.books.grpc.generated.CityTemperature;
import hr.algebra.books.grpc.generated.TemperatureResponse;
import hr.algebra.books.grpc.generated.WeatherServiceGrpc;
import hr.algebra.books.service.DhmzService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WeatherGrpcService extends WeatherServiceGrpc.WeatherServiceImplBase {

    private final DhmzService dhmzService;

    @Override
    public void getTemperature(CityRequest request,
                               StreamObserver<TemperatureResponse> responseObserver) {
        TemperatureResponse.Builder responseBuilder = TemperatureResponse.newBuilder();
        try {
            String cityName = request.getCityName();
            if (cityName == null || cityName.trim().isEmpty()) {
                responseBuilder.setMessage("Naziv grada ne smije biti prazan.");
                responseObserver.onNext(responseBuilder.build());
                responseObserver.onCompleted();
                return;
            }
            List<String[]> results = dhmzService.getTemperatureByCity(cityName);
            if (results.isEmpty()) {
                responseBuilder.setMessage("Nisu pronađeni podaci za grad: " + cityName);
            } else {
                responseBuilder.setMessage("Pronađeno " + results.size() + " rezultat(a).");
                for (String[] city : results) {
                    CityTemperature cityTemp = CityTemperature.newBuilder()
                            .setCity(city[0])
                            .setTemperature(city[1])
                            .setDescription(city[2])
                            .build();
                    responseBuilder.addCities(cityTemp);
                }
            }
        } catch (Exception e) {
            responseBuilder.setMessage("Greška: " + e.getMessage());
        }
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }
}
