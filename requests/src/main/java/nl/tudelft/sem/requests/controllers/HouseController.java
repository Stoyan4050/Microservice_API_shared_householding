package nl.tudelft.sem.requests.controllers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    public ResponseEntity<House> getHouseByHouseNumber(@PathVariable int houseNumber) {
        Optional<House> house = houseRepository.findById(houseNumber);
        return house.map(value -> ResponseEntity.ok().body(value))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Returns all users that belong to this house.
     *
     * @param houseNumber house number of the house that we want the users from.
     * @return a list of users in this house
     */
    @GetMapping("/getUsersFromHouse/{houseNumber}")
    public ResponseEntity<List<User>> getAllUsersFromHouse(@PathVariable int houseNumber) {
        Optional<House> house = houseRepository.findById(houseNumber);
        return house.map(value -> ResponseEntity.ok().body(List.copyOf(value.getUsers())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Adds a new house to the database.
     *
     * @param house    house to be added
     * @param username username of the user creating the house
     */
    @PostMapping("/addNewHouse")
    //@RequestMapping(value = "/addNewHouse", method=RequestMethod.POST,
    //       consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addNewHouse(@RequestBody House house, @Username String username) {
        Optional<User> user = userRepository.findById(username);
        return user.map(u -> {
            houseRepository.save(house);
            u.setHouse(house);
            userRepository.save(u);
            return ResponseEntity.created(URI.create("/addNewHouse")).build();
        }).orElseGet(() ->
                ResponseEntity.badRequest().body("User is not present in the database."));
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
    public ResponseEntity<String> updateHouse(@RequestBody House houseWithNewInfo) {
        Optional<House> house = houseRepository.findById(houseWithNewInfo.getHouseNr());

        if (house.isPresent()) {
            try {
                // update the house
                houseRepository.save(houseWithNewInfo);
            } catch (Exception e) {
                return new ResponseEntity<>("House couldn't be updated!",
                    HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return new ResponseEntity<>("House updated successfully!", HttpStatus.OK);
        }

        return new ResponseEntity<>("House not found!", HttpStatus.NOT_FOUND);
    }

    /**
     * Deletes a house with a given houseNumber.
     *
     * @param houseNumber houseNumber of the house to delete from the database
     */
    @DeleteMapping("/deleteHouse/{houseNumber}")
    public ResponseEntity<?> deleteHouse(@PathVariable int houseNumber) {
        Optional<House> house = houseRepository.findById(houseNumber);
        if (house.isPresent()) {
            ResponseEntity<List<User>> usersResponse = getAllUsersFromHouse(houseNumber);
            if (usersResponse.getStatusCode() != HttpStatus.OK) {
                return ResponseEntity.badRequest().body("Invalid house number.");
            }
            List<User> users = usersResponse.getBody();
            assert users != null;
            for (User user : users) {
                user.setHouse(null);
                userRepository.save(user);
            }
            houseRepository.deleteById(houseNumber);
            return ResponseEntity.ok().body("House successfully deleted.");
        } else {
            return ResponseEntity.notFound().build();
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
        userController.updateUser(user.get(), user.get().getUsername());
        // userController.updateUser(user.get());

        // // add the user to the members
        // house.get().getUsers().add(user.get());

        // HouseController houseController = new HouseController(houseRepository, userRepository);
        // houseController.updateHouse(house.get());
        
    }
    
    /**
     * Method for subtracting credits, when products is expired.
     *
     * @param username username of the user that has reported the product as expired
     * @param credits  amount of credits to be split
     * @return true if the credits were subtracted from each user
     */
    @PostMapping("/splitCreditsExpired")
    public @ResponseBody
    ResponseEntity<?> splitCreditsWhenExpired(@RequestParam String username,
                                              @RequestParam float credits) {
        
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request
        
        
        User currentUser = userRepository.findByUsername(username);
        
        House house = currentUser.getHouse();
        
        Set<User> users = house.getUsers();
        
        if (users == null || users.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        credits = credits / users.size();
        
        try {
            for (User user : users) {
                if (userRepository.updateUserCredits(user.getHouse().getHouseNr(),
                        user.getEmail(),
                        user.getTotalCredits() - credits,
                        user.getUsername()) == 0) { //NOPMD
                    return ResponseEntity.badRequest().build();
                }
                
            }
            return ResponseEntity.created(URI.create("/editUserCredits")).build();
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**Get all products from a fridge of the house.
     *
     *@param houseNr the number of the house which products from the fridge will be displayed
     *@return all products from the fridge
     */
    @PostMapping("/getUsernamesByHouse")
    public @ResponseBody
    ResponseEntity<?> getUsernamesByHouse(@RequestParam int houseNr) {
        
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request
        
        House house = houseRepository.findByHouseNr(houseNr);
        Set<User> users = house.getUsers();
        
        
        if (users == null || users.isEmpty() || house == null) {
            return ResponseEntity.badRequest().build();
        }
        
        List<String> usernames = new ArrayList<>();
        for (User user : users) {
            usernames.add(user.getUsername());
        }
        
        try {
            return ResponseEntity.created(URI.create("/getUsernamesByHouse")).body(usernames);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

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
    @PutMapping("/leaveHouse/{houseNumber}")
    public ResponseEntity<String> userLeavingHouse(@Username String username,
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

                    return new ResponseEntity<>("You successfully removed " + username
                        + " from house number " + houseNumber + "!", HttpStatus.OK);
                }

                return new ResponseEntity<>("You can not remove a user from a different household!",
                    HttpStatus.FORBIDDEN);
            }

            return new ResponseEntity<>("The user does not have a house!",
                HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>("The user or the house were not found, please check again!",
            HttpStatus.NOT_FOUND);
    }
}

