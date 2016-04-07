package com.zooplus.openexchange.tests.unit;

import com.zooplus.openexchange.clients.RestClient;
import com.zooplus.openexchange.protocol.v1.Status;
import com.zooplus.openexchange.starters.ControllersStarter;
import javafx.util.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Optional;

import static com.zooplus.openexchange.controllers.v1.Version.STATUS_PATH;
import static com.zooplus.openexchange.security.filters.CsrfTokenGeneratorFilter.*;
import static com.zooplus.openexchange.security.filters.DataSourceAuthenticationFilter.X_AUTH_TOKEN_HEADER;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(ControllersStarter.class)
@WebIntegrationTest("server.port:0")
@ActiveProfiles("controllers")
public class TestAdminFlows extends TestMockedClient {
    @Test
    public void testAdminStatusPath() throws Throwable {
        CsrfToken csrfToken = mockCsrfToken();

        // Make a request
        ResponseEntity<Status> response =
                getRestClient()
                        .exchange(
                                STATUS_PATH,
                                HttpMethod.GET,
                                RestClient.build(
                                        new Pair<>(X_AUTH_TOKEN_HEADER, getAdminSessionToken()),
                                        new Pair<>(CSRF_TOKEN_HEADER, csrfToken.getHeaderName())
                                ),
                                Optional.empty(),
                                Status.class);

        Assert.assertNotNull(response);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(response.getBody().getInstanceId(), "7935a0789a204973ab70b6f01458b4f3");
    }
}
