package br.com.bbc.banco.util;

import br.com.bbc.banco.model.User;
import br.com.bbc.banco.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserUtils {

    @Autowired
    private UserService userService;

    public User idToUser(Long id){
        User user = this.userService.findById(id);
        if (user == null){
            user = this.userService.create(new User(id));
        }
        return user;
    }
}
