package nl.tudelft.sem.requests.controllers;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.requests.config.Username;
import nl.tudelft.sem.requests.entities.Request;
import nl.tudelft.sem.requests.entities.RequestId;
import nl.tudelft.sem.requests.helpers.AcceptUserHelper;
import nl.tudelft.sem.requests.repositories.HouseRepository;
import nl.tudelft.sem.requests.repositories.RequestRepository;
import nl.tudelft.sem.requests.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * The controller class for Request.
 */
@RestController
@SuppressWarnings("PMD")
public class RequestController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private transient RequestRepository requestRepository;

    @Autowired
    private transient HouseRepository houseRepository;


    /**
     * Get all requests from the database.
     *
     * @return The List containing all requests in the database
     */
    @GetMapping("allRequests")
    @ResponseBody
    public List<Request> getAllRequests() {
        return requestRepository.findAll();
    }

    /**
     * Get one request from the database identified by the ID.
     *
     * @param requestId - the ID of the request to return
     * @return The request corresponding with that ID
     */
    @PostMapping("/getRequest")
    @ResponseBody
    public ResponseEntity<Request> getRequestById(@RequestBody RequestId requestId) {
        return requestRepository.findById(requestId)
            .map(request -> ResponseEntity.ok().body(request))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Create or update a request in the database.
     *
     * @param newRequest - the Request to add to the database
     */
    @PostMapping("/addNewRequest")
    public ResponseEntity<?> addRequest(@RequestBody Request newRequest) {
        return ResponseEntity.created(URI.create("/addNewRequest"))
            .body(requestRepository.save(newRequest));
    }

    /**
     * Updates a Request, searched by the requestId.
     *
     * @param requestWithNewInfo - the Request containing new data
     * @return OK                    - the request was updated successfully
     *         NOT_FOUND             - the request was not found
     *         INTERNAL_SERVER_ERROR - the request couldn't be updated because of a server error
     */
    @PutMapping("/updateRequest")
    public ResponseEntity<String> updateRequest(@RequestBody Request requestWithNewInfo) {
        Optional<Request> request = requestRepository.findById(requestWithNewInfo.getId());

        if (request.isPresent()) {
            try {
                requestRepository.save(requestWithNewInfo);
            } catch (Exception e) {
                return new ResponseEntity<>("Request couldn't be updated!",
                    HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return new ResponseEntity<>("Request updated successfully!", HttpStatus.OK);
        }

        return new ResponseEntity<>("Request not found!", HttpStatus.NOT_FOUND);
    }

    /**
     * Delete a request from the database identified by the ID.
     *
     * @param requestId - the ID of the request to delete from the database
     */
    @DeleteMapping("/deleteRequest")
    @ResponseBody
    public ResponseEntity<?> deleteRequest(@RequestBody RequestId requestId) {
        requestRepository.deleteById(requestId);
        return ResponseEntity.ok().build();
    }

    /**
     * One of the members accepting a request from a user to join their household.
     *
     * @return NOT_FOUND - if the user/request was not found
     *         FORBIDDEN - if the request house does not coincide with the user house
     *         OK        - if the user was successfully updated
     */
    @PostMapping("/membersAcceptedRequest")
    public ResponseEntity<String> membersAcceptingRequest(
        @RequestParam(name = "username") String username,
        @RequestParam(name = "houseNumber") int houseNumber,
        @Username String myUsername) {

        RequestId id = new RequestId(houseNumber, username);

        Optional<Request> currentRequest = requestRepository.findById(id);

        if (!currentRequest.isPresent()) {
            return new ResponseEntity<>("The request is not found!", HttpStatus.NOT_FOUND);
        }

        AcceptUserHelper helper = new AcceptUserHelper(userRepository, houseRepository);
        // get the response entity indicating if the user was eligible
        // for accepting another user to the house with that house number
        ResponseEntity<String> response = helper.acceptUser(username, houseNumber, myUsername);

        if (response.getStatusCode() == HttpStatus.OK) {
            //set the field of the request from false to true
            currentRequest.get().setApproved(true);
            updateRequest(currentRequest.get());
        }
        return response;
    }
}