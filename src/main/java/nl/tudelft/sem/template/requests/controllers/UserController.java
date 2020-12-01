package nl.tudelft.sem.template.requests.controllers;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.requests.entities.User;
import nl.tudelft.sem.template.requests.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * The controller class for User.
 */
@Controller
//@RequestMapping(path="/house")
public class UserController {
    @Autowired
    transient UserRepository userRepository;

    /** Returns all users from the database.
     *
     * @return A list of all users
     */
    @GetMapping("/allUsers")
    @ResponseBody
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /** Returns a user with given username.
     *
     * @param username username of the user to be found
     * @return User that was found with that username in the database
     */
    @GetMapping("/getUser/{username}")
    @ResponseBody
    public Optional<User> getUserByUsername(@PathVariable String username) {
        return userRepository.findById(username);
    }

    /** Adds a new user to the database.
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

    /** Deletes a user with a given username.
     *
     * @param username username of the user to delete from the database
     */
    @DeleteMapping("/deleteUser/{username}")
    public void deleteUser(@PathVariable String username) {
        userRepository.deleteById(username);
    }

}
