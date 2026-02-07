package in.codefarm.client.service.config;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.UUID;

import static in.codefarm.client.service.config.CorrelationIdFilter.CORRELATION_ID;

@Configuration
public class RestClientConfig {

    @Bean
    RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    RestClient restClient(RestClient.Builder builder) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(500))   // connection timeout
                .build();

        JdkClientHttpRequestFactory requestFactory =
                new JdkClientHttpRequestFactory(httpClient);

        requestFactory.setReadTimeout(Duration.ofMillis(800)); // response timeout
        return builder
                .baseUrl("http://localhost:8081/third-party")
                .requestFactory(requestFactory)
                .requestInterceptor((request, body, execution) -> {
                    String id = MDC.get(CORRELATION_ID);
                    if(id == null){
                        id = UUID.randomUUID().toString();
                    }
                    request.getHeaders().set(CORRELATION_ID, id);
                    return execution.execute(request, body);
                })
                .build();
    }
}