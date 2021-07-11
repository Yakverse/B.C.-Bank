package br.com.bbc.banco.command;

import br.com.bbc.banco.service.UserService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class Command {

    @Autowired
    protected UserService userService;

    public final String name = null;
    public final String description = null;
    public final OptionData[] options = {};

    public void execute(SlashCommandEvent event) throws Exception {
        log.error(String.format("O Slash Command da classe %s não foi executado!", this.getClass().getName()));
    }

    public void execute(MessageReceivedEvent event) throws Exception {
        log.error(String.format("O comando da classe %s não foi executado!", this.getClass().getName()));
    }
}
