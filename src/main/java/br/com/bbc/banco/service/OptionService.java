package br.com.bbc.banco.service;

import br.com.bbc.banco.model.Option;
import br.com.bbc.banco.repository.OptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OptionService {

    @Autowired
    private OptionRepository optionRepository;

    public Option create(Option option){
        return this.optionRepository.save(option);
    }
}
