package nl.tudelft.sem.transactions;

import com.fasterxml.jackson.core.JsonProcessingException;
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
     * @param credits - the number of credits to be added or subtracted.
     * @param add - true if we want to add credits, false if we subtract
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
	    	System.out.print("Error: Operation did not succeed!");
	    }
	    System.out.println("Operation was successful!");
		
	} catch (IOException | InterruptedException e) {
	    System.out.println(
		    "Error encountered while trying to send a request for adding credits: "
								   + e.getLocalizedMessage());
	}
	
    }

    /**
	 * Sending request for splitting the credits of a meal when users are eating together.
	 *
     * @param usernames the list of usernames of the users eating together.
	 *
     * @param credits credits to be split.
     */
    public static void sendRequestForSplittingCredits(
		List<String> usernames, float credits) {
		
        if (usernames == null || usernames.isEmpty()) {
            throw new InvalidParameterException("One of the parameters is null!");
	}
	
	String url = "http://localhost:9102/editUserCredits";
	
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
	
}
