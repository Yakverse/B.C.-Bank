package br.com.bbc.banco.event;

import br.com.bbc.banco.command.*;
import br.com.bbc.banco.enumeration.BotEnumeration;
import br.com.bbc.banco.util.GenericUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@Slf4j
public class Events extends ListenerAdapter {

    @Autowired
    private CommandFactory commandFactory;

    @Autowired
    private JokenpoCommand jokenpoCommand;

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        GenericUtils.asciibbc();
        log.info("Bot Online!");
    }

    @SneakyThrows
    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        commandFactory.factory(event.getName()).execute(event);
    }

    @SneakyThrows
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getMessage().getAuthor().isBot()) return;
        if (!event.getMessage().getAttachments().isEmpty()) return;

        String[] args = event.getMessage().getContentRaw().split(" ");
        String firstWord = args[0].substring(1);

        if(args[0].startsWith(BotEnumeration.PREFIX.getText())) {

            //Transferir
            if (firstWord.equalsIgnoreCase("transferir")) {
                if (args.length > 3) throw new Exception();
                if (event.getMessage().getMentionedUsers().size() > 1) throw new Exception();
            }

            //Apostar
            if (firstWord.equalsIgnoreCase("apostar")){
                if (args.length < 4) throw new Exception();
            }

            //FinalizarAposta
            if (firstWord.equalsIgnoreCase("finalizar")){
                if (args.length < 3) throw new Exception();
            }

            if (firstWord.equalsIgnoreCase("cancelar")){
                if (args.length < 2) throw new Exception();
            }

            commandFactory.factory(firstWord.toLowerCase()).execute(event);

        }
    }

    @SneakyThrows
    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event){
        switch (event.getButton().getId()){
            case "aceitarJokenpo":
                this.jokenpoCommand.aceitaJokenpo(event);
                break;
            case "recusarJokenpo":
                this.jokenpoCommand.recusaJokenpo(event);
                break;
            case "U+270A":
            case "U+270B":
            case "U+270C":
                this.jokenpoCommand.opcaoJokenpo(event);
                break;
            default:
                throw new Exception();
        }
    }
}