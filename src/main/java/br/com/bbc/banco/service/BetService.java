package br.com.bbc.banco.service;

import br.com.bbc.banco.model.Bet;
import br.com.bbc.banco.repository.BetRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BetService {

    @Autowired
    private BetRepository betRepository;

    public Bet create(Bet bet){
        return this.betRepository.save(bet);
    }

    @Transactional(readOnly = true)
    public Bet findById(long id){
        Optional<Bet> bet = this.betRepository.findById(id);
        bet.ifPresent(b -> Hibernate.initialize(b.getOptions()));
        return bet.orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Bet> findAll(){
        Optional<List<Bet>> listBet = this.betRepository.findByIsOpenTrue();
        listBet.ifPresent(b -> {
            for (Bet bet : b) {
                Hibernate.initialize(bet.getOptions());
            }
        });
        return listBet.orElse(null);
    }
}
