package nl.tudelft.sem.template.controllers;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.entities.House;
import nl.tudelft.sem.template.repositories.HouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
//@RequestMapping(path="/house")
public class HouseController {

    @Autowired
    private transient HouseRepository houseRepository;

    @GetMapping("/allHouses")
    public @ResponseBody
    List<House> getAllHouses() {
        return houseRepository.findAll();
    }


    @GetMapping("/getHouse/{houseNumber)")
    public @ResponseBody
    Optional<House> getHouseByHouseNumber(@PathVariable int houseNumber) {
        return houseRepository.findById(houseNumber);
    }

    @PostMapping("/addNewHouse")
    boolean addNewHouse(@RequestBody House house) {
        try {
            houseRepository.save(house);
            return true;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }

    @DeleteMapping("/deleteHouse")
    boolean deleteHouse(@RequestParam int houseNumber) {
        houseRepository.deleteById(houseNumber);
        return true;
    }



}
