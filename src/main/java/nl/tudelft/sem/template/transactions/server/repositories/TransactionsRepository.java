package nl.tudelft.sem.template.transactions.server.repositories;

import nl.tudelft.sem.template.transactions.server.entities.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface TransactionsRepository  extends JpaRepository<Transactions, Integer> {
    @Query(value = "UPDATE Transactions "
            + "SET product_id = ?1, username = ?2, portions_consumed = ?3 "
            + "WHERE transaction_id = ?4", nativeQuery = true)
    @Modifying
    @Transactional
    int updateExistingTransaction(int product_id, String username, int portions_consumed, int transaction_id);

}
