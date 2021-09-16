package br.com.bbc.banco.service;

import br.com.bbc.banco.model.Bet;
import br.com.bbc.banco.model.Option;
import br.com.bbc.banco.model.User;
import br.com.bbc.banco.model.UserBet;
import br.com.bbc.banco.repository.BetRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BetService {

    private final BetRepository betRepository;

    public BetService(BetRepository betRepository) {
        this.betRepository = betRepository;
    }

    public Bet create(Bet bet){
        return this.betRepository.save(bet);
    }

    @Transactional()
    public Bet findById(long id){
        Optional<Bet> bet = this.betRepository.findById(id);
        bet.ifPresent(b -> {
            Hibernate.initialize(b.getOptions());
            Hibernate.initialize(b.getCreatedBy());
            List<Option> list = b.getOptions();
            User user = b.getCreatedBy();
            if (Hibernate.isInitialized(list) && Hibernate.isInitialized(user)){
                for (Option option : b.getOptions()){
                    Hibernate.initialize(option.getUser_bet());
                }
            }
        });
        return bet.orElse(null);
    }

    @Transactional()
    public List<Bet> findAll(){
        Optional<List<Bet>> listBet = this.betRepository.findByIsOpenTrue();
        listBet.ifPresent(b -> {
            for (Bet bet : b) {
                Hibernate.initialize(bet.getOptions());
                Hibernate.initialize(bet.getCreatedBy());
                for (Option option : bet.getOptions()){
                    Hibernate.initialize(option.getUser_bet());
                }
            }
        });
        return listBet.orElse(null);
    }

    public void update(Bet bet){
        this.betRepository.save(bet);
    }

    public void delete(Bet bet){
        this.betRepository.delete(bet);
    }
}
