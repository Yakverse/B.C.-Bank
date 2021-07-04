package br.com.bbc.banco.event;

import br.com.bbc.banco.command.Commands;
import br.com.bbc.banco.configuration.Bot;
import br.com.bbc.banco.embed.Embeds;
import br.com.bbc.banco.enumeration.BotEnumeration;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ExceptionEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.restaction.WebhookAction;
import net.dv8tion.jda.internal.interactions.InteractionHookImpl;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.nio.channels.Channel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class Events extends ListenerAdapter {

    @Autowired
    private Commands commands;

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        System.out.printf("[%s] Bot Online!%n", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss")));
    }

    @SneakyThrows
    @Override
    public void onSlashCommand(SlashCommandEvent event) {
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

            case "convite":
                event.reply("https://discord.com/api/oauth2/authorize?client_id=826577440549502976&permissions=8&scope=applications.commands%20bot")
                        .queue();
                break;

            case "saldo":
                event.replyEmbeds(commands.mostrarSaldo(event.getUser())).setEphemeral(true).queue();
                break;

            case "criar":
                commands.criarUsuario(event.getUser().getIdLong());
                event.replyEmbeds(commands.mostrarSaldo(event.getUser())).setEphemeral(true).queue();
                break;

            case "depositar":
//                commands.depositar(event.getUser(), event.getOption("valor").getAsString());

                commands.transferir(Bot.jda.getSelfUser(),event.getOption("valor").getAsString(),event.getUser());

                event.replyEmbeds(commands.mostrarSaldo(event.getUser())).setEphemeral(true).queue();
                break;

            case "sacar":
//                commands.sacar(event.getUser(), event.getOption("valor").getAsString());

                commands.transferir(event.getUser(),event.getOption("valor").getAsString(),Bot.jda.getSelfUser());

                event.replyEmbeds(commands.mostrarSaldo(event.getUser())).setEphemeral(true).queue();
                break;

            case "transferir":
                commands.transferir(event.getUser(), event.getOption("valor").getAsString(), event.getOption("pessoa").getAsUser());
                event.replyEmbeds(commands.mostrarSaldo(event.getUser())).setEphemeral(true).queue();
                break;

            case "daily":
                event.replyEmbeds(commands.daily(event.getUser())).setEphemeral(true).queue();
                break;

            case "extrato":
                event.replyEmbeds(commands.mostrarExtrato(event.getUser())).setEphemeral(true).queue();
                break;

            case "criaraposta":
                event.replyEmbeds(commands.criarAposta(event.getUser(), event.getOption("nome").getAsString(), event.getOption("opcao1").getAsString(), event.getOption("opcao2").getAsString())).queue();
                break;

            case "apostas":
                event.replyEmbeds(commands.apostas(event.getUser())).setEphemeral(true).queue();
                break;

            case "jokenpo":
                event.replyEmbeds(commands.jokenpo(event.getUser(),event.getOption("pessoa").getAsUser(), event.getOption("valor").getAsString()))
                        .addActionRow(
                            Button.primary("aceitarJokenpo", Emoji.fromUnicode("U+2714")),
                            Button.secondary("recusarJokenpo", Emoji.fromUnicode("U+274C"))
                            )
                        .queue();
                break;
        }

    }

    @SneakyThrows
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(event.getMessage().getAuthor().isBot()) return;

        String[] args = event.getMessage().getContentRaw().split(" ");
        String firstWord = args[0].substring(1);

        net.dv8tion.jda.api.entities.User author = event.getAuthor();
        Message message = event.getMessage();
        MessageChannel channel = event.getChannel();

        if(args[0].startsWith(BotEnumeration.PREFIX.getValue())) {

            if (firstWord.equalsIgnoreCase("teste")) {
                throw new AbstractMethodError();
            }

            // Criar conta
            if (firstWord.equalsIgnoreCase("criar")) {
                commands.criarUsuario(author.getIdLong());
                channel.sendMessage(commands.mostrarSaldo(author)).queue();
            }

            // Saldo
            if (firstWord.equalsIgnoreCase("saldo")) {
                channel.sendMessage(commands.mostrarSaldo(author)).queue();
            }

            // Depositar
            if (firstWord.equalsIgnoreCase("depositar")) {
                commands.depositar(author, args[1]);
                channel.sendMessage(commands.mostrarSaldo(author)).queue();
            }

            //Sacar
            if (firstWord.equalsIgnoreCase("sacar")) {
                if (args.length > 2) throw new Exception();

                commands.sacar(author, args[1]);
                channel.sendMessage(commands.mostrarSaldo(author)).queue();
            }


            //Transferir
            if (firstWord.equalsIgnoreCase("transferir")) {
                if (args.length > 3) throw new Exception();

                List<User> users = event.getMessage().getMentionedUsers();
                if (users.size() > 1) throw new Exception();

                commands.transferir(author, args[1], users.get(0));
                channel.sendMessage(commands.mostrarSaldo(author)).queue();
            }

            //Daily
            if (firstWord.equalsIgnoreCase("daily")){
                channel.sendMessage(commands.daily(event.getAuthor())).queue();
            }

            //Criar Aposta
            if (firstWord.equalsIgnoreCase("criaraposta")){
                if (args.length < 4) channel.sendMessage(Embeds.criarApostaEmbedError(author, 0x00000).build()).queue();
                else {
                    String nome = args[1];

                    List<String> list = Arrays.asList(args);
                    list = list.subList(2, args.length);

                    String[] newarr = new String[list.size()];
                    list.toArray(newarr);

                    channel.sendMessage(commands.criarAposta(author, nome, newarr)).queue();
                }
            }

            //Apostas
            if (firstWord.equalsIgnoreCase("apostas")){
                channel.sendMessage(commands.apostas(author)).queue();
            }

        }
    }
}