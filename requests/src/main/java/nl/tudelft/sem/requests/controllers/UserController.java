package nl.tudelft.sem.requests.controllers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.requests.config.Username;
import nl.tudelft.sem.requests.entities.User;
import nl.tudelft.sem.requests.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
     * Returns the credits of a user with given username.
     *
     * @param username username of the user
     * @return User's credits that were found with that username in the database
     */
    @GetMapping("/getCredits/{username}")
    @ResponseBody
    public Optional<Float> getCreditsByUsername(@PathVariable String username) {
        Optional<User> user = userRepository.findById(username);
        return user.map(User::getTotalCredits);
    }

    /**
     * Adds a new user to the database.
     *
     * @param user user to be added
     */
    @PostMapping("/addNewUser")
    public ResponseEntity<?> addNewUser(@RequestBody User user) {
        userRepository.save(user);
        return ResponseEntity.created(URI.create("/addNewUser")).build();
    }


    /**
     * Updates a User, searched by the username.
     *
     * @param userWithNewInfo - the User containing new data
     * @return OK                    - the user was updated successfully
     *         NOT_FOUND             - the user was not found
     *         INTERNAL_SERVER_ERROR - the user couldn't be updated because of a server error
     */
    @PutMapping("/updateUser")
    public ResponseEntity<String> updateUser(@RequestBody User userWithNewInfo,
                                             @Username String username) {
        Optional<User> user = userRepository.findById(username);

        if (user.isPresent()) {
            try {
                userRepository.save(userWithNewInfo);
            } catch (Exception e) {
                return new ResponseEntity<>("User couldn't be updated!",
                    HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return new ResponseEntity<>("User updated successfully!", HttpStatus.OK);
        }

        return new ResponseEntity<>("User not found!", HttpStatus.NOT_FOUND);
    }

    /**
     * Deletes a user with a given username.
     *
     * @param username username of the user to delete from the database
     */
    @DeleteMapping("/deleteUser")
    public void deleteUser(@Username String username) {
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
    @GetMapping("/getCreditsStatusForGroceries")
    public ResponseEntity<String> getCreditsStatusForGroceries(@Username String username) {
        Optional<User> user = userRepository.findById(username);

        if (user.isPresent()) {
            if (user.get().getTotalCredits() > -50) {
                return new ResponseEntity<>(HttpStatus.OK);
            }

            return new ResponseEntity<>("Your credits are less than -50! You should buy groceries.",
                HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Method For changing the credits of a user.
     *
     * @param username Username of the user which credits will be changed
     * @param credits  credits that will be added or subtracted from a user
     * @return true if the credits were changed
     */
    @PostMapping("/editUserCredits")
    public @ResponseBody
    ResponseEntity<?> editUserCredits(@RequestParam String username,
                                      @RequestParam float credits,
                                      @RequestParam boolean add) {
        if (!add) {
            credits = credits * (-1);
        }

        User currentUser = userRepository.findByUsername(username);

        try {
            if (userRepository.updateUserCredits(currentUser.getHouse().getHouseNr(),
                currentUser.getEmail(),
                currentUser.getTotalCredits() + credits,
                currentUser.getUsername()) == 1) {
                return ResponseEntity.created(URI.create("/editUserCredits")).build();
            }

            return ResponseEntity.badRequest().build();
        } catch (Exception e) {

            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Method for splitting credits, when users are eating together.
     *
     * @param usernames usernames of the users eating together*
     * @param credits   amount of credits to be split
     * @return true if the credits were subtracted from each user
     */
    @PostMapping("/splitCredits")
    public @ResponseBody
    boolean splitUserCredits(@RequestBody List<String> usernames, @RequestParam float credits) {

        List<User> users = new ArrayList<>();
        for (String username : usernames) {
            users.add(userRepository.findByUsername(username));
        }

        for (User user : users) {
            float currentCredits = user.getTotalCredits();
            currentCredits = currentCredits - credits;
            user.setTotalCredits(currentCredits);

            try {
                userRepository.updateUserCredits(user.getHouse().getHouseNr(),
                    user.getEmail(),
                    currentCredits,
                    user.getUsername());
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
}
