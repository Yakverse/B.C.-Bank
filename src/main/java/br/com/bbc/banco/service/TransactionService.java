package br.com.bbc.banco.service;

import br.com.bbc.banco.model.Transaction;
import br.com.bbc.banco.model.User;
import br.com.bbc.banco.repository.TransactionRepository;
import br.com.bbc.banco.repository.UserRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction create(Transaction transaction){
        return this.transactionRepository.save(transaction);
    }

    public Transaction findById(Long id){
        return this.transactionRepository.findById(id).orElse(null);
    }

    public List<Transaction> findByUserId(Long id){
        Pageable sortedFirstPageWithTenElementsByDate = PageRequest.of(0, 10, Sort.by("date").descending());

        Optional<List<Transaction>> transactions = this.transactionRepository.findByUserIdOrOriginUserId(id, id, sortedFirstPageWithTenElementsByDate);
        transactions.ifPresent(t -> {
            for (Transaction transaction : t) {
                Hibernate.initialize(transaction.getUser());
                Hibernate.initialize(transaction.getOriginUser());
            }
        });

        return transactions.orElse(null);
    }

    public Transaction update(Transaction transaction){
        return this.transactionRepository.save(transaction);
    }
}
