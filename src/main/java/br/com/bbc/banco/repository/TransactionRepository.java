package br.com.bbc.banco.repository;

import br.com.bbc.banco.model.Transaction;
import br.com.bbc.banco.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserId(Long id);
    List<Transaction> findByOriginUserId(Long id);
}
