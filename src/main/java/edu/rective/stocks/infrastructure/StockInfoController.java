package edu.rective.stocks.infrastructure;

import edu.rective.stocks.application.StockInfoService;
import edu.rective.stocks.domain.Stock;
import lombok.AllArgsConstructor;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/stocks")
@AllArgsConstructor
public class StockInfoController {

    private final StockInfoService stockInfoService;

    @PostMapping
    Mono<StockDTO> createNewStock(@RequestBody StockDTO stock) {
        return stockInfoService
                .createNewStock(
                        new Stock(
                                stock.isin(),
                                Money.of(CurrencyUnit.of(stock.priceCurrency()), stock.priceAmount()))
                )
                .map(StockDTO::of);
    }

    @GetMapping("/{isin}")
    Mono<StockDTO> byIsin(@PathVariable String isin) {
        return stockInfoService
                .getStockInfo(isin)
                .map(StockDTO::of);
    }


}
