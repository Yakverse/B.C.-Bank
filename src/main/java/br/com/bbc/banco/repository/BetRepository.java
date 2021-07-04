package br.com.bbc.banco.repository;

import br.com.bbc.banco.model.Bet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BetRepository extends JpaRepository<Bet, Long> {

    Optional<List<Bet>> findByIsOpenTrue();
}
