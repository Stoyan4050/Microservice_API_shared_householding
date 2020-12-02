package nl.tudelft.sem.requests.repositories;

import nl.tudelft.sem.requests.entities.Request;
import nl.tudelft.sem.requests.entities.RequestId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for the requests.
 */
@Repository
public interface RequestRepository extends JpaRepository<Request, RequestId> {

}