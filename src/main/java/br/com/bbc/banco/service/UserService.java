package br.com.bbc.banco.service;

import br.com.bbc.banco.model.User;
import br.com.bbc.banco.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User create(User user){
        return this.userRepository.save(user);
    }

    public User findById(Long id){
        return this.userRepository.findById(id).orElse(null);
    }

    public User update(User user, Long id){
        User checkUser = this.userRepository.findById(id).orElse(null);
        return this.userRepository.save(user);
    }
}
