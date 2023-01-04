package edu.rective.stocks.application;

import edu.rective.stocks.domain.Stock;
import edu.rective.stocks.domain.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Service
@RequiredArgsConstructor
public class StockInfoService {

    private final StockRepository stockRepository;

    public Mono<Stock> getStockInfo(String isin) {
        if (isEmpty(isin)) {
            return Mono.error(new IllegalArgumentException("ISIN must be provided"));
        }

        return stockRepository
                .findByIsin(isin)
                .switchIfEmpty(
                        Mono.error(new IllegalArgumentException(String.format("No stock with ISIN %s exists", isin)))
                );
    }

    public Mono<Stock> createNewStock(Stock stock) {
        if (stock.getId() != null) {
            return Mono.error(new IllegalArgumentException("New stock entity must have null as ID"));
        }
        return stockRepository.save(stock);
    }

}
