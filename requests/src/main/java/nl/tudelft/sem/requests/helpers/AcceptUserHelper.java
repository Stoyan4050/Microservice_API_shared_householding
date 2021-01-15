package nl.tudelft.sem.requests.helpers;

import java.util.Optional;
import nl.tudelft.sem.requests.controllers.HouseController;
import nl.tudelft.sem.requests.entities.User;
import nl.tudelft.sem.requests.repositories.HouseRepository;
import nl.tudelft.sem.requests.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AcceptUserHelper {

    @Autowired
    UserRepository userRepository;

    @Autowired
    HouseRepository houseRepository;

    public AcceptUserHelper(UserRepository userRepository, HouseRepository houseRepository) {
        this.userRepository = userRepository;
        this.houseRepository = houseRepository;
    }

    /** Checks if the user accepting the request of another user is allowed to do so
     * (his username is correct and he's from the house that another user makes the request to).
     *
     * @param username username of the user making the request
     * @param houseNumber house number of the house which the user wants to join
     * @param myUsername username of the user accepting the request
     * @return an appropriate ResponseEntity
     */
    public ResponseEntity<String> acceptUser(String username, int houseNumber, String myUsername) {
        Optional<User> currentUser = userRepository.findById(myUsername);

        if (!currentUser.isPresent()) {
            return new ResponseEntity<>("The user is not found!", HttpStatus.NOT_FOUND);
        }

        if (currentUser.get().getHouse() == null
                || currentUser.get().getHouse().getHouseNr() != houseNumber) {
            return new ResponseEntity<>("You can't accept a user from other household!",
                    HttpStatus.FORBIDDEN);
        }


        //create an instance of house controller - constructor defined in HouseController
        HouseController houseController = new HouseController(houseRepository, userRepository);

        //method userJoiningHouse of HouseController -> setting the house of the new user
        houseController.userJoiningHouse(username, houseNumber);


        return new ResponseEntity<>("You have successfully accepted the user: "
                + username, HttpStatus.OK);
    }
}
