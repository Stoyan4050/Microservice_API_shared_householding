package nl.tudelft.sem.requests.controllers;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.requests.entities.House;
import nl.tudelft.sem.requests.entities.Request;
import nl.tudelft.sem.requests.entities.RequestId;
import nl.tudelft.sem.requests.entities.User;
import nl.tudelft.sem.requests.repositories.HouseRepository;
import nl.tudelft.sem.requests.repositories.RequestRepository;
import nl.tudelft.sem.requests.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    @GetMapping("getRequest/{requestId}")
    @ResponseBody
    public Optional<Request> getRequestById(@PathVariable RequestId requestId) {
        return requestRepository.findById(requestId);
    }

    /**
     * Create or update a request in the database.
     *
     * @param newRequest - the Request to add to the database
     */
    @PostMapping(value = "/addNewRequest", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addRequest(@RequestBody Request newRequest) {
        requestRepository.save(newRequest);
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
    public ResponseEntity<Request> updateRequest(@RequestBody Request requestWithNewInfo) {
        Optional<Request> request = requestRepository.findById(requestWithNewInfo.getId());

        if (request.isPresent()) {
            try {
                requestRepository.save(requestWithNewInfo);
            } catch (Exception e) {
                return new ResponseEntity("Request couldn't be updated!",
                    HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return new ResponseEntity("Request updated successfully!", HttpStatus.OK);
        }

        return new ResponseEntity("Request not found!", HttpStatus.NOT_FOUND);
    }

    /**
     * Delete a request from the database identified by the ID.
     *
     * @param requestId - the ID of the request to delete from the database
     */
    @DeleteMapping("deleteRequest/{requestId}")
    @ResponseBody
    public void deleteRequest(@PathVariable RequestId requestId) {
        requestRepository.deleteById(requestId);
    }

    /**
     * One of the members accepting a request from a user to join their household.
     *
     * @return NOT_FOUND - if the user/request was not found
     *         FORBIDDEN - if the request house does not coincide with the user house
     *         OK        - if the user was successfully updated
     */
    @PostMapping("/membersAcceptedRequest")
    public ResponseEntity<Request> membersAcceptingRequest(
                    @RequestParam(name = "username") String username,
                    @RequestParam(name = "houseNumber") int houseNumber,
                    @RequestParam(name = "myUsername") String myUsername) {

        RequestId id = new RequestId(houseNumber, username);

        UserController userController = new UserController(userRepository);


        if (!requestRepository.existsById(id)) {
            return new ResponseEntity("The request is not found!", HttpStatus.NOT_FOUND);
        }

        Optional<User> currentUser = userController.getUserByUsername(myUsername);

        if (!currentUser.isPresent()) {
            return new ResponseEntity("The user is not found!", HttpStatus.NOT_FOUND);
        }

        if (currentUser.get().getHouse().getHouseNr() != houseNumber) {
            return new ResponseEntity("You can't accept a user from other household!",
                    HttpStatus.FORBIDDEN);
        }

        Optional<Request> currentRequest = requestRepository.findById(id);

        //set the field of the request from false to true
        currentRequest.get().setApproved(true);

        //create an instance of house controller - constructor defined in HouseController
        HouseController houseController = new HouseController(houseRepository, userRepository);

        //method userJoiningHouse of HouseController -> setting the house of the new user
        houseController.userJoiningHouse(username, houseNumber);

        updateRequest(currentRequest.get());

        return new ResponseEntity("You have successfully accepted the user: "
                + currentRequest.get().getUser().getUsername(), HttpStatus.OK);
    }

}