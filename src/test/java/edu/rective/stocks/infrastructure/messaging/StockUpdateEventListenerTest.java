package edu.rective.stocks.infrastructure.messaging;

import edu.rective.stocks.application.StockInfoService;
import edu.rective.stocks.infrastructure.StockDTO;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.function.cloudevent.CloudEventMessageBuilder;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;

import static io.netty.handler.codec.http.HttpHeaders.Values.APPLICATION_JSON;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Import(TestChannelBinderConfiguration.class)
class StockUpdateEventListenerTest {

    @Autowired
    private InputDestination inputDestination;

    @MockBean
    private StockInfoService stockInfoServiceMock;

    @Test
    void eventConsumptionTest() {
        var stockData = new StockDTO(null, "IBM", BigDecimal.valueOf(22.34), "USD", Instant.now());
        var cloudEvent = CloudEventMessageBuilder
                .withData(stockData)
                .setSource(URI.create("test"))
                .setType("com.finance.stock.Updated")
                .setDataContentType(APPLICATION_JSON)
                .build();

        inputDestination.send(cloudEvent, "com.financial.stock");

        verify(stockInfoServiceMock).updateStockPrice(stockData.isin(), Money.of(CurrencyUnit.of(stockData.priceCurrency()), stockData.priceAmount()));
    }
}