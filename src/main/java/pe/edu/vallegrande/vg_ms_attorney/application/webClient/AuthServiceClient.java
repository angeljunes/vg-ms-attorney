package pe.edu.vallegrande.vg_ms_attorney.application.webClient;

import com.google.common.net.HttpHeaders;
import io.netty.channel.ChannelOption;
import io.netty.handler.logging.LogLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.vallegrande.vg_ms_attorney.domain.dto.TokenValidationResponse;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import java.time.Duration;

@Component
public class AuthServiceClient {

    private final WebClient webClient;

    @Autowired
    public AuthServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://profound-lark-web-maria-enriqueta-dominicci-7da37999.koyeb.app/firebase-users")
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                        .responseTimeout(Duration.ofSeconds(5))
                        .wiretap("reactor.netty.http.client.HttpClient", LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL)))
                .build();
    }

    public Mono<TokenValidationResponse> validateToken(String token) {
        return webClient.get()
                .uri("/validate")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(TokenValidationResponse.class);
    }

    public WebClient getWebClient() {
        return webClient;
    }

}
