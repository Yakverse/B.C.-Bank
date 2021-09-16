package br.com.bbc.banco.service;

import br.com.bbc.banco.model.Option;
import br.com.bbc.banco.repository.OptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OptionService {

    private final OptionRepository optionRepository;

    public OptionService(OptionRepository optionRepository) {
        this.optionRepository = optionRepository;
    }

    public Option create(Option option){
        return this.optionRepository.save(option);
    }

    public Option findById(long id){
        return this.optionRepository.findById(id).orElse(null);
    }
}
