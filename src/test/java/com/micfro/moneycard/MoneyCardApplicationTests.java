package com.micfro.moneycard;


import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.micfro.moneycard.model.MoneyCard;
import net.minidev.json.JSONArray;
import org.apache.coyote.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.net.URI;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class MoneyCardApplicationTests {
  @Autowired
  TestRestTemplate restTemplate;

  @Test
  void shouldReturnAMoneyCardWhenDataIsSaved() {
    ResponseEntity<String> response = restTemplate
        .withBasicAuth("sarah1", "abc123")
        .getForEntity("/moneycards/99", String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    DocumentContext documentContext = JsonPath.parse(response.getBody());
    Number id = documentContext.read("$.id");
    assertThat(id).isEqualTo(99);

    Double amount = documentContext.read("$.amount");
    assertThat(amount).isEqualTo(123.45);
  }

  @Test
  void shouldNotReturnAMoneyCardWithAnUnknownId() {
    ResponseEntity<String> response = restTemplate
        .withBasicAuth("sarah1", "abc123")
        .getForEntity("/moneycards/1000", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).isBlank();
  }

  @Test
  @DirtiesContext
  void shouldCreateANewMoneyCard() {
    MoneyCard newMoneyCard = new MoneyCard(null, 250.00, null);
    ResponseEntity<Void> createResponse = restTemplate
        .withBasicAuth("sarah1", "abc123")
        .postForEntity("/moneycards", newMoneyCard, Void.class);
    assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    URI locationOfNewMoneyCard = createResponse.getHeaders().getLocation();
    ResponseEntity<String> getResponse = restTemplate
        .withBasicAuth("sarah1", "abc123")
        .getForEntity(locationOfNewMoneyCard, String.class);
    assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

    DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
    Number id = documentContext.read("$.id");
    Double amount = documentContext.read("$.amount");

    assertThat(id).isNotNull();
    assertThat(amount).isEqualTo(250.00);
  }


  @Test
  void shouldReturnAllMoneyCardsWhenListIsRequested() {
    ResponseEntity<String> response = restTemplate
        .withBasicAuth("sarah1", "abc123")
        .getForEntity("/moneycards", String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    DocumentContext documentContext = JsonPath.parse(response.getBody());
    int MoneyCardCount = documentContext.read("$.length()");
    assertThat(MoneyCardCount).isEqualTo(3);

    JSONArray ids = documentContext.read("$..id");
    assertThat(ids).containsExactlyInAnyOrder(99, 100, 101);

    JSONArray amounts = documentContext.read("$..amount");
    assertThat(amounts).containsExactlyInAnyOrder(123.45, 1.00, 150.00);

  }


  @Test
  void shouldReturnAPageOfMoneyCard() {
    ResponseEntity<String> response = restTemplate
        .withBasicAuth("sarah1", "abc123")
        .getForEntity("/moneycards?page=0&size=1", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    DocumentContext documentContext = JsonPath.parse(response.getBody());

    JSONArray page = documentContext.read("$.[*]");
    assertThat(page.size()).isEqualTo(1);

  }


  @Test
  void shouldReturnASortedPageOfMoneyCards() {
    ResponseEntity<String> response = restTemplate
        .withBasicAuth("sarah1", "abc123")
        .getForEntity("/moneycards?page=0&size=1&sort=amount,desc", String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    DocumentContext documentContext = JsonPath.parse(response.getBody());
    JSONArray read = documentContext.read("$[*]");
    assertThat(read.size()).isEqualTo(1);

    double amount = documentContext.read("$[0].amount");
    assertThat(amount).isEqualTo(150.00);
  }

  @Test
  void shouldReturnASortedPageOfMoneyCardsWithNoParametersAndUseDefaultValues() {
    ResponseEntity<String> response = restTemplate
        .withBasicAuth("sarah1", "abc123")
        .getForEntity("/moneycards", String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    DocumentContext documentContext = JsonPath.parse(response.getBody());
    JSONArray page = documentContext.read("$[*]");
    assertThat(page.size()).isEqualTo(3);

    JSONArray amounts = documentContext.read("$..amount");
    assertThat(amounts).containsExactly(1.00, 123.45, 150.00);
  }


  @Test
  void shouldNotReturnAMoneyCardWhenUsingBadCredentials() {
    ResponseEntity<String> response1 = restTemplate
        .withBasicAuth("BAD-USER", "abc123")
        .getForEntity("/moneycards/99", String.class);
    assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

    ResponseEntity<String> response2 = restTemplate
        .withBasicAuth("sarah1", "BAD-PASSWORD")
        .getForEntity("/moneycards/99", String.class);
    assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

  }

  @Test
  void shouldRejectUsersWhoAreNotCardOwners() {
    ResponseEntity<String> response = restTemplate
        .withBasicAuth("hank-owns-no-cards", "qrs456")
        .getForEntity("/moneycards/99", String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }


  @Test
  void shouldNotAllowAccessToMoneyCardsTheyDoNotOwn() {
    ResponseEntity<String> response = restTemplate
        .withBasicAuth("sarah1", "abc123")
        .getForEntity("/moneycards/102", String.class); // kumar2's data
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }


  @Test
  @DirtiesContext
  void shouldUpdateAnExistingMoneyCard() {
    MoneyCard moneyCardUpdate = new MoneyCard(null, 19.99, null);
    HttpEntity<MoneyCard> request = new HttpEntity<>(moneyCardUpdate);
    ResponseEntity<Void> response = restTemplate
        .withBasicAuth("sarah1", "abc123")
        .exchange("/moneycards/99", HttpMethod.PUT, request, Void.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);


    ResponseEntity<String> getResponse = restTemplate
        .withBasicAuth("sarah1", "abc123")
        .getForEntity("/moneycards/99", String.class);
    assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
    Number id = documentContext.read("$.id");
    Double amount = documentContext.read("$.amount");
    assertThat(id).isEqualTo(99);
    assertThat(amount).isEqualTo(19.99);
  }

  @Test
  void shouldNotUpdateAMoneyCardThatDoesNotExist() {
    MoneyCard unknownCard = new MoneyCard(null, 19.99, null);
    HttpEntity<MoneyCard> request = new HttpEntity<>(unknownCard);
    ResponseEntity<Void> response = restTemplate
        .withBasicAuth("sarah1", "abc123")
        .exchange("/moneycards/99 99", HttpMethod.PUT, request, Void.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }


  @Test
  void shouldNotUpdateAMoneyCardThatIsOwnedBySomeoneElse() {
    MoneyCard kumarsCard = new MoneyCard(null, 333.33, null);
    HttpEntity<MoneyCard> request = new HttpEntity<>(kumarsCard);
    ResponseEntity<Void> response = restTemplate
        .withBasicAuth("sarah1", "abc123")
        .exchange("/moneycards/102", HttpMethod.PUT, request, Void.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }



  @Test
  @DirtiesContext
  void shouldDeleteAnExistingMoneyCard() {
    ResponseEntity<Void> response = restTemplate
        .withBasicAuth("sarah1", "abc123")
        .exchange("/moneycards/99", HttpMethod.DELETE, null, Void.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    ResponseEntity<String> getResponse = restTemplate
        .withBasicAuth("sarah1", "abc123")
        .getForEntity("/moneycards/99", String.class);
    assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }


  @Test
  void shouldNotAllowDeletetionOfMoneyCardTheyDoNotOwn() {
    ResponseEntity<Void> deleteResponse = restTemplate
        .withBasicAuth("sarah1", "abc123")
        .exchange("/moneycards/102", HttpMethod.DELETE, null, Void.class);
    assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    ResponseEntity<String> getResponse = restTemplate
        .withBasicAuth("kumar2", "xyz789")
        .getForEntity("/moneycards/102", String.class);
    assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
  }



}























