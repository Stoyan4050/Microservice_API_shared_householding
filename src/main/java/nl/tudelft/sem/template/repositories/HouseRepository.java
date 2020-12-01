package nl.tudelft.sem.template.repositories;

import nl.tudelft.sem.template.entities.House;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for the houses.
 */
@Repository
public interface HouseRepository  extends JpaRepository<House, Integer> {

}
