package com.zooplus.openexchange.tests.unit;

import com.zooplus.openexchange.protocol.integration.HistoricalQuotesRequest;
import com.zooplus.openexchange.services.external.currencylayer.api.CurrencyLayerApi;
import com.zooplus.openexchange.starters.UnitTestStarter;
import org.apache.commons.lang.time.DateFormatUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(UnitTestStarter.class)
@ActiveProfiles("api")
public class TestIntegrationFramework {
    @Autowired
    private CurrencyLayerApi currencyLayerApiGateway;

    @Test
    public void testCurrencyList() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean stateSuccess = new AtomicBoolean(false);
        AtomicBoolean stateError = new AtomicBoolean(false);
        currencyLayerApiGateway.getCurrenciesList(10)
                .addCallback(
                        currencies -> {
                            Assert.assertTrue(currencies.getCurrencies().size() > 0);
                            stateSuccess.compareAndSet(false, true);
                            latch.countDown();
                        }, throwable -> {
                            throwable.printStackTrace();
                            stateError.compareAndSet(false, true);
                            latch.countDown();
                        });
        latch.await(5000, TimeUnit.MILLISECONDS);
        Assert.assertTrue(stateSuccess.get());
        Assert.assertFalse(stateError.get());
    }

    @Test
    public void testHistoricalQuotes() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean stateSuccess = new AtomicBoolean(false);
        AtomicBoolean stateError = new AtomicBoolean(false);
        HistoricalQuotesRequest request = new HistoricalQuotesRequest();
        request.setCurrencyCode("USD");
        request.setExchangeDate(DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd"));
        currencyLayerApiGateway.getHistoricalQuotes("USD", DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd"))
                .addCallback(
                        historicalQuotes -> {
                            Assert.assertTrue(historicalQuotes.getQuotes().containsKey("EUR"));
                            Assert.assertTrue(historicalQuotes.getQuotes().get("EUR") > 0);
                            stateSuccess.compareAndSet(false, true);
                            latch.countDown();
                        }, throwable -> {
                            throwable.printStackTrace();
                            stateError.compareAndSet(false, true);
                            latch.countDown();
                        });
        ;
        latch.await(5000, TimeUnit.MILLISECONDS);
        Assert.assertTrue(stateSuccess.get());
        Assert.assertFalse(stateError.get());
    }
}
