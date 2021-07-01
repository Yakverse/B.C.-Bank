package br.com.bbc.banco.event;

import br.com.bbc.banco.command.SaldoCommand;
import br.com.bbc.banco.enumeration.BotEnumeration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Events extends ListenerAdapter {

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        System.out.printf("[%s] Bot Online!%n", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss")));
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if(event.getMessage().getAuthor().isBot()) return;

        String[] args = event.getMessage().getContentRaw().split(" ");
        String firstWord = args[0].substring(1);

        if(args[0].startsWith(BotEnumeration.PREFIX.getValue())){
            // Saldo
            if(firstWord.equalsIgnoreCase("saldo")){
                SaldoCommand.mostrarSaldo(event);
            }

        }

    }
}
