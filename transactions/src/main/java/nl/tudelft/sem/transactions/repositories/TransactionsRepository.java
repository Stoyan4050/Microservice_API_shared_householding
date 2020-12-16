package nl.tudelft.sem.transactions.repositories;

import javax.transaction.Transactional;
import nl.tudelft.sem.transactions.entities.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TransactionsRepository  extends JpaRepository<Transactions, Long> {
    @Query(value = "UPDATE transaction "
            + "SET product_id = ?1, username = ?2, portions_consumed = ?3 "
            + "WHERE transaction_id = ?4", nativeQuery = true)
    @Modifying
    @Transactional
    int updateExistingTransaction(long productId, String username,
                                  int portionsConsumed, long transactionId);

    @Query(value = "DELETE FROM transaction WHERE transaction_id = ?1", nativeQuery = true)
    @Modifying
    @Transactional
    int deleteTransactionById(long transactionId); //delete transaction

}
