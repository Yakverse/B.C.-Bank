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
public class BotApplication {

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

        CommandListUpdateAction commands = jda.updateCommands();

        commands.addCommands(
                new CommandData("convite", "Te envia o convite para o bot"),
                new CommandData("criar", "Criar conta no BBC"),
                new CommandData("saldo", "Mostra o seu saldo"),
                new CommandData("daily", "Recompensa diária"),
                new CommandData("apostas", "Mostra todas as apostas disponíveis"),
                new CommandData("extrato", "Mostra seu extrato"),

                new CommandData("transferir", "Transfeir dinheiro")
                    .addOptions(
                        new OptionData(STRING, "valor", "O quanto você vai transferir").setRequired(true),
                        new OptionData(USER, "pessoa", "Pessoa que recebe o dinheiro").setRequired(true)
                    ),

                new CommandData("criaraposta", "Cria uma aposta")
                    .addOptions(
                        new OptionData(STRING, "nome", "Nome da Aposta").setRequired(true),
                        new OptionData(STRING, "opcao1", "Opção 1 da aposta").setRequired(true),
                        new OptionData(STRING, "opcao2", "Opção 2 da aposta").setRequired(true)
                    ),

                new CommandData("apostar", "Aposta em uma opção")
                    .addOptions(
                            new OptionData(INTEGER, "id_aposta", "ID da aposta").setRequired(true),
                            new OptionData(INTEGER, "numero_opcao", "Número da opção na aposta").setRequired(true),
                            new OptionData(STRING, "valor", "Valor que vai ser apostado na opção").setRequired(true)
                    ),

                new CommandData("aposta", "Descreve uma aposta")
                    .addOptions(new OptionData(INTEGER, "id_aposta", "ID da aposta").setRequired(true)),

                new CommandData("finalizar", "Finaliza uma aposta")
                    .addOptions(
                            new OptionData(INTEGER, "id_aposta", "ID da aposta").setRequired(true),
                            new OptionData(INTEGER, "numero_opcao", "Número da opção na aposta").setRequired(true)
                    ),

                new CommandData("cancelar", "Cancela uma aposta")
                    .addOptions(new OptionData(INTEGER, "id_aposta", "ID da aposta").setRequired(true)),

                new CommandData("jokenpo", "jokenpo")
                    .addOptions(
                        new OptionData(USER, "pessoa", "Pessoa você quer desafiar").setRequired(true),
                        new OptionData(STRING, "valor", "Valor da aposta").setRequired(true)
                    )
        );


        commands.queue();
    }

}
