package br.com.bbc.banco.configuration;

import br.com.bbc.banco.command.Command;
import br.com.bbc.banco.enumeration.BotEnumeration;
import br.com.bbc.banco.event.Events;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.reflections.Reflections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.security.auth.login.LoginException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Configuration
@Slf4j
public class BotApplication {

    private final Events events;
    public static JDA jda;

    public BotApplication(Events events) {
        this.events = events;
    }

    @Bean
    public void initialize() throws LoginException, InstantiationException, IllegalAccessException {
        jda = JDABuilder.createLight(BotEnumeration.TOKEN.getText())
                .setRawEventsEnabled(true)
                .addEventListeners(events)
                .setActivity(Activity.playing("o Charlão na cama"))
                .build();


        log.info("Verificando se os comandos precisam ser atualizados");
        CommandListUpdateAction commands = jda.updateCommands();
        List<net.dv8tion.jda.api.interactions.commands.Command> comandosAtuais = jda.retrieveCommands().complete();
        List<CommandData> comandosNovos = this.addCommands();

        boolean comandosIguais = true;
        if (BotEnumeration.UPDATE_COMMANDS.getText() != null && BotEnumeration.UPDATE_COMMANDS.getText().equals("true")) comandosIguais = false;
        else if (comandosAtuais.size() == comandosNovos.size()) {
            for (net.dv8tion.jda.api.interactions.commands.Command comandosAtual : comandosAtuais) {
                for (int j = 0; j < comandosNovos.size(); j++) {

                    if (comandosAtual.getName().equals(comandosNovos.get(j).getName()) && comandosAtual.getDescription().equals(comandosNovos.get(j).getDescription())) {
                        if (comandosAtual.getOptions().size() == comandosNovos.get(j).getOptions().size()) break;
                    }

                    if (j == (comandosNovos.size() - 1)) {
                        comandosIguais = false;
                    }
                }
            }
        } else comandosIguais = false;


        if (!comandosIguais){
            commands.addCommands(comandosNovos).queue();
            log.info("Os comandos foram atualizados com sucesso!");
        } else log.info("Os comandos já estão atualizados");


   }


    private List<CommandData> addCommands() throws InstantiationException, IllegalAccessException {
        Reflections reflections = new Reflections("br.com.bbc.banco.command");
        Set<Class<? extends Command>> classes = reflections.getSubTypesOf(Command.class);
        List<CommandData> commandDataList = new ArrayList<>();
        for (Class<? extends Command> aClass : classes) {
            @SuppressWarnings("deprecation")
            Command command = aClass.newInstance();
            CommandData commandData = new CommandData(command.getName(), command.getDescription());
            commandData.addOptions(command.getOptions());

            commandDataList.add(commandData);
        }
        return commandDataList;
    }

}
