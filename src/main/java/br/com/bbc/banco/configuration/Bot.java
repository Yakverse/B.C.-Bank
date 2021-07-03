package br.com.bbc.banco.configuration;

import br.com.bbc.banco.enumeration.BotEnumeration;
import br.com.bbc.banco.event.Events;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.WebhookClient;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.interactions.InteractionHookImpl;
import net.dv8tion.jda.internal.interactions.InteractionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.security.auth.login.LoginException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import static net.dv8tion.jda.api.interactions.commands.OptionType.*;
import static net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER;

@Configuration
public class Bot {

    @Autowired
    private Events events;

    public static JDA jda;

    @Bean
    public void initialize() throws LoginException {
        jda = JDABuilder.createLight(BotEnumeration.TOKEN.getValue())
                .setRawEventsEnabled(true)
                .addEventListeners(events)
                .setActivity(Activity.playing("o Charlão na cama"))
                .build();

        jda.upsertCommand("ping", "Calculate ping of the bot").queue();

        CommandListUpdateAction commands = jda.updateCommands();

        commands.addCommands(
                new CommandData("say", "Faça o bot falar o que você disse")
                        .addOptions(new OptionData(STRING, "conteúdo", "O que o bot deve dizer")
                                .setRequired(true))
        );

        commands.addCommands(
                new CommandData("ping", "Ping-Pong com o bot")
        );

        commands.addCommands(
                new CommandData("convite", "Te envia o convite para o bot")
        );

        commands.addCommands(
                new CommandData("criar", "Criar conta no BBC")
        );

        commands.addCommands(
                new CommandData("saldo", "Mostra o seu saldo")
        );

        commands.addCommands(
                new CommandData("depositar", "Deposita dinheiro")
                        .addOptions(new OptionData(STRING, "valor", "O quanto você vai depositar")
                                .setRequired(true))
        );

        commands.addCommands(
                new CommandData("sacar", "Retira dinheiro")
                        .addOptions(new OptionData(STRING, "valor", "O quanto você vai retirar")
                                .setRequired(true))
        );

        commands.addCommands(
                new CommandData("transferir", "Retira dinheiro")
                        .addOptions(
                            new OptionData(STRING, "valor", "O quanto você vai transferir").setRequired(true),
                            new OptionData(USER, "pessoa", "Pessoa que recebe o dinheiro").setRequired(true)
                        )
        );

        commands.addCommands(
                new CommandData("extrato", "Mostra seu extrato")
        );


        commands.queue();
    }

}
