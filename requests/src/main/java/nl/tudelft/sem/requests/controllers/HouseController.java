package nl.tudelft.sem.requests.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.requests.entities.House;
import nl.tudelft.sem.requests.entities.Request;
import nl.tudelft.sem.requests.entities.RequestId;
import nl.tudelft.sem.requests.entities.User;
import nl.tudelft.sem.requests.repositories.HouseRepository;
import nl.tudelft.sem.requests.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
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
 * The controller class for House.
 */
@RestController
@SuppressWarnings("PMD")
public class HouseController {

    @Autowired
    private transient HouseRepository houseRepository;

    @Autowired
    private transient UserRepository userRepository;

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
    public Optional<House> getHouseByHouseNumber(@PathVariable int houseNumber) {
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
     * Updates a Request, searched by the houseNr. - Without HTTP response
     *
     * @param houseWithNewInfo - the Request containing new data
     * @param houseNr          - the requestId of the Request that is going to be changed
     * @return status if the update was successful or not
     */
    @PutMapping("/updateHouse/{houseNr}")
    public String updateRequest(@RequestBody House houseWithNewInfo, @PathVariable int houseNr) {
        Optional<House> house = houseRepository.findById(houseNr);

        if (house.isPresent()) {
            house.get().setHouseNr(houseWithNewInfo.getHouseNr());
            house.get().setName(houseWithNewInfo.getName());
            house.get().setRequests(houseWithNewInfo.getRequests());
            house.get().setUsers(houseWithNewInfo.getUsers());

            House newHouse;
            try {
                newHouse = houseRepository.save(house.get());
            } catch (Exception e) {
                return "House couldn't be updated!";
            }

            return "House updated successfully!";
        }

        return "House not found!";
    }

    /**
     * Deletes a house with a given houseNumber.
     *
     * @param houseNumber houseNumber of the house to delete from the database
     */
    @DeleteMapping("/deleteHouse/{houseNumber}")
    public void deleteHouse(@PathVariable int houseNumber) {
        Optional<House> house = houseRepository.findById(houseNumber);
        if(house.isPresent()) {
            houseRepository.deleteById(houseNumber);
            System.out.println("house successfully deleted");
        }
        System.out.println("house not found!");
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

        user.get().setHouse(house.get());

        UserController userController = new UserController(userRepository);
        userController.updateUser(user.get(), user.get().getUsername());

    }
}
