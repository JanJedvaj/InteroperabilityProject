package hr.algebra.books.config;

import hr.algebra.books.grpc.WeatherGrpcService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GrpcServerConfig {

    private final WeatherGrpcService weatherGrpcService;

    @Value("${grpc.server.port}")
    private int grpcPort;

    private Server server;

    @PostConstruct
    public void startServer() throws Exception {
        server = ServerBuilder.forPort(grpcPort)
                .addService(weatherGrpcService)
                .build()
                .start();
        System.out.println("gRPC Server pokrenut na portu " + grpcPort);
    }

    @PreDestroy
    public void stopServer() {
        if (server != null) {
            server.shutdown();
            System.out.println("gRPC Server zaustavljen.");
        }
    }
}
