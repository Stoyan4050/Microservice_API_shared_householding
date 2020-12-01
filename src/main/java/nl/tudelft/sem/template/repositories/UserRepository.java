package nl.tudelft.sem.template.repositories;

import nl.tudelft.sem.template.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for the users.
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

}
