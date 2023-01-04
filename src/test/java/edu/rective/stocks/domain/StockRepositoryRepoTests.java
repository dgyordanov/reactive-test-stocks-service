package edu.rective.stocks.domain;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

@SpringBootTest
public class StockRepositoryRepoTests {

	@Autowired
	private StockRepository stockRepository;

	@Test
	void persistStockTest() {
		Stock testStock = new Stock("testISIN", Money.of(CurrencyUnit.EUR, BigDecimal.valueOf(10.23)));

		Mono<Stock> saved = stockRepository.save(testStock);
		StepVerifier.create(saved)
				.expectNext(testStock)
				.verifyComplete();

		Mono<Stock> findByIsin = stockRepository.findByIsin("testISIN");
		StepVerifier.create(findByIsin)
				.expectNext(testStock)
				.verifyComplete();

	}

}
