package nl.tudelft.sem.transactions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.InvalidParameterException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class MicroserviceCommunicator {
    private static final HttpClient httpClient = HttpClient.newBuilder().build();

    /**
     * This method sends a request to change credits.
     *
     * @param username - username of the user whose credits need to be changed
     * @param credits  - the number of credits to be added or subtracted.
     * @param add      - true if we want to add credits, false if we subtract
     */
    public static void sendRequestForChangingCredits(
            String username, float credits, boolean add) {

        if (username == null) {
            throw new InvalidParameterException("One of the parameters is null!");
        }

        String url = "http://localhost:9102/editUserCredits";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "?username=" + username
                        + "&credits=" + credits + "&add=" + add))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        try {
            HttpResponse<String> httpResponse = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString());

            if (httpResponse.body().equals("" + false)) {
                System.out.print("Error: Cannot get the house number!");
            }

        } catch (IOException | InterruptedException e) {
            System.out.println(
                    "Error encountered while trying to send a request for adding/deleting credits: "
                            + e.getLocalizedMessage());
        }

    }

    /**
     * This method sends a request to change credits.
     *
     * @param username - username of the users that has reported th product as expired
     * @param credits  - the number of credits to be added or subtracted.
     */

    public static void subtractCreditsWhenExpired(
            String username, float credits) {


        String url = "http://localhost:9102/splitCreditsExpired";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "?username=" + username
                        + "&credits=" + credits))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        try {
            HttpResponse<String> httpResponse = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString());

            if (httpResponse.body().equals("" + false)) {
                System.out.print("Error: Operation did not succeed!");
            }


        } catch (IOException | InterruptedException e) {
            System.out.println(
                    "Error encountered while trying to send a request for expired credits: "
                            + e.getLocalizedMessage());
        }


    }

    /**
     * Sending request for splitting the credits of a meal when users are eating together.
     *
     * @param usernames the list of usernames of the users eating together.
     * @param credits   credits to be split.
     */
    public static void sendRequestForSplittingCredits(
            List<String> usernames, float credits) {

        if (usernames == null || usernames.isEmpty()) {
            throw new InvalidParameterException("One of the parameters is null!");
        }

        String url = "http://localhost:9102/splitCredits";

        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonParse = mapper.writeValueAsString(usernames);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "?credits=" + credits))
                    .POST(
                            HttpRequest.BodyPublishers.ofString(jsonParse))
                    .header("Content-type", "application/json")
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Request sent successfully!");

            if (httpResponse.body().equals("" + false)) {
                System.out.print("Error: Operation did not succeed!");
                return;
            }
            System.out.println("Operation was successful!");

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * Get the list of a usernames from particular house.
     *
     * @param houseNr number of the house
     * @return the list of usernames
     */
    public static List<String> sendRequestForUsersOfHouse(int houseNr) {


        String url = "http://localhost:9102/getUsernamesByHouse";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "?houseNr=" + houseNr))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        try {
            HttpResponse<String> httpResponse = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Request sent successfully!");


            ObjectMapper mapper = new ObjectMapper();
            List<String> usernames = mapper.readValue(httpResponse.body(),
                    new TypeReference<List<String>>() {
                    });
            System.out.println("Operation was successful!");
            return usernames;

        } catch (IOException | InterruptedException e) {
            System.out.println(
                    "Error encountered while trying to send a request for expired credits: "
                            + e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * Request for getting the house number of a user, using the username.
     *
     * @param username username of the user which house number we will get
     * @return the number of the house
     */
    public static int sendRequestForHouseNumber(String username) {
        String url = "http://localhost:9102/getHouseByUsername";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "?username=" + username))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        try {
            HttpResponse<String> httpResponse = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Request sent successfully!");
            if (httpResponse.statusCode() == HttpStatus.BAD_REQUEST.value()) {
                System.out.println("The user does not have a house!");
                return -1;
            }
            ObjectMapper mapper = new ObjectMapper();
            int houseNumber = mapper.readValue(httpResponse.body(), new TypeReference<Integer>() {
            });
            System.out.println("Operation was successful!");
            return houseNumber;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /** Get credits of a user from the requests microservice.
     *
     * @param username The username of the user.
     * @param discoveryClient The Eureka discovery client
     *                        that should be autowired at a higher level.
     * @return The credits of the user returned from the requests microservice.
     */
    public static float getCredits(String username, EurekaClient discoveryClient) {
        // get the uri of the requests microservice from eureka
        InstanceInfo requestsInstance = discoveryClient.getNextServerFromEureka("REQUESTS", false);
        String requestsUri = requestsInstance.getHomePageUrl();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestsUri + "getCredits/" + username))
                .GET()
                .build();

        try {
            HttpResponse<String> httpResponse = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Request to get credits sent successfully!");

            if (httpResponse.body().equals("")) {
                System.out.print("Error: Operation did not succeed! Empty user.");
                return 0;
            }

            ObjectMapper mapper = new ObjectMapper();
            Float credits = mapper.readValue(httpResponse.body(), Float.class);
            System.out.println("Operation was successful! Total credits: "
                    + credits);

            return credits;

        } catch (IOException | InterruptedException e) {
            System.out.println(
                    "Error encountered while trying to send a request for user's credits: "
                            + e.getLocalizedMessage());
            return 0;
        }
    }

}
