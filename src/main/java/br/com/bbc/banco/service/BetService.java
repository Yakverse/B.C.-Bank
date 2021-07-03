package br.com.bbc.banco.service;

import br.com.bbc.banco.model.Bet;
import br.com.bbc.banco.repository.BetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BetService {

    @Autowired
    private BetRepository betRepository;

    public Bet create(Bet bet){
        return this.betRepository.save(bet);
    }

    public Bet findById(long id){
        return this.betRepository.findById(id).orElse(null);
    }

    public List<Bet> findAll(){
        return this.betRepository.findAll();
    }
}
