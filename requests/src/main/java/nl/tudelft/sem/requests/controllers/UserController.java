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
     * Adds a new user to the database.
     *
     * @param user user to be added
     */
    @PostMapping("/addNewUser")
    public ResponseEntity<?> addNewUser(@RequestBody User user) {
        userRepository.save(user);
        return ResponseEntity.created(URI.create("/addNewUser")).build();
    }

    // TODO - choose the update method returning String or ResponseEntity (useful for tests)
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
    @PutMapping("/updateUser")
    public String updateUser(@RequestBody User userWithNewInfo, @Username String username) {
        Optional<User> user = userRepository.findById(username);

        if (user.isPresent()) {
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
    public ResponseEntity<User> getCreditsStatusForGroceries(@Username String username) {
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
    ResponseEntity<?> editUserCredits(@RequestParam String username,
                             @RequestParam float credits,
                             @RequestParam boolean add) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request
        if (!add) {
            credits = credits * (-1);
        }
    
        User currentUser = userRepository.findByUsername(username);
        System.out.println(currentUser.toString());
        try {
            if (userRepository.updateUserCredits(currentUser.getHouse().getHouseNr(),
                    currentUser.getEmail(),
                    currentUser.getTotalCredits() + credits,
                    currentUser.getUsername()) == 1) { //NOPMD
                //return ResponseEntity.created(URI.create("/editUserCredits")).build();
                ResponseEntity.created(URI.create("/editUserCredits")).body(15.f);
            }
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**Method for splitting credits, when users are eating together.
     *
     * @param usernames usernames of the users eating together*
     * @param credits amount of credits to be split
     *
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
                if (userRepository.updateUserCredits(user.getHouse().getHouseNr(),
                        user.getEmail(),
                        currentCredits,
                        user.getUsername()) == 1) { //NOPMD
                    continue;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }
}
