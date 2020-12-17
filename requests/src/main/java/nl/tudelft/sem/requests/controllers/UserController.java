package nl.tudelft.sem.requests.controllers;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.requests.entities.User;
import nl.tudelft.sem.requests.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
 * The controller class for User.
 */
@RestController
@SuppressWarnings("PMD")
public class UserController {

    @Autowired
    transient UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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
     */
    @PostMapping(value = "/addNewUser", consumes = "application/json")
    public void addNewUser(@RequestBody User user) {
        userRepository.save(user);
    }


    /**
     * Updates a User, searched by the username.
     *
     * @param userWithNewInfo - the User containing new data
     * @param username        - the name of the User that is going to be changed
     * @return OK                    - the user was updated successfully
     *         NOT_FOUND             - the user was not found
     *         INTERNAL_SERVER_ERROR - the user couldn't be updated because of a server error
     */
    @PutMapping("/updateUser/{username}")
    public ResponseEntity<User> updateUser(@RequestBody User userWithNewInfo,
                        @PathVariable String username) {
        Optional<User> user = userRepository.findById(username);

        if(user.isPresent()) {

            user.get().setHouse(userWithNewInfo.getHouse());
            user.get().setTotalCredits(userWithNewInfo.getTotalCredits());
            user.get().setEmail(userWithNewInfo.getEmail());
            user.get().setRequests(userWithNewInfo.getRequests());

            try {
                userRepository.save(user.get());
            } catch (Exception e) {
                return new ResponseEntity("User couldn't be updated!",
                    HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return new ResponseEntity("User updated successfully!", HttpStatus.OK);
        }

        return new ResponseEntity("User not found!", HttpStatus.NOT_FOUND);
    }

    /**
     * Deletes a user with a given username.
     *
     * @param username username of the user to delete from the database
     */
    @DeleteMapping("/deleteUser/{username}")
    public void deleteUser(@PathVariable String username) {
        Optional<User> user = userRepository.findById(username);
        if (user.isPresent()) {
            userRepository.deleteById(username);
            System.out.println("user successfully deleted");
        } else {
            System.out.println("user not found!");
        }
    }

    /**
     * A status message for the user representing whether or not they should buy groceries next.
     *
     * @param username - the username of the User
     * @return OK - if the userBalance > -50
     *         FORBIDDEN - if the userBalance <= -50
     */
    @GetMapping("/getCreditsStatusForGroceries/{username}")
    public ResponseEntity<User> getCreditsStatusForGroceries(@PathVariable String username) {
        Optional<User> user = userRepository.findById(username);

        if (user.isPresent()) {
            if (user.get().getTotalCredits() > -50) {
                return new ResponseEntity<>(HttpStatus.OK);
            }

            return new ResponseEntity("Your credits are less than -50! You should buy groceries.",
                HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**Method For changing the credits of a user.
    *
    * @param username Username of the user which credits will be changed
    * @param credits credits that will be added or subtracted from a user
    * @return true if the credits were changed
    */
    @PostMapping("/editUserCredits")
    public @ResponseBody
    boolean editUserCredits(@RequestParam String username,
                             @RequestParam float credits,
                             @RequestParam boolean add) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request
        if (!add) {
            credits = credits * (-1);
        }
    
        User currentUser = userRepository.getOne(username);
        try {
            if (userRepository.updateUserCredits(currentUser.getHouse().getHouseNr(),
                    currentUser.getEmail(),
                    currentUser.getTotalCredits() + credits,
                    currentUser.getUsername()) == 1) { //NOPMD
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
