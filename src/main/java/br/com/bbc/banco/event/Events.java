package br.com.bbc.banco.event;

import br.com.bbc.banco.command.Commands;
import br.com.bbc.banco.enumeration.BotEnumeration;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.WebhookAction;
import net.dv8tion.jda.internal.interactions.InteractionHookImpl;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class Events extends ListenerAdapter {

    @Autowired
    private Commands commands;

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        System.out.printf("[%s] Bot Online!%n", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss")));
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event)
    {
        System.out.println("SLASH");
        switch (event.getName()){
            case "ping":
                long time = System.currentTimeMillis();
                event.reply("Pong!").setEphemeral(true) // reply or acknowledge
                        .flatMap(v ->
                                event.getHook().editOriginalFormat("Pong: %d ms", System.currentTimeMillis() - time) // then edit original
                        ).queue(); // Queue both reply and edit
                break;

            case "say":
                event.reply(event.getOption("conteúdo").getAsString()) // reply or acknowledge
                        .queue(); // Queue both reply and edit
                break;
//            case "saldo":
//                event.reply("Pong!").setEphemeral(true) // reply or acknowledge
//                        .flatMap(v ->
//                                event.getHook().editOriginalFormat("Pong: %d ms", System.currentTimeMillis() - time) // then edit original
//                        ).queue(); // Queue both reply and edit
//                break;
        }

    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(event.getMessage().getAuthor().isBot()) return;

        String[] args = event.getMessage().getContentRaw().split(" ");
        String firstWord = args[0].substring(1);

        if(args[0].startsWith(BotEnumeration.PREFIX.getValue())){

            if(firstWord.equalsIgnoreCase("teste")){

            }

            // Saldo
            if(firstWord.equalsIgnoreCase("saldo")){
                commands.mostrarSaldo(event);
            }

            // Depositar
            if(firstWord.equalsIgnoreCase("depositar")){
                try{
                    String secondWord = args[1].replace(',','.');
                    BigDecimal valor = BigDecimal.valueOf( Double.parseDouble(secondWord));
                    if (valor.compareTo(BigDecimal.ZERO) <= 0) throw new Exception();
                    commands.depositar(event,valor);
                }
                catch (Exception e){
                    commands.erro(event);
                }
            }

            //Sacar
            if(firstWord.equalsIgnoreCase("sacar")){
                try{
                    if(args.length > 2) throw new Exception();

                    String secondWord = args[1].replace(',','.');
                    BigDecimal valor = BigDecimal.valueOf( Double.parseDouble(secondWord));
                    if (valor.compareTo(BigDecimal.ZERO) <= 0) throw new Exception();
                    commands.sacar(event,valor);
                }
                catch (Exception e){
                    commands.erro(event);
                }
            }

            if(firstWord.equalsIgnoreCase("transferir")){
                try{
                    if(args.length > 3) throw new Exception();

                    List<User> users = event.getMessage().getMentionedUsers();
                    if(users.size() > 1) throw new Exception();
                    if(event.getAuthor().getIdLong() == users.get(0).getIdLong()) throw new Exception();
                    String secondWord = args[1].replace(',','.');
                    BigDecimal valor = BigDecimal.valueOf( Double.parseDouble(secondWord));
                    if (valor.compareTo(BigDecimal.ZERO) <= 0) throw new Exception();

                    commands.transferir(event,valor,users.get(0));
                }
                catch (Exception e){
                    commands.erro(event);
                }
            }



        }
    }
}
