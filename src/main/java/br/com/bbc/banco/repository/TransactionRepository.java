package br.com.bbc.banco.repository;

import br.com.bbc.banco.model.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends PagingAndSortingRepository<Transaction, Long> {

    Optional<List<Transaction>> findByUserIdOrOriginUserId(Long id, Long otherId, Pageable pageable);

}
