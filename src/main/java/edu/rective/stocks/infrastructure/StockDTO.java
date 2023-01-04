package edu.rective.stocks.infrastructure;

import edu.rective.stocks.domain.Stock;

import java.math.BigDecimal;
import java.time.Instant;

public record StockDTO(
        Integer id,
        String isin,
        BigDecimal priceAmount,
        String priceCurrency,
        Instant updatedOn) {

    public static StockDTO of(Stock stock) {
        return new StockDTO(
                stock.getId(),
                stock.getIsin(),
                stock.getPrice().getAmount(),
                stock.getPrice().getCurrencyUnit().getCode(),
                stock.getUpdatedOn()
        );
    }

}
