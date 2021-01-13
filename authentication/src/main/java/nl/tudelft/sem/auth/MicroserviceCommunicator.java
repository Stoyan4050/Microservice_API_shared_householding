package nl.tudelft.sem.auth;

import com.netflix.discovery.EurekaClient;
import java.io.IOException;
import java.net.http.HttpResponse;
import nl.tudelft.sem.auth.helpers.HttpRequestHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

public class MicroserviceCommunicator {

    private transient EurekaClient discoveryClient;

    public MicroserviceCommunicator(EurekaClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    // no operation method that fixes a "dataflow anomaly" PMD warning
    private static void nop(HttpResponse<String> hr) {
    }

    /**
     * Makes an HTTP request to the request microservice's addNewUser method.
     *
     * @param userRequestJson The json representation of the UserRequest object.
     * @param username Username of the newly created user.
     * @return A response entity that should be returned to the client.
     */
    public ResponseEntity<String> addNewUser(String userRequestJson,
                                             String username) {

        HttpResponse<String> response = null;
        nop(response);
        try {
            response = sendRequest(discoveryClient, userRequestJson);
            nop(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return checkResponse(response, username);
    }

    private HttpResponse<String> sendRequest(EurekaClient discoveryClient,
                                             String userRequestJson) throws IOException,
                                                                            InterruptedException {
        return HttpRequestHelper.sendHttpRequest(discoveryClient, userRequestJson);
    }

    private ResponseEntity<String> checkResponse(HttpResponse<String> response, String username) {
        return HttpRequestHelper.checkResponseCode(response)
                .orElseGet(() -> ResponseEntity.created(
                                    UriComponentsBuilder.fromPath("register/{user_name}")
                                        .buildAndExpand(username).toUri()
                                ).build()
                );
    }
}
