package nl.tudelft.sem.template.transactions.client.communication;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tudelft.sem.template.transactions.server.entities.Transactions;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

public class ServerTransactionsCommunication {
    private String baseUrl = "http://localhost:8080";
    protected WebClient webClient = WebClient.create(baseUrl);

    public List<Transactions> getTransactions() throws IOException {
        String jsonString = this.webClient.get().uri("/allTransactions")
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> {
                    System.out.println("4xx error");
                    return Mono.error(new RuntimeException("4xx"));
                })
                .onStatus(HttpStatus::is5xxServerError, response -> {
                    System.out.println("5xx error");
                    return Mono.error(new RuntimeException("5xx"));
                })
                .bodyToMono(String.class)
                .block();

        ObjectMapper mapper = new ObjectMapper();
        List<Transactions> transactionsJsonList = mapper
                .readValue(jsonString, new com
                        .fasterxml
                        .jackson
                        .core
                        .type
                        .TypeReference<List<Transactions>>() {
                });

        return transactionsJsonList;
    }

    public boolean addTransaction(int product_id, String username, int portions_consumed) {

        String body = "{\"product_id\":\"" + product_id
                + "\",\"username\":\"" + username
                + "\",\"portions_consumed\":\"" + portions_consumed + "\"}";
        System.out.println(body);
        try {
            boolean bool = this.webClient.post().uri("/addNewTransaction")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromObject(body))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
            if (bool) {
                System.out.println("Transaction added");
                return true;
            } else {
                System.out.println("failed");
                return false;
            }
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }
}
