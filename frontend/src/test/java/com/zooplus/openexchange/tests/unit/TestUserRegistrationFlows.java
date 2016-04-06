package com.zooplus.openexchange.tests.unit;

import com.zooplus.openexchange.clients.RestClient;
import com.zooplus.openexchange.protocol.v1.Loginresponse;
import com.zooplus.openexchange.protocol.v1.Registrationrequest;
import com.zooplus.openexchange.protocol.v1.Registrationresponse;
import com.zooplus.openexchange.service.frontend.database.domain.Role;
import com.zooplus.openexchange.service.frontend.database.domain.User;
import com.zooplus.openexchange.service.frontend.security.SecurityConfig;
import com.zooplus.openexchange.starters.ControllersStarter;
import javafx.util.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static com.zooplus.openexchange.service.frontend.controllers.v1.Version.USER_LOGIN_PATH;
import static com.zooplus.openexchange.service.frontend.controllers.v1.Version.USER_REGISTRATION_PATH;
import static com.zooplus.openexchange.service.frontend.security.SecurityConfig.X_AUTH_TOKEN_HEADER;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(ControllersStarter.class)
@WebIntegrationTest("server.port:0")
@ActiveProfiles("controllers")
public class TestUserRegistrationFlows extends TestMockedClient {

    @Test
    public void testUserRegistration() throws Exception {
        // Request database
        final String userName = "ak";
        final String userPassword = "pwd";
        final String userEmail = "ak@zooplus.com";

        // Remember creation timestamp
        final long preUserCreationTimeStamp = System.currentTimeMillis();

        // Mock new user in not in db
        User user = new User(userName, passwordEncoder.encode(userPassword), userEmail, Collections.singleton(new Role(getNextId(), "USER")));
        user.setId(getNextId());
        user.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        user.setEnabled(true);
        Mockito.when(userRepository.findByName(user.getName())).thenReturn(null);
        Mockito.when(userRepository.saveAndFlush(Mockito.any(User.class))).thenReturn(user);

        // Create request
        Registrationrequest req = new Registrationrequest();
        req.setName(userName);
        req.setPassword(userPassword);
        req.setEmail(userEmail);

        // Send request
        ResponseEntity<Registrationresponse> resp =
                getRestClient()
                        .exchange(
                                USER_REGISTRATION_PATH,
                                HttpMethod.POST,
                                RestClient.headersFrom(new Pair<>(SecurityConfig.X_AUTH_TOKEN_HEADER, getAdminSessionToken())),
                                Optional.of(req),
                                Registrationresponse.class);

        // Analyze response
        Assert.assertNotNull(resp);
        Assert.assertEquals(resp.getStatusCode(), HttpStatus.OK);
        Assert.assertNotNull(resp.getBody().getId());

        // Mock new user in db
        long newUserId = resp.getBody().getId();
        Mockito.when(userRepository.findOne(newUserId)).thenReturn(user);
        Mockito.when(userRepository.findByName(user.getName())).thenReturn(user);

        // Send the same request second time => HttpStatus.CONFLICT
        resp =
                getRestClient()
                        .exchange(
                                USER_REGISTRATION_PATH,
                                HttpMethod.POST,
                                RestClient.headersFrom(new Pair<>(SecurityConfig.X_AUTH_TOKEN_HEADER, getAdminSessionToken())),
                                Optional.of(req),
                                Registrationresponse.class);

        // Analyze response
        Assert.assertNotNull(resp);
        Assert.assertEquals(resp.getStatusCode(), HttpStatus.CONFLICT);

        // Fetch user directly from repository by Id
        User existedUser = userRepository.findOne(newUserId);
        Assert.assertNotNull(existedUser);
        Assert.assertEquals(existedUser.getName(), userName);
        Assert.assertTrue(passwordEncoder.matches(userPassword, existedUser.getPassword()));
        Assert.assertEquals(existedUser.getEmail(), userEmail);
        Assert.assertNotNull(existedUser.getCreatedAt());
        Assert.assertTrue(preUserCreationTimeStamp < existedUser.getCreatedAt().getTime());
        Assert.assertTrue(existedUser.getEnabled());
        Assert.assertEquals(existedUser.getRoles().size(), 1);
        Assert.assertTrue(existedUser.getRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase("USER")));

        // Mock db for login
        Mockito.when(userRepository.findByNameAndPassword(user.getName(), user.getPassword())).thenReturn(user);

        // Login with a regular user
        ResponseEntity<Loginresponse> loginResp =
                getRestClient()
                        .exchange(
                                USER_LOGIN_PATH,
                                HttpMethod.POST,
                                RestClient.headersFrom(
                                        new Pair<>(SecurityConfig.X_AUTH_USERNAME_HEADER, user.getName()),
                                        new Pair<>(SecurityConfig.X_AUTH_PASSWORD_HEADER, user.getPassword())),
                                Optional.empty(),
                                Loginresponse.class);

        // Analyze login response
        Assert.assertNotNull(loginResp);
        Assert.assertEquals(loginResp.getStatusCode(), HttpStatus.OK);
        Assert.assertTrue(loginResp.hasBody());
        String newUserToken = loginResp.getHeaders().toSingleValueMap().getOrDefault(X_AUTH_TOKEN_HEADER, "");
        Assert.assertNotNull(newUserToken);
        Assert.assertNotEquals(loginResp, getAdminSessionToken());

        // Switch user session
        mockUserRedisSession(user, newUserToken);
    }
}