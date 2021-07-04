package br.com.bbc.banco.service;

import br.com.bbc.banco.model.UserBet;
import br.com.bbc.banco.repository.UserBetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserBetService {

    @Autowired
    private UserBetRepository userBetRepository;

    public UserBet create(UserBet userBet){
        return this.userBetRepository.save(userBet);
    }
}
