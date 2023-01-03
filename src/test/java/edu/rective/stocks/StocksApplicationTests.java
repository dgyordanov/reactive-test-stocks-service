package edu.rective.stocks;

import edu.rective.stocks.domain.Stock;
import edu.rective.stocks.domain.StockRepo;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

@SpringBootTest
class StocksRepoTests {

	@Autowired
	private StockRepo stockRepo;

	@Test
	void persistStockTest() {
		Stock testStock = new Stock("testISIN", Money.of(CurrencyUnit.EUR, BigDecimal.valueOf(10.23)));

		Mono<Stock> saved = stockRepo.save(testStock);
		StepVerifier.create(saved)
				.expectNext(testStock)
				.verifyComplete();

		Flux<Stock> findByIsin = stockRepo.findByIsin("testISIN");
		StepVerifier.create(findByIsin)
				.expectNext(testStock)
				.verifyComplete();

	}

}
