package br.com.bbc.banco.command;

import br.com.bbc.banco.embed.Embeds;
import br.com.bbc.banco.embed.JokenpoEmbed;
import br.com.bbc.banco.exception.PlayerInvalidoException;
import br.com.bbc.banco.model.Jokenpo;
import br.com.bbc.banco.service.JokenpoService;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AceitaJokenpoCommand extends Command{

    @Autowired
    private JokenpoService jokenpoService;

    @Override
    public void execute(ButtonClickEvent event) throws Exception{
        event.editMessageEmbeds(this.process(event.getUser(),event.getMessage().getEmbeds().get(0).getFooter().getText().split("#")[1]))
                .setActionRow(
                        Button.secondary("U+270A", Emoji.fromUnicode("U+270A")),
                        Button.secondary("U+270B",Emoji.fromUnicode("U+270B")),
                        Button.secondary("U+270C",Emoji.fromUnicode("U+270C"))
                ).queue();
    }


    public MessageEmbed process(net.dv8tion.jda.api.entities.User author, String jokenpoId) throws PlayerInvalidoException{
        Jokenpo jokenpo = this.jokenpoService.findById(Long.parseLong(jokenpoId));
        if(jokenpo.getPlayer2Id() != author.getIdLong()) throw new PlayerInvalidoException();

        Embeds embed = new JokenpoEmbed(author, Long.parseLong(jokenpoId));
        embed.addField("Escolha uma das opçoes abaixo","");
        return embed.build();
    }


}
