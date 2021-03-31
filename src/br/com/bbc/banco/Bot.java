package br.com.bbc.banco;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.util.EventListener;

public class Bot{
    public static JDA jda;
    public static String prefix = "$";

    public static void main(String[] args) throws LoginException {
        jda = JDABuilder.createDefault("ODI2NTc3NDQwNTQ5NTAyOTc2.YGOgOg.IsEfs0gYfVxaj3wQJRr--QJAKgc").build();
        jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing("o Charlão na cama"));
        jda.addEventListener(new Commands());
    }
}
