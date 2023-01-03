package edu.rective.stocks.domain;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface StockRepo extends ReactiveCrudRepository<Stock, String> {

    Flux<Stock> findByIsin(String isin);
}
