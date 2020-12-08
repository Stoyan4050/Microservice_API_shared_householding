package nl.tudelft.sem.requests.controllers;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.requests.entities.House;
import nl.tudelft.sem.requests.entities.Request;
import nl.tudelft.sem.requests.entities.RequestId;
import nl.tudelft.sem.requests.repositories.HouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * The controller class for House.
 */
@Controller
@SuppressWarnings("PMD")
//@RequestMapping(path="/house")
public class HouseController {

    @Autowired
    private transient HouseRepository houseRepository;

    /** Returns all houses from the database.
     *
     * @return a list of all houses
     */
    @GetMapping("/allHouses")
    @ResponseBody
    public List<House> getAllHouses() {
        return houseRepository.findAll();
    }

    /** Returns a house with specified houseNumber.
     *
     * @param houseNumber houseNumber of the house to be returned
     * @return House that was found with that houseNumber in the database
     */
    @GetMapping("/getHouse/{houseNumber)")
    @ResponseBody
    public Optional<House> getHouseByHouseNumber(@PathVariable int houseNumber) {
        return houseRepository.findById(houseNumber);
    }

    /** Adds a new house to the database.
     *
     * @param house house to be added
     * @return true if house was successfully added, false otherwise
     */
    @PostMapping("/addNewHouse")
    boolean addNewHouse(@RequestBody House house) {
        try {
            houseRepository.save(house);
            return true;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
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

        if(house.isPresent()) {

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

    /** Deletes a house with a given houseNumber.
     *
     * @param houseNumber houseNumber of the house to delete from the database
     */
    @DeleteMapping("/deleteHouse/{houseNumber}")
    public void deleteHouse(@PathVariable int houseNumber) {
        houseRepository.deleteById(houseNumber);

    }

}
