package br.com.bbc.banco.repository;

import br.com.bbc.banco.model.Jokenpo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JokenpoRepository extends JpaRepository<Jokenpo, Long> {
}
