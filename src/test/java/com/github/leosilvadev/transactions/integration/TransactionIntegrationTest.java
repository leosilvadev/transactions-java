package com.github.leosilvadev.transactions.integration;

import com.github.leosilvadev.transactions.domains.Transaction;
import com.github.leosilvadev.transactions.repositories.TransactionRepository;
import com.github.leosilvadev.transactions.services.TransactionManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;

/**
 * Created by leonardo on 9/26/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TransactionIntegrationTest {

    @Autowired
    private TransactionRepository repository;

    @Before
    public void setup() {
        this.repository.removeAllOlderThan(Instant.now());
    }

    @Test
    public void shouldRegisterAValidTransaction() {
        given()
            .header("Content-Type", "application/json")
            .body(new Transaction(10.0, Instant.now()))
        .when()
            .post("/transactions").
        then()
            .assertThat()
                .statusCode(201);
    }

    @Test
    public void shouldNotRegisterAnOldTransaction() {
        given()
            .header("Content-Type", "application/json")
            .body(new Transaction(10.0, Instant.now().minusSeconds(61)))
        .when()
            .post("/transactions")
        .then()
            .assertThat()
                .statusCode(204);
    }

    @Test
    public void shouldGenerateTheSummaryWithSomeTransactions() throws InterruptedException {
        repository.register(new Transaction(10.0, Instant.now()));
        repository.register(new Transaction(11.0, Instant.now()));
        repository.register(new Transaction(12.0, Instant.now().plusSeconds(1)));
        repository.register(new Transaction(13.0, Instant.now().plusSeconds(1)));
        repository.register(new Transaction(14.0, Instant.now().plusSeconds(2)));
        repository.register(new Transaction(15.0, Instant.now().plusSeconds(2)));

        repository.register(new Transaction(12.0, Instant.now().minusSeconds(58)));
        repository.register(new Transaction(13.0, Instant.now().minusSeconds(58)));
        repository.register(new Transaction(14.0, Instant.now().minusSeconds(58)));
        repository.register(new Transaction(15.0, Instant.now().minusSeconds(58)));

        // Waiting for the latest transactions to become invalid
        Thread.sleep(5_000);

        when()
            .get("/transactions")
        .then()
            .assertThat()
                .statusCode(200)
            .assertThat()
                .body("sum", equalTo(75.0f))
                .body("avg", equalTo(12.5f))
                .body("max", equalTo(15.0f))
                .body("min", equalTo(10.0f))
                .body("count", equalTo(6));
    }

}
