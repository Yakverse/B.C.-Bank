package br.com.bbc.banco.service;

import br.com.bbc.banco.model.Jokenpo;
import br.com.bbc.banco.repository.JokenpoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JokenpoService {

    @Autowired
    private JokenpoRepository jokenpoRepository;

    public Jokenpo create(Jokenpo jokenpo) {
        return this.jokenpoRepository.save(jokenpo);
    }

}
