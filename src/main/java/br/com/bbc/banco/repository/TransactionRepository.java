package br.com.bbc.banco.repository;

import br.com.bbc.banco.model.Transaction;
import br.com.bbc.banco.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends PagingAndSortingRepository<Transaction, Long> {

    List<Transaction> findByUserId(Long id);
    List<Transaction> findByOriginUserId(Long id);
    List<Transaction> findByUserIdOrOriginUserId(Long id, Long otherId, Pageable pageable);

}
