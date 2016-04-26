package com.zooplus.openexchange.integrations.gateways;

import com.zooplus.openexchange.protocol.ws.v1.CurrencyListRequest;
import com.zooplus.openexchange.protocol.ws.v1.CurrencyListResponse;
import com.zooplus.openexchange.protocol.ws.v1.HistoricalQuotesRequest;
import com.zooplus.openexchange.protocol.ws.v1.HistoricalQuotesResponse;
import org.springframework.integration.annotation.GatewayHeader;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.util.concurrent.ListenableFuture;

import static com.zooplus.openexchange.integrations.configurations.CurrencyLayerApiIntegrationConfiguration.IN_DIRECT_CURRENCYLAYER;

@MessagingGateway(
        defaultRequestChannel = IN_DIRECT_CURRENCYLAYER,
        defaultHeaders = @GatewayHeader(name = "calledMethod", expression = "#gatewayMethod.name")
)
public interface CurrencyLayerApiGateway {
    ListenableFuture<CurrencyListResponse> getCurrenciesList(@Payload CurrencyListRequest request);

    ListenableFuture<HistoricalQuotesResponse> getHistoricalQuotes(@Payload HistoricalQuotesRequest request);
}
