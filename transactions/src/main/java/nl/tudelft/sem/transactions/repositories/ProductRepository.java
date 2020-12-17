package nl.tudelft.sem.transactions.repositories;

import javax.transaction.Transactional;
import nl.tudelft.sem.transactions.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query(value = "UPDATE product "
                           + "SET product_name = ?1, username = ?2, price = ?3, "
                           + "total_portions = ?4, portions_left = ?5, expired = ?6 "
                           + "WHERE product_id = ?7", nativeQuery = true)
    
    @Modifying
    @Transactional
    int updateExistingProduct(String productName, String username, float price,
                              int totalPortions, int portionsLeft,
                              int expired, long productId);

    @Query(value = "DELETE FROM product WHERE productId = ?1", nativeQuery = true)
    @Modifying
    @Transactional
    int deleteProductById(long productId);

    Product findByProductId(long productId);
    
}

