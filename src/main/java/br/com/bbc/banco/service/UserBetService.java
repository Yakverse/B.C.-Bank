package br.com.bbc.banco.service;

import br.com.bbc.banco.model.UserBet;
import br.com.bbc.banco.repository.UserBetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserBetService {

    private final UserBetRepository userBetRepository;

    public UserBetService(UserBetRepository userBetRepository) {
        this.userBetRepository = userBetRepository;
    }

    public UserBet create(UserBet userBet){
        return this.userBetRepository.save(userBet);
    }

    public UserBet findByOption_idAndUser_id(long optionId, long userId){
        return this.userBetRepository.findByOption_idAndUser_id(optionId, userId).orElse(null);
    }

    public void update(UserBet userBet) {
        this.userBetRepository.save(userBet);
    }

    public List<UserBet> findAll(){
        return this.userBetRepository.findAll();
    }
}
