package edu.rective.stocks.application;

import edu.rective.stocks.domain.Stock;
import edu.rective.stocks.domain.StockRepository;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StockInfoServiceTest {

    private StockInfoService stockInfoService;

    private static final String ISIN = "J123BK124";

    private static final Stock EXISTING_STOCK = new Stock(ISIN, Money.of(CurrencyUnit.EUR, 12.24));


    @BeforeEach
    void setUp() {
        StockRepository stockRepoMock = mock(StockRepository.class);
        when(stockRepoMock.findByIsin(anyString()))
                .thenAnswer(invocation ->
                        invocation.getArgument(0, String.class).equals(ISIN)
                                ? Mono.just(EXISTING_STOCK)
                                : Mono.empty()
                );

        stockInfoService = new StockInfoService(stockRepoMock);
    }

    @Test
    public void givenAStockWithISIN_whenGetStockInfoByISIN_thenReturnTheStock() {
        StepVerifier.create(stockInfoService.getStockInfo(ISIN))
                .expectNext(EXISTING_STOCK)
                .expectComplete();
    }

    @Test
    public void givenAStockWithISIN_whenGetStockInfoByNotExistingISIN_thenEmitError() {
        StepVerifier.create(stockInfoService.getStockInfo("notExisting"))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    public void givenAStockWithISIN_whenGetStockInfoByNullISIN_thenEmitError() {
        StepVerifier.create(stockInfoService.getStockInfo(null))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    public void givenAStockWithISIN_whenGetStockInfoByNotEmptryISIN_thenEmitError() {
        StepVerifier.create(stockInfoService.getStockInfo(""))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

}