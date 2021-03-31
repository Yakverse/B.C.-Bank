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
        jda = JDABuilder.createDefault("Njc5MTUzNzU0MTc1NzAxMDMy.XktNOQ.iaFHfe8Xcr-Eo6e9v3tJJWSehb0").build();
        jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing("o Charlão na cama"));
        jda.addEventListener(new Commands());
    }
}
