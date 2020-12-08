package nl.tudelft.sem.requests.controllers;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.requests.entities.Request;
import nl.tudelft.sem.requests.entities.RequestId;
import nl.tudelft.sem.requests.entities.User;
import nl.tudelft.sem.requests.repositories.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * The controller class for Request.
 */
@Controller
@SuppressWarnings("PMD")
public class RequestController {

    @Autowired
    private transient RequestRepository requestRepository;

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
     * @param requestId The ID of the request to return
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
     * @param newRequest The Request to add to the database
     */
    @PutMapping("addNewRequest")
    @ResponseBody
    public void addRequest(@RequestBody Request newRequest) {
        requestRepository.save(newRequest);
    }

    /**
     * Updates a Request, searched by the requestId. - Without HTTP response
     *
     * @param requestWithNewInfo - the Request containing new data
     * @param requestId          - the requestId of the Request that is going to be changed
     * @return status if the update was successful or not
     */
    @PutMapping("/updateRequest/{requestId}")
    public String updateRequest(@RequestBody Request requestWithNewInfo,
                                @PathVariable RequestId requestId) {
        Optional<Request> request = requestRepository.findById(requestId);

        if(request.isPresent()) {
            request.get().setId(requestWithNewInfo.getId());
            request.get().setHouse(requestWithNewInfo.getHouse());
            request.get().setUser(requestWithNewInfo.getUser());
            request.get().setApproved(requestWithNewInfo.isApproved());

            Request newRequest;
            try {
                newRequest = requestRepository.save(request.get());
            } catch (Exception e) {
                return "Request couldn't be updated!";
            }

            return "Request updated successfully!";
        }

        return "Request not found!";
    }

    /**
     * Delete a request from the database identified by the ID.
     *
     * @param requestId The ID of the request to delete from the database
     */
    @DeleteMapping("deleteRequest/{requestId}")
    @ResponseBody
    public void removeRequest(@PathVariable RequestId requestId) {
        requestRepository.deleteById(requestId);
    }

}