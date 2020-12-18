package nl.tudelft.sem.requests.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.requests.config.Username;
import nl.tudelft.sem.requests.entities.House;
import nl.tudelft.sem.requests.entities.User;
import nl.tudelft.sem.requests.repositories.HouseRepository;
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
 * The controller class for House.
 */
@RestController
@SuppressWarnings("PMD")
public class HouseController {

    @Autowired
    private transient HouseRepository houseRepository;

    @Autowired
    private transient UserRepository userRepository;

    /**
     * Constructor for linking the house and user repositories used in membersAcceptingRequest
     * method of the RequestController.
     * Note: can be used in other classes/controllers.
     *
     * @param houseRepository - the house repository
     * @param userRepository  - the user repository
     */
    public HouseController(HouseRepository houseRepository, UserRepository userRepository) {
        this.houseRepository = houseRepository;
        this.userRepository = userRepository;
    }

    /**
     * Returns all houses from the database.
     *
     * @return a list of all houses
     */
    @GetMapping("/allHouses")
    @ResponseBody
    public List<House> getAllHouses() {
        return houseRepository.findAll();
    }

    /**
     * Returns a house with specified houseNumber.
     *
     * @param houseNumber houseNumber of the house to be returned
     */
    @GetMapping("/getHouse/{houseNumber}")
    @ResponseBody
    public Optional<House> getHouseByHouseNumber(@PathVariable int houseNumber,
                                                 @Username String username) {
        System.out.println(username);
        return houseRepository.findById(houseNumber);
    }

    /**
     * Returns all users that belong to this house.
     *
     * @param houseNumber house number of the house that we want the users from.
     * @return a list of users in this house
     */
    @GetMapping("/getUsersFromHouse/{houseNumber}")
    public List<User> getAllUsersFromHouse(@PathVariable int houseNumber) {
        Optional<House> house = houseRepository.findById(houseNumber);
        List<User> users = new ArrayList<>();
        if (house.isPresent()) {
            users.addAll(house.get().getUsers());
        }
        return users;
    }

    /**
     * Adds a new house to the database.
     *
     * @param house    house to be added
     * @param username username of the user creating the house
     */
    @PostMapping(value = "/addNewHouse", consumes = "application/json")
    //@RequestMapping(value = "/addNewHouse", method=RequestMethod.POST,
    //       consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
    public void addNewHouse(@RequestBody House house, @RequestParam("username") String username) {
        houseRepository.save(house);
        Optional<User> user = userRepository.findById(username);
        user.ifPresent(u -> u.setHouse(house));
        user.ifPresent(u -> userRepository.save(u));
    }

    /**
     * Updates a House, searched by the houseNr.
     *
     * @param houseWithNewInfo - the House containing new data
     * @return OK                    - the house was updated successfully
     *         NOT_FOUND             - the house was not found
     *         INTERNAL_SERVER_ERROR - the house couldn't be updated because of a server error
     */
    @PutMapping("/updateHouse")
    public ResponseEntity<House> updateHouse(@RequestBody House houseWithNewInfo) {
        Optional<House> house = houseRepository.findById(houseWithNewInfo.getHouseNr());

        if (house.isPresent()) {
            try {
                houseRepository.save(houseWithNewInfo);
            } catch (Exception e) {
                return new ResponseEntity("House couldn't be updated!",
                    HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return new ResponseEntity("House updated successfully!", HttpStatus.OK);
        }

        return new ResponseEntity("House not found!", HttpStatus.NOT_FOUND);
    }

    /**
     * Deletes a house with a given houseNumber.
     *
     * @param houseNumber houseNumber of the house to delete from the database
     */
    @DeleteMapping("/deleteHouse/{houseNumber}")
    public void deleteHouse(@PathVariable int houseNumber) {
        Optional<House> house = houseRepository.findById(houseNumber);
        if (house.isPresent()) {
            List<User> users = getAllUsersFromHouse(houseNumber);
            for (User user : users) {
                user.setHouse(null);
                userRepository.save(user);
            }
            houseRepository.deleteById(houseNumber);
            System.out.println("house successfully deleted");
        } else {
            System.out.println("house not found!");
        }
    }

    /**
     * User joining a house, once a request has been approved.
     *
     * @param username    - the username of the User entering the household
     * @param houseNumber - the house number of the House to add the user in
     */
    public void userJoiningHouse(String username, int houseNumber) {
        Optional<House> house = houseRepository.findById(houseNumber);
        Optional<User> user = userRepository.findById(username);

        // set the new house of the user
        user.get().setHouse(house.get());

        UserController userController = new UserController(userRepository);
        userController.updateUser(user.get());

        // add the user to the members
        house.get().getUsers().add(user.get());

        HouseController houseController = new HouseController(houseRepository, userRepository);
        houseController.updateHouse(house.get());

    }

    /**
     * User leaving a house.
     *
     * @param username    - the username of the User entering the household
     * @param houseNumber - the house number of the House to add the user in
     * @return OK        - the user was successfully remove from the household
     *         FORBIDDEN - the house number of the user is different from the one given
     *         NOT_FOUND - if the user or the house do not exist in the database
     */
    @PutMapping("/leaveHouse/{username}/{houseNumber}")
    public ResponseEntity<House> userLeavingHouse(@PathVariable String username,
                                                  @PathVariable int houseNumber) {
        Optional<House> house = houseRepository.findById(houseNumber);
        Optional<User> user = userRepository.findById(username);

        if (user.isPresent() && house.isPresent()) {
            if (user.get().getHouse() != null) {
                if (user.get().getHouse().getHouseNr() == houseNumber) {

                    user.get().setHouse(null);

                    userRepository.save(user.get());
                    if (house.get().getUsers().size() == 0) {
                        deleteHouse(houseNumber);
                    }

                    return new ResponseEntity("You successfully removed " + username
                        + " from house number " + houseNumber + "!", HttpStatus.OK);
                }

                return new ResponseEntity("You can not remove a user from a different household!",
                    HttpStatus.FORBIDDEN);
            }

            return new ResponseEntity("The user does not have a house!",
                HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity("The user or the house were not found, please check again!",
            HttpStatus.NOT_FOUND);
    }
}
