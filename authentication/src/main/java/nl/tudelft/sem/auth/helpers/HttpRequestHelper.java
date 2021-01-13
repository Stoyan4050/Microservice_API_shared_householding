package nl.tudelft.sem.auth.helpers;

import com.netflix.discovery.EurekaClient;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class HttpRequestHelper {

    /**
     * Build an HTTP request.
     *
     * @param discoveryClient Eureka client used to get the URI of the requests microservice.
     * @param userRequestJson A JSON representation of a UserRequest object.
     * @return The newly built HttpRequest.
     */
    private static HttpRequest buildHttpRequest(EurekaClient discoveryClient,
                                               String userRequestJson) {
        // get the uri of the requests microservice from eureka
        String requestsUri = discoveryClient
                .getNextServerFromEureka("REQUESTS", false).getHomePageUrl();

        // build a new POST request
        return HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(userRequestJson))
                .uri(URI.create(requestsUri + "addNewUser/")).build();
    }

    /**
     * Send an HTTP request to the requests microservice.
     *
     * @param discoveryClient Eureka client used to get the URI of the requests microservice.
     * @param userRequestJson A JSON representation of a UserRequest object.
     * @return The HTTP response from the requests microservice.
     * @throws IOException May be thrown by HttpClient's send() method.
     * @throws InterruptedException May be thrown by HttpClient's send() method.
     */
    public static HttpResponse<String> sendHttpRequest(EurekaClient discoveryClient,
                                                       String userRequestJson)
            throws IOException, InterruptedException {
        return HttpClient.newHttpClient()
                .send(buildHttpRequest(discoveryClient, userRequestJson),
                        HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Check a given HTTP response code.
     *
     * @param response The response whose code will be checked.
     * @return A response entity if the response code is anything other than
     *         201 CREATED.
     *         If the response code is correct, return an empty Optional
     *         to indicate that there were no problems.
     */
    public static Optional<ResponseEntity<String>>
                            checkResponseCode(HttpResponse<String> response) {
        // if the microservice returns anything different from 201, report to the client
        if (response.statusCode() != HttpStatus.CREATED.value()) {
            System.out.println("Status: " + response.statusCode());
            return Optional.of(ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY)
                    .body("Requests microservice returned: "
                            + HttpStatus.resolve(response.statusCode())));
        }
        return Optional.empty();
    }
}
