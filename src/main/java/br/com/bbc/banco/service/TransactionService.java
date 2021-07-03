package br.com.bbc.banco.service;

import br.com.bbc.banco.model.Transaction;
import br.com.bbc.banco.model.User;
import br.com.bbc.banco.repository.TransactionRepository;
import br.com.bbc.banco.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public Transaction create(Transaction transaction){
        return this.transactionRepository.save(transaction);
    }

    public Transaction findById(Long id){
        return this.transactionRepository.findById(id).orElse(null);
    }

    public List<Transaction> findByUserId(Long id){
        Pageable sortedFirstPageWithTenElementsByDate = PageRequest.of(0, 10, Sort.by("date").descending());
        return this.transactionRepository.findByUserIdOrOriginUserId(id, id, sortedFirstPageWithTenElementsByDate);
    }

    public Transaction update(Transaction transaction){
        return this.transactionRepository.save(transaction);
    }
}
