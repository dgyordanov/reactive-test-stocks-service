package edu.rective.stocks.domain;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface StockRepository extends ReactiveCrudRepository<Stock, String> {

    Mono<Stock> findByIsin(String isin);
}
