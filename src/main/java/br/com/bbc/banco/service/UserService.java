package br.com.bbc.banco.service;

import br.com.bbc.banco.exception.ContaJaExisteException;
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

    @Transactional()
    public User create(User user) throws ContaJaExisteException {
        if (this.findById(user.getId()) != null){
            throw new ContaJaExisteException();
        }
        User newUser = this.userRepository.save(user);
        Optional.of(newUser).ifPresent(u -> Hibernate.initialize(u.getTransactions()));
        return newUser;
    }

    @Transactional()
    public User findById(Long id){
        Optional<User> user = this.userRepository.findById(id);
        user.ifPresent(u -> Hibernate.initialize(u.getTransactions()));
        return user.orElse(null);
    }

    @Transactional()
    public User findOrCreateById(Long id){
        User user;
        try{
            user = this.create(new User(id));
        } catch (ContaJaExisteException e) {
            user = this.findById(id);
        }

        return user;
    }

    public void update(User user){
        this.userRepository.save(user);
    }
}
