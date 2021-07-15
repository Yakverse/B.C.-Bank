package br.com.bbc.banco.command;

import br.com.bbc.banco.embed.Embeds;
import br.com.bbc.banco.model.Transaction;
import br.com.bbc.banco.model.User;
import lombok.Getter;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExtratoCommand extends Command {

    @Getter private final String name = "extrato";
    @Getter private final String description = "Mostra seu extrato";

    @Override
    public void execute(SlashCommandEvent event) throws Exception {
        event.replyEmbeds(this.process(event.getUser())).setEphemeral(true).queue();
    }

    @Override
    public void execute(MessageReceivedEvent event) throws Exception {
        event.getChannel().sendMessage(this.process(event.getAuthor())).queue();
    }

    private MessageEmbed process(net.dv8tion.jda.api.entities.User author) throws Exception{
        User user = userService.findOrCreateById(author.getIdLong());
        List<Transaction> transactions = this.transactionService.findByUserId(user.getId());
        return Embeds.extratoEmbed(author, user, transactions, 0x00000).build();
    }
}
