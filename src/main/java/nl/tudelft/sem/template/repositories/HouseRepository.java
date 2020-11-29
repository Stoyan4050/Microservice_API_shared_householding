package nl.tudelft.sem.template.repositories;

import nl.tudelft.sem.template.entities.House;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HouseRepository  extends JpaRepository<House, Integer> {

}
