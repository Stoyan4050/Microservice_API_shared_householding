package nl.tudelft.sem.requests.repositories;

import nl.tudelft.sem.requests.entities.House;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for the houses.
 */
@Repository
public interface HouseRepository extends JpaRepository<House, Integer> {
    House findByHouseNr(int houseNr);

    House findByUsersUsername(String username);

}
