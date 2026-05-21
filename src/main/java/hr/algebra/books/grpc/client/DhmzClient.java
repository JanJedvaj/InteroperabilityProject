package hr.algebra.books.grpc.client;

import hr.algebra.books.common.config.AppProperties;
import hr.algebra.books.grpc.parser.model.Hrvatska;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.time.Instant;

/**
 * Fetches and caches the DHMZ current-conditions XML feed.
 * The feed is windows-1250 encoded — explicit charset is required to avoid mojibake
 * on Croatian diacritics (Šibenik, Đakovo, etc.).
 */
@Component
public class DhmzClient {

    private final WebClient webClient;
    private final AppProperties props;

    private volatile Hrvatska cached;
    private volatile Instant cacheTime;

    public DhmzClient(WebClient.Builder webClientBuilder, AppProperties props) {
        this.webClient = webClientBuilder.build();
        this.props = props;
    }

    public Hrvatska fetchData() throws Exception {
        if (cached != null && cacheTime != null
                && Instant.now().isBefore(cacheTime.plus(props.dhmz().cacheTtl()))) {
            return cached;
        }

        byte[] body = webClient.get()
                .uri(props.dhmz().url())
                .retrieve()
                .bodyToMono(byte[].class)
                .block();

        JAXBContext ctx = JAXBContext.newInstance(Hrvatska.class);
        Unmarshaller u = ctx.createUnmarshaller();

        try (var reader = new InputStreamReader(
                new ByteArrayInputStream(body), Charset.forName("windows-1250"))) {
            cached = (Hrvatska) u.unmarshal(new InputSource(reader));
            cacheTime = Instant.now();
            return cached;
        }
    }
}
