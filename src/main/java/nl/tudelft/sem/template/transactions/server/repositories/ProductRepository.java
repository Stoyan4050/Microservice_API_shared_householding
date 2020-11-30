package nl.tudelft.sem.template.transactions.server.repositories;

import nl.tudelft.sem.template.transactions.server.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
}
