package br.com.bbc.banco.configuration;

import br.com.bbc.banco.command.Command;
import br.com.bbc.banco.enumeration.BotEnumeration;
import br.com.bbc.banco.event.Events;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.security.auth.login.LoginException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static net.dv8tion.jda.api.interactions.commands.OptionType.*;
import static net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER;

@Configuration
public class BotApplication {

    @Autowired
    private Events events;

    public static JDA jda;

    @Bean
    public void initialize() throws LoginException, InstantiationException, IllegalAccessException {
        jda = JDABuilder.createLight(BotEnumeration.TOKEN.getText())
                .setRawEventsEnabled(true)
                .addEventListeners(events)
                .setActivity(Activity.playing("o Charlão na cama"))
                .build();

        CommandListUpdateAction commands = jda.updateCommands();

        Reflections reflections = new Reflections("br.com.bbc.banco.command");
        Set<Class<? extends Command>> classes = reflections.getSubTypesOf(Command.class);
        for (Class<? extends Command> aClass : classes) {
            Command command = aClass.newInstance();
            CommandData commandData = new CommandData(command.getName(), command.getDescription());

            if (!command.getOptions().isEmpty()) commandData.addOptions(command.getOptions());

            commands.addCommands(commandData);
        }

        commands.queue();
    }

}
