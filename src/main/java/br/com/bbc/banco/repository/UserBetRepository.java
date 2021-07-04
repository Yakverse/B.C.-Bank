package br.com.bbc.banco.repository;

import br.com.bbc.banco.model.UserBet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBetRepository extends JpaRepository<UserBet, Long> {
}
