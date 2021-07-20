package br.com.bbc.banco.command;

import br.com.bbc.banco.embed.Embeds;
import br.com.bbc.banco.embed.JokenpoEmbed;
import br.com.bbc.banco.enumeration.BotEnumeration;
import br.com.bbc.banco.exception.PlayerInvalidoException;
import br.com.bbc.banco.model.Jokenpo;
import br.com.bbc.banco.model.User;
import br.com.bbc.banco.service.JokenpoService;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static br.com.bbc.banco.util.GenericUtils.convertStringToBigDecimalReplacingComma;

@Component
public class JokenpoCommand extends Command{

    @Autowired
    private JokenpoService jokenpoService;

    @Getter private final String name = "jokenpo";
    @Getter private final String description = "jogar jokenpo";

    @Override
    public void execute(SlashCommandEvent event) throws Exception{
        event.replyEmbeds(this.process(event.getUser(), event.getOption("valor").getAsString(), event.getOption("pessoa").getAsUser()))
            .addActionRow(
                    Button.primary("aceitarJokenpo", Emoji.fromUnicode("U+2714")),
                    Button.danger("recusarJokenpo", Emoji.fromUnicode("U+2716"))
            ).queue();
    }

    private MessageEmbed process(net.dv8tion.jda.api.entities.User author, String valueString, net.dv8tion.jda.api.entities.User other) throws Exception {
        if(author.getIdLong() == other.getIdLong()) throw new PlayerInvalidoException();


        User player1 = userService.findOrCreateById(author.getIdLong());
        User player2 = userService.findOrCreateById(other.getIdLong());
        BigDecimal value = convertStringToBigDecimalReplacingComma(valueString);

        Jokenpo jokenpo = new br.com.bbc.banco.model.Jokenpo();
        jokenpo.setPlayer1Id(player1.getId());
        jokenpo.setPlayer2Id(player2.getId());
        jokenpo.setValue(value);

        this.jokenpoService.create(jokenpo);

        Embeds embed = new JokenpoEmbed(other, jokenpo.getId());

        String message = String.format("%s te desafiou!",author.getName());

        String underMessage = String.format("Valor: %s %.2f",
                BotEnumeration.CURRENCY.getText(),
                value
        );

        embed.addField(message,underMessage);

        return embed.build();
    }

}
