package edu.rective.stocks.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.function.context.config.JsonMessageConverter;
import org.springframework.cloud.function.json.JacksonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class Config {

    private static final int TIMEOUT_MILLIS = 1000;

    @Configuration
    public static class CloudEventMessageConverterConfiguration {

        @Bean
        public JsonMessageConverter cloudEventJsonMessageConverter() {
            return new JsonMessageConverter(new JacksonMapper(new ObjectMapper()));
        }
    }

    @Bean
    public WebClient AlphavantageWebClient(WebClient.Builder webClientBuilder, @Value("${alphavantage.api.baseuri}") String baseUri) {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIMEOUT_MILLIS)
                .responseTimeout(Duration.ofMillis(TIMEOUT_MILLIS))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)));

        return webClientBuilder
                .baseUrl(baseUri)
                .filter(logRequest())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    // This method returns filter function which will log request data
    private static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            System.out.println("Request: " + clientRequest.method() + "  " + clientRequest.url());
            clientRequest.headers().forEach((name, values) -> values.forEach(value -> System.out.println(name + " = " + value)));
            return Mono.just(clientRequest);
        });
    }

}
