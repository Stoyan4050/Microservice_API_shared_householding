package nl.tudelft.sem.template.requests.controllers;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.requests.entities.Request;
import nl.tudelft.sem.template.requests.entities.RequestId;
import nl.tudelft.sem.template.requests.repositories.RequestRepository;
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
public class RequestController {

    @Autowired
    private transient RequestRepository repository;

    /**
     * Get all requests from the database.
     *
     * @return The List containing all requests in the database
     */
    @GetMapping("allRequests")
    @ResponseBody
    public List<Request> getAllRequests() {
        return repository.findAll();
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
        return repository.findById(requestId);
    }

    /**
     * Create or update a request in the database.
     *
     * @param newRequest The Request to add to the database
     */
    @PutMapping("addNewRequest")
    @ResponseBody
    public void addRequest(@RequestBody Request newRequest) {
        repository.save(newRequest);
    }

    /**
     * Delete a request from the database identified by the ID.
     *
     * @param requestId The ID of the request to delete from the database
     */
    @DeleteMapping("deleteRequest/{requestId}")
    @ResponseBody
    public void removeRequest(@PathVariable RequestId requestId) {
        repository.deleteById(requestId);
    }

}