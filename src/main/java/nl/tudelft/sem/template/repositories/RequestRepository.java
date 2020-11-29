package nl.tudelft.sem.template.repositories;

import nl.tudelft.sem.template.entities.Request;
import nl.tudelft.sem.template.entities.RequestId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for the requests.
 */
@Repository
public interface RequestRepository extends JpaRepository<Request, RequestId> {

}