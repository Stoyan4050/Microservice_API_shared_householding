package nl.tudelft.sem.transactions;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.InvalidParameterException;
import org.springframework.stereotype.Service;

@Service
public class MicroserviceCommunicator {
	
	private static final HttpClient httpClient = HttpClient.newBuilder().build();


	/**
	 * This method sends a request to change credits.
	 *
	 * @param username - username of the user whose credits need to be changed
	 * @param credits - the number of credits to be added or substracted.
	 * @param add -
	 */
	public static void sendRequestForChangingCredits(String username,
													 float credits, boolean add) {
		
		if (username == null) {
			throw new InvalidParameterException("One of the parameters is null!");
		}
		
		String url = "http://localhost:9102/editUserCredits";
		
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url + "&username=" + username + "&credits=" + credits + "&add=" + add))
				.POST(HttpRequest.BodyPublishers.noBody())
				.build();
		
		try {
			HttpResponse<String> httpResponse= httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			
			System.out.println("Request sent successfully!");
			
			if (httpResponse.body().equals("" + false)) {
				System.out.print("Error: Operation did not succeed!");
			}
			System.out.println("Operation was successful!");
			
		} catch (IOException | InterruptedException e) {
			System.out.println("Error encountered while trying to send a request for adding credits: "
								+ e.getLocalizedMessage());
		}

	}
	
	
}
