package com.zooplus.openexchange.tests.unit;

import com.zooplus.openexchange.database.domain.Currency;
import com.zooplus.openexchange.database.domain.Rate;
import com.zooplus.openexchange.integrations.gateways.CurrenciesGateway;
import com.zooplus.openexchange.starters.LogicStarter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.concurrent.ListenableFuture;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(LogicStarter.class)
@ActiveProfiles("logic")
public class TestIntegrationFramework {
    @Autowired
    CurrenciesGateway gateway;

    @Test
    public void testSupportedCurrenciesList() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean stateSuccess = new AtomicBoolean(false);
        AtomicBoolean stateError = new AtomicBoolean(false);
        ListenableFuture<List<Currency>> reply = gateway.getCurrenciesList();
        reply.addCallback(currencies1 -> {
            try {
                List<Currency> currencyList = reply.get();
                Assert.assertNotNull(currencyList);
                Assert.assertTrue(currencyList.size() > 0);
                stateSuccess.compareAndSet(false, true);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                stateError.compareAndSet(false, true);
            }
            latch.countDown();
        }, throwable -> {
            throwable.printStackTrace();
            stateError.compareAndSet(false, true);
            latch.countDown();
        });
        latch.await(3000, TimeUnit.MILLISECONDS);
        Assert.assertTrue(stateSuccess.get());
        Assert.assertFalse(stateError.get());
    }

    @Test
    public void testRates() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean stateSuccess = new AtomicBoolean(false);
        AtomicBoolean stateError = new AtomicBoolean(false);
        ListenableFuture<List<Rate>> reply = gateway.getRates(Date.from(Instant.now()), Optional.of(new Currency("USD", "United States Dollar")));
        reply.addCallback(currencies1 -> {
            try {
                List<Rate> rates = reply.get();
                Assert.assertNotNull(rates);
                Assert.assertEquals(rates.get(0).getAlternative().getCode(), "EUR");
                stateSuccess.compareAndSet(false, true);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                stateError.compareAndSet(false, true);
            }
            latch.countDown();
        }, throwable -> {
            throwable.printStackTrace();
            stateError.compareAndSet(false, true);
            latch.countDown();
        });
        ;
        latch.await(3000, TimeUnit.MILLISECONDS);
        Assert.assertTrue(stateSuccess.get());
        Assert.assertFalse(stateError.get());
    }
}
