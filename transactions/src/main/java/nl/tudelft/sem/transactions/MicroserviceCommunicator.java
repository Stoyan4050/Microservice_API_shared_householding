package nl.tudelft.sem.transactions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.InvalidParameterException;
import java.util.List;
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

            System.out.println("Request sent successfully!");

            if (httpResponse.body().equals("" + false)) {
                System.out.print("Error: Operation changing credits did not succeed!");
            }
            System.out.println("Operation was successful!");

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

            System.out.println("Subtract Request sent successfully!");

            if (httpResponse.body().equals("" + false)) {
                System.out.print("Error: Operation did not succeed!");
            }
            System.out.println("Subtract Operation was successful!");

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

            if (httpResponse.body().equals("" + false)) {
                System.out.print("Error: Operation did not succeed!");
            }

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

}
