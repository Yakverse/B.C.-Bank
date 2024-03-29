package br.com.bbc.banco.service;

import br.com.bbc.banco.model.Jokenpo;
import br.com.bbc.banco.repository.JokenpoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JokenpoService {

    private final JokenpoRepository jokenpoRepository;

    public JokenpoService(JokenpoRepository jokenpoRepository) {
        this.jokenpoRepository = jokenpoRepository;
    }

    public Jokenpo create(Jokenpo jokenpo) {
        return this.jokenpoRepository.save(jokenpo);
    }

    public Jokenpo findById(Long id) {
        return this.jokenpoRepository.findById(id).orElse(null);
    }

    public Jokenpo update(Jokenpo jokenpo){
        return this.jokenpoRepository.save(jokenpo);
    }
}
