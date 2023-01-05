package edu.rective.stocks.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import edu.rective.stocks.application.StockInfoService;
import edu.rective.stocks.domain.StockRepository;
import jakarta.annotation.PostConstruct;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Service
public class RestApiStockInfoFetcher {

    private final WebClient webClient;

    private final StockRepository stockRepository;

    private final StockInfoService stockInfoService;

    private final String apikey;

    public RestApiStockInfoFetcher(WebClient webClient,
                                   StockRepository stockRepository,
                                   StockInfoService stockInfoService,
                                   @Value("${alphavantage.api.apikey}") String apikey) {
        this.webClient = webClient;
        this.stockRepository = stockRepository;
        this.stockInfoService = stockInfoService;
        this.apikey = apikey;
    }

    @PostConstruct
    public void init() {
        System.out.println("### RestApiStockInfoFetcher.afterPropertiesSet()");
        Flux.interval(Duration.ofSeconds(10))
                .flatMap(i -> stockRepository.findAll())
                .flatMap(stock -> getUpdatedStockInfo(stock.getIsin(), webClient))
                .doOnNext(System.out::println)
                .doOnNext(stockInfo -> stockInfoService.updateStockPrice(stockInfo.symbol, stockInfo.price))
                .subscribe();
        System.out.println("### RestApiStockInfoFetcher.afterPropertiesSet() exit");
    }


    private Mono<AlphavantageStockInfo> getUpdatedStockInfo(String stockSymbol, WebClient webClient) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("symbol", stockSymbol)
                        .queryParam("function", "GLOBAL_QUOTE")
                        .queryParam("apikey", apikey)
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
                .acceptCharset(StandardCharsets.UTF_8)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(this::toAlphavantageStockInfo);
    }

    private AlphavantageStockInfo toAlphavantageStockInfo(JsonNode json) {
        var symbol = json.get("Global Quote").get("01. symbol").textValue();
        var priceString = json.get("Global Quote").get("05. price").textValue();
        var price = new BigDecimal(priceString).setScale(2, RoundingMode.HALF_DOWN);
        return new AlphavantageStockInfo(
                symbol,
                Money.of(CurrencyUnit.USD, price)
        );
    }

    private record AlphavantageStockInfo(String symbol, Money price) {
    }

}
