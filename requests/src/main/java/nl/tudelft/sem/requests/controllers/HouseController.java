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
    @PostMapping("/addNewHouse")
    //@RequestMapping(value = "/addNewHouse", method=RequestMethod.POST,
    //       consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
    public void addNewHouse(@RequestBody House house, @Username String username) {
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
    public String updateHouse(@RequestBody House houseWithNewInfo, @PathVariable int houseNr) {
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
        if (house.isPresent()) {
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
}

