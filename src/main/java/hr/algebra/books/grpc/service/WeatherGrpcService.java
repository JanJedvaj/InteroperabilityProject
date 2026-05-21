package hr.algebra.books.grpc.service;

import hr.algebra.books.grpc.api.CityRequest;
import hr.algebra.books.grpc.api.CityTemperature;
import hr.algebra.books.grpc.api.WeatherServiceGrpc;
import hr.algebra.books.grpc.client.DhmzClient;
import hr.algebra.books.grpc.parser.model.Grad;
import hr.algebra.books.grpc.parser.model.Hrvatska;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.Locale;

/**
 * gRPC service that streams Croatian city temperatures from the DHMZ live feed.
 * Filters cities by case-insensitive partial name match (Croatian locale aware).
 */
@GrpcService
public class WeatherGrpcService extends WeatherServiceGrpc.WeatherServiceImplBase {

    private final DhmzClient dhmzClient;

    public WeatherGrpcService(DhmzClient dhmzClient) {
        this.dhmzClient = dhmzClient;
    }

    @Override
    public void findTemperature(CityRequest request, StreamObserver<CityTemperature> responseObserver) {
        String namePart = request.getNamePart().toLowerCase(Locale.forLanguageTag("hr-HR"));
        try {
            Hrvatska data = dhmzClient.fetchData();
            String measuredAt = data.getDatumTermin() != null ? data.getDatumTermin() : "";

            for (Grad grad : data.getGradovi()) {
                if (grad.getGradIme() == null) continue;
                if (!grad.getGradIme().toLowerCase(Locale.forLanguageTag("hr-HR")).contains(namePart)) continue;
                if (grad.getPodaci() == null || grad.getPodaci().getTemp() == null) continue;

                try {
                    double tempC = Double.parseDouble(grad.getPodaci().getTemp().trim());
                    CityTemperature ct = CityTemperature.newBuilder()
                            .setCity(grad.getGradIme())
                            .setTemperatureC(tempC)
                            .setMeasuredAt(measuredAt)
                            .build();
                    responseObserver.onNext(ct);
                } catch (NumberFormatException ignored) {
                    // skip cities with non-numeric temp (e.g. "-" when station is down)
                }
            }
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL.withDescription("Failed to fetch DHMZ data: " + e.getMessage()).asException()
            );
        }
    }
}
