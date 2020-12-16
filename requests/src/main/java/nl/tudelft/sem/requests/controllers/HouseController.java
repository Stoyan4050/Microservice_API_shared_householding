package nl.tudelft.sem.requests.controllers;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.requests.entities.House;
import nl.tudelft.sem.requests.repositories.HouseRepository;
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
 * The controller class for House.
 */
@Controller
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

    /** Deletes a house with a given houseNumber.
     *
     * @param houseNumber houseNumber of the house to delete from the database
     */
    @DeleteMapping("/deleteHouse/{houseNumber}")
    public void deleteHouse(@PathVariable int houseNumber) {
        houseRepository.deleteById(houseNumber);

    }
}
