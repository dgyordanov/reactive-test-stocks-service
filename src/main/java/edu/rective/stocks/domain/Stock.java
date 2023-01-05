package edu.rective.stocks.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.Validate;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.time.Instant;

@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Stock {

    @Getter
    @Id
    private Integer id;

    @Getter
    private String isin;

    private BigDecimal priceAmount;

    private String priceCurrency;

    @Getter
    private Instant updatedOn;

    public Stock(String isin, Money price) {
        Validate.notNull(isin);
        Validate.notNull(price);

        this.isin = isin;
        this.priceAmount = price.getAmount();
        this.priceCurrency = price.getCurrencyUnit().getCode();
        this.updatedOn = Instant.now();
    }

    public Money getPrice() {
        return Money.of(CurrencyUnit.of(priceCurrency), priceAmount);
    }

    public Stock updatePrice(Money newPrice) {
        this.priceAmount = newPrice.getAmount();
        this.priceCurrency = newPrice.getCurrencyUnit().getCode();
        updatedOn = Instant.now();

        return this;
    }

}
