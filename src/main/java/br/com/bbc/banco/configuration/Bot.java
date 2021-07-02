package br.com.bbc.banco.configuration;

import br.com.bbc.banco.enumeration.BotEnumeration;
import br.com.bbc.banco.event.Events;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.security.auth.login.LoginException;

@Configuration
public class Bot {

    @Autowired
    private Events events;

    public static JDA jda;

    @Bean
    public void initialize() throws LoginException {
        jda = JDABuilder.createDefault(BotEnumeration.TOKEN.getValue()).build();
        jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing("o Charlão na cama"));
        jda.addEventListener(events);
    }
}
