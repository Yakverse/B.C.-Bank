package br.com.bbc.banco.repository;

import br.com.bbc.banco.model.UserBet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBetRepository extends JpaRepository<UserBet, Long> {

    Optional<UserBet> findByOption_idAndUser_id(long optionId, long userId);
}
