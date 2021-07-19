package br.com.bbc.banco.command;

import br.com.bbc.banco.embed.Embeds;
import br.com.bbc.banco.model.Bet;
import lombok.Getter;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApostasCommand extends Command{

    @Getter private final String name = "apostas";
    @Getter private final String description = "Mostra todas as apostas disponíveis";

    @Override
    public void execute(SlashCommandEvent event) throws Exception{
        event.replyEmbeds(this.process(event.getUser())).setEphemeral(true).queue();
    }

    @Override
    public void execute(MessageReceivedEvent event) throws Exception{
        event.getChannel().sendMessage(this.process(event.getAuthor())).queue();
    }

    public MessageEmbed process(net.dv8tion.jda.api.entities.User author){
        List<Bet> listBet = this.betService.findAll();
        if (listBet == null || listBet.isEmpty()) return Embeds.semApostas(author, 0x00000).build();

        return Embeds.apostasEmbed(author, 0x00000, listBet).build();
    }
}
