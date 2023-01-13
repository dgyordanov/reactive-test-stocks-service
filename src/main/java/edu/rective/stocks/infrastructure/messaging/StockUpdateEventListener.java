package edu.rective.stocks.infrastructure.messaging;

import edu.rective.stocks.application.StockInfoService;
import edu.rective.stocks.infrastructure.StockDTO;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

@Configuration
public class StockUpdateEventListener {

    @Bean
    public Consumer<Flux<StockDTO>> stockUpdates(StockInfoService stockInfoService) {
        return flux -> flux
                .doOnNext(System.out::println)
                .doOnNext(stock ->
                        stockInfoService.updateStockPrice(
                                stock.isin(),
                                Money.of(CurrencyUnit.of(stock.priceCurrency()), stock.priceAmount())))
                .subscribe();
    }

}
