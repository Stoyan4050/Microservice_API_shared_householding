package nl.tudelft.sem.requests.repositories;

import nl.tudelft.sem.requests.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for the users.
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

}
