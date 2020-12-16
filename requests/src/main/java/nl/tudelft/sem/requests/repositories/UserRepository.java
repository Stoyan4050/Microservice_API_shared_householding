package nl.tudelft.sem.requests.repositories;

import javax.transaction.Transactional;
import nl.tudelft.sem.requests.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for the users.
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {
	
    @Query(value = "UPDATE user "
					   + "SET house_nr = ?1, email = ?2, total_credits = ?3 "
					   + "WHERE username = ?4", nativeQuery = true)
    @Modifying
    @Transactional
    int updateUserCredits(int houseNumber, String email,
					  float totalCredits, String username);

    User findByUsername(String username);
}