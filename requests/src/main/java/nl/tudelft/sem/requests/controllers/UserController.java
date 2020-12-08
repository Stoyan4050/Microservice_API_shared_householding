package nl.tudelft.sem.requests.controllers;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.requests.entities.User;
import nl.tudelft.sem.requests.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * The controller class for User.
 */
@Controller
@SuppressWarnings("PMD")
//@RequestMapping(path="/house")
public class UserController {
    @Autowired
    transient UserRepository userRepository;

    /**
     * Returns all users from the database.
     *
     * @return A list of all users
     */
    @GetMapping("/allUsers")
    @ResponseBody
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Returns a user with given username.
     *
     * @param username username of the user to be found
     * @return User that was found with that username in the database
     */
    @GetMapping("/getUser/{username}")
    @ResponseBody
    public Optional<User> getUserByUsername(@PathVariable String username) {
        return userRepository.findById(username);
    }

    /**
     * Adds a new user to the database.
     *
     * @param user user to be added
     * @return true if user was successfully added, false otherwise
     */
    @PostMapping("/addNewUser")
    public boolean addNewUser(@RequestBody User user) {
        try {
            userRepository.save(user);
            return true;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }

    /*
    /**
     * Updates a User, searched by the username. - With HTTP response
     *
     * @param userWithNewInfo - the User containing new data
     * @param username        - the name of the User that is going to be changed
     * @return a response entity
     */
    /*
    @PutMapping("/updateUser/{username}")
    public ResponseEntity<User> updateUser(@RequestBody User userWithNewInfo,
                        @PathVariable String username) {
        Optional<User> user = userRepository.findById(username);

        if(user.isPresent()) {

            user.get().setHouse(userWithNewInfo.getHouse());
            user.get().setTotalCredits(userWithNewInfo.getTotalCredits());
            user.get().setEmail(userWithNewInfo.getEmail());
            user.get().setRequests(userWithNewInfo.getRequests());

            User newUser;
            try {
                newUser = userRepository.save(user.get());
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }

            return new ResponseEntity<>(newUser, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    */

    /**
     * Updates a User, searched by the username. - Without HTTP response
     *
     * @param userWithNewInfo - the User containing new data
     * @param username        - the name of the User that is going to be changed
     * @return status if the update was successful or not
     */
    @PutMapping("/updateUser/{username}")
    public String updateUser(@RequestBody User userWithNewInfo, @PathVariable String username) {
        Optional<User> user = userRepository.findById(username);

        if(user.isPresent()) {

            user.get().setHouse(userWithNewInfo.getHouse());
            user.get().setTotalCredits(userWithNewInfo.getTotalCredits());
            user.get().setEmail(userWithNewInfo.getEmail());
            user.get().setRequests(userWithNewInfo.getRequests());

            User newUser;
            try {
                newUser = userRepository.save(user.get());
            } catch (Exception e) {
                return "User couldn't be updated!";
            }

            return "User updated successfully!";
        }

        return "User not found!";
    }

    /**
     * Deletes a user with a given username.
     *
     * @param username username of the user to delete from the database
     */
    @DeleteMapping("/deleteUser/{username}")
    public void deleteUser(@PathVariable String username) {
        userRepository.deleteById(username);
    }

}
