package nl.tudelft.sem.transactions.client.communication;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import nl.tudelft.sem.transactions.entities.Transactions;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@SuppressWarnings("PMD")
public class ServerTransactionsCommunication {
    private final String baseUrl = "http://localhost:8080";
    protected WebClient webClient = WebClient.create(baseUrl);

    public String getBaseUrl() {
        return baseUrl;
    }

    public WebClient getWebClient() {
        return webClient;
    }

    public void setWebClient(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Gets all transaction from the database.
     *
     * @return List of all transactions
     * @throws IOException when can not read JSON
     */
    public List<Transactions> getTransactions() throws IOException {
        String jsonString;
        jsonString = this.webClient.get().uri("/allTransactions")
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

        return mapper
            .readValue(jsonString, new com
                .fasterxml
                .jackson
                .core
                .type
                .TypeReference<List<Transactions>>() {
            });
    }

    /**
     * Adds new transaction.
     *
     * @param productId        the id of a product
     * @param username         the username of the user
     * @param portionsConsumed the number of portions consumed
     * @return true if a new transaction was added
     */
    public boolean addTransaction(int productId, String username, int portionsConsumed) {

        String body = "{\"product_id\":\"" + productId
            + "\",\"username\":\"" + username
            + "\",\"portions_consumed\":\"" + portionsConsumed + "\"}";
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
