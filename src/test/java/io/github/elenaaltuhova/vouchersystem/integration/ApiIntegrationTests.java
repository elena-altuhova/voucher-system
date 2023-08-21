package io.github.elenaaltuhova.vouchersystem.integration;

import io.github.elenaaltuhova.vouchersystem.dto.CampaignDTO;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.text.IsEmptyString.emptyOrNullString;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiIntegrationTests {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private final String baseUrl = "http://localhost:";

    @Test
    @Order(1)
    public void testCreateCampaign() {
        //Given
        CampaignDTO campaignDTO = new CampaignDTO("Test Campaign 98", LocalDate.parse("2023-08-01"), null);

        final HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", "testkey");
        final HttpEntity<CampaignDTO> entity = new HttpEntity<>(campaignDTO, headers);

        String json = "{\"data\":{\"id\":4,\"title\":\"Test Campaign 98\",\"startDate\":\"2023-08-01\",\"endDate\":null,\"" +
            "links\":[{\"rel\":\"self\",\"href\":\"http://localhost:" + port + "/v1/campaigns/4\"}]}}";

        //When
        ResponseEntity<String> responseEntity = requestBuilder("/v1/campaigns", entity, HttpMethod.POST);

        //Then
        assertThat(responseEntity.getStatusCode(), is(equalTo(CREATED)));
        assertThat(responseEntity.getBody(), is(equalTo(json)));
    }

    @Test
    @Order(2)
    public void testSearchCampaign() {
        //Given
        final HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", "testkey");
        final HttpEntity<CampaignDTO> entity = new HttpEntity<>(headers);

        String json = "{\"data\":{\"id\":1,\"title\":\"Free Shipping\",\"startDate\":\"2023-05-23\",\"endDate\":null,\"" +
            "links\":[{\"rel\":\"self\",\"href\":\"http://localhost:" + port + "/v1/campaigns/1\"}]}}";

        //When
        ResponseEntity<String> responseEntity = requestBuilder("/v1/campaigns/1", entity, HttpMethod.GET);

        //Then
        assertThat(responseEntity.getStatusCode(), is(equalTo(OK)));
        assertThat(responseEntity.getBody(), is(equalTo(json)));
    }

    @Test
    @Order(2)
    public void testAddVouchersForCampaign() {
        //Given
        final HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", "testkey");
        final HttpEntity<CampaignDTO> entity = new HttpEntity<>(headers);

        //When
        ResponseEntity<String> responseEntity = requestBuilder("/v1/campaigns/1/vouchers?count=3", entity, HttpMethod.POST);

        //Then
        assertThat(responseEntity.getStatusCode(), is(equalTo(CREATED)));
        assertThat(responseEntity.getBody(), not(emptyOrNullString()));
    }

    @Test
    @Order(3)
    public void testCheckVoucher() {
        //Given
        final HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", "testkey");
        final HttpEntity<CampaignDTO> entity = new HttpEntity<>(headers);

        String
            responseBody =
            "{\"data\":{\"code\":\"ce90dfbd-ff2a-4e43-941c-ea26c04c6e7e\",\"campaignName\":\"Free Shipping\",\"status\":\"ISSUED\"," +
                "\"id\":1,\"links\":[{\"rel\":\"redeem\",\"href\":\"http://localhost:" + port + "/v1/vouchers/1/redeem\"}]}}";

        //When
        ResponseEntity<String> responseEntity = requestBuilder("/v1/vouchers/ce90dfbd-ff2a-4e43-941c-ea26c04c6e7e/check", entity, HttpMethod.GET);

        //Then
        assertThat(responseEntity.getStatusCode(), is(equalTo(OK)));
        assertThat(responseEntity.getBody(), is(responseBody));
    }

    @Test
    @Order(3)
    public void testSendValidVoucher() {
        //Given
        final HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", "testkey");
        final HttpEntity<CampaignDTO> entity = new HttpEntity<>(headers);

        String
            responseBody =
            "{\"data\":{\"code\":\"ce90dfbd-ff2a-4e43-941c-ea26c04c6e7e\",\"campaignName\":\"Free Shipping\",\"status\":\"ISSUED\"," +
                "\"id\":1,\"links\":[{\"rel\":\"check\",\"href\":\"http://localhost:" + port + "/v1/vouchers/ce90dfbd-ff2a-4e43-941c-ea26c04c6e7e/check\"}]}}";

        //When
        ResponseEntity<String> responseEntity = requestBuilder("/v1/vouchers/send?campaignId=1", entity, HttpMethod.GET);

        //Then
        assertThat(responseEntity.getStatusCode(), is(equalTo(OK)));
        assertThat(responseEntity.getBody(), is(responseBody));
    }

    @Test
    @Order(3)
    public void testRedeemVoucher() {
        //Given
        final HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", "testkey");
        final HttpEntity<CampaignDTO> entity = new HttpEntity<>(headers);

        String
            responseBody =
            "{\"data\":{\"code\":\"ce90dfbd-ff2a-4e43-941c-ea26c04c6e7e\",\"campaignName\":\"Free Shipping\",\"status\":\"REDEEMED\"," +
                "\"id\":1,\"links\":[{\"rel\":\"check\",\"href\":\"http://localhost:" + port + "/v1/vouchers/ce90dfbd-ff2a-4e43-941c-ea26c04c6e7e/check\"}]}}";

        //When
        ResponseEntity<String> responseEntity = requestBuilder("/v1/vouchers/1/redeem", entity, HttpMethod.POST);

        //Then
        assertThat(responseEntity.getStatusCode(), is(equalTo(OK)));
        assertThat(responseEntity.getBody(), is(responseBody));
    }


    private ResponseEntity<String> requestBuilder(String url, HttpEntity entity, HttpMethod method) {
        String fullUrl = baseUrl + port + url;
        return this.restTemplate.exchange(fullUrl, method, entity, String.class);

    }
}
