package edu.rective.stocks.infrastructure;

import edu.rective.stocks.application.StockInfoService;
import edu.rective.stocks.domain.Stock;
import org.joda.money.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static org.joda.money.CurrencyUnit.EUR;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class StockInfoControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private StockInfoService stockInfoServiceMock;

    private Stock stock;

    @BeforeEach
    void setUp() {
        stock = new Stock("J23JJFBS2865D", Money.of(EUR, 9.42));
        ReflectionTestUtils.setField(stock, "id", 1);
        ReflectionTestUtils.setField(stock, "updatedOn", Instant.parse("2023-01-04T15:25:58.834445Z"));

        when(stockInfoServiceMock.getStockInfo("J23JJFBS2865D")).thenReturn(Mono.just(stock));
        when(stockInfoServiceMock.createNewStock(any(Stock.class))).thenAnswer(i -> Mono.just(stock));
    }

    @Test
    void testGetStockByIsin() {
        webTestClient.get().uri("/stocks/J23JJFBS2865D")
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectHeader()
                .contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.isin").isEqualTo("J23JJFBS2865D")
                .jsonPath("$.priceAmount").isEqualTo(9.42)
                .jsonPath("$.priceCurrency").isEqualTo("EUR")
                .jsonPath("$.updatedOn").isEqualTo("2023-01-04T15:25:58.834445Z");
    }

    @Test
    void testCreateStock() {
        webTestClient.post().uri("/stocks")
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .body(Mono.just(StockDTO.of(stock)), StockDTO.class)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectHeader()
                .contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.isin").isEqualTo("J23JJFBS2865D")
                .jsonPath("$.priceAmount").isEqualTo(9.42)
                .jsonPath("$.priceCurrency").isEqualTo("EUR")
                .jsonPath("$.updatedOn").exists();
    }

}