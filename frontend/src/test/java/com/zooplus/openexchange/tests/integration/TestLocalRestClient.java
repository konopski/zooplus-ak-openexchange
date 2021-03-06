package com.zooplus.openexchange.tests.integration;

import com.zooplus.openexchange.clients.RestClient;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;

public abstract class TestLocalRestClient {
    private RestClient restClient;
    @Value("${local.server.port}")
    private int port;

    protected RestClient getRestClient() {
        return restClient;
    }

    @PostConstruct
    private void initRestClient(){
        this.restClient = new RestClient("localhost", port);
    }
}
