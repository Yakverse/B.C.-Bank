package br.com.bbc.banco.service;

import br.com.bbc.banco.model.User;
import br.com.bbc.banco.repository.UserRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User create(User user){
        return this.userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findById(Long id){
        Optional<User> user = this.userRepository.findById(id);
        user.ifPresent(u -> Hibernate.initialize(u.getTransactions()));
        return user.orElse(null);
    }

    public User update(User user){
        return this.userRepository.save(user);
    }
}
