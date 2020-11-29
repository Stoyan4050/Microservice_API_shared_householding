package nl.tudelft.sem.template.controllers;

import java.util.List;
import nl.tudelft.sem.template.entities.User;
import nl.tudelft.sem.template.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
//@RequestMapping(path="/house")
public class UserController {
    @Autowired
    transient UserRepository userRepository;

    @GetMapping("/allUsers")
    public @ResponseBody
    List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /** Adds a new user to the database.
     *
     * @param user user to be added
     * @return true if user is successfully added, false otherwis
     */
    @PostMapping("/addNewUser")
    public @ResponseBody
    boolean addNewUser(@RequestBody User user) {
        try {
            userRepository.save(user);
            return true;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }
}
