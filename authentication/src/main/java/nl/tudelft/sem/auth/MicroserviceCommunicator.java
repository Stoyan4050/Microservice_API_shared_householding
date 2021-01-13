package nl.tudelft.sem.auth;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponents;
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
     * @param uriComponentsBuilder A URI Components Builder that is passed from the respective
     *                             controller class.
     * @param username Username of the newly created user.
     * @return A response entity that should be returned to the client.
     */
    public ResponseEntity<String> addNewUser(String userRequestJson,
                                             UriComponentsBuilder uriComponentsBuilder,
                                             String username) {
        // get the uri of the requests microservice from eureka
        InstanceInfo requestsInstance = discoveryClient.getNextServerFromEureka("REQUESTS", false);
        String requestsUri = requestsInstance.getHomePageUrl();

        // build a new POST request
        HttpRequest addNewUserReq = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(userRequestJson))
                .uri(URI.create(requestsUri + "addNewUser/")).build();

        HttpResponse<String> response = null;
        HttpClient client = HttpClient.newBuilder().build();
        nop(response);
        try {
            response = client.send(addNewUserReq, HttpResponse.BodyHandlers.ofString());
            nop(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        // if the microservice returns anything different from 201, report to the client
        if (response.statusCode() != HttpStatus.CREATED.value()) {
            System.out.println("Status: " + response.statusCode());
            return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY)
                    .body("Requests microservice returned: "
                            + HttpStatus.resolve(response.statusCode()));
        }

        final UriComponents uri = uriComponentsBuilder
                .path("register/{user_name}").buildAndExpand(username);
        return ResponseEntity.created(uri.toUri()).build();
    }
}
