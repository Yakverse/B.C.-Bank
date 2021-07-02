package br.com.bbc.banco.service;

import br.com.bbc.banco.model.Transaction;
import br.com.bbc.banco.model.User;
import br.com.bbc.banco.repository.TransactionRepository;
import br.com.bbc.banco.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
        return this.transactionRepository.findByUserId(id);
    }

    public Transaction update(Transaction transaction){
        return this.transactionRepository.save(transaction);
    }
}
