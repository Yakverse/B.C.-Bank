package br.com.bbc.banco.command;

import br.com.bbc.banco.configuration.BotApplication;
import br.com.bbc.banco.embed.DefaultEmbed;
import br.com.bbc.banco.embed.Embeds;
import br.com.bbc.banco.enumeration.BotEnumeration;
import br.com.bbc.banco.model.Transaction;
import br.com.bbc.banco.model.User;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
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
        return embedBuild(author, user, transactions).build();
    }

    private Embeds embedBuild(net.dv8tion.jda.api.entities.User author, User user, List<Transaction> transactions){
        Embeds embed = new DefaultEmbed(author,"💰 Extrato 💰");

        String beforeMessage;
        String message;
        net.dv8tion.jda.api.entities.User userRetrieved;
        Emoji emoji;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yy");

        if(transactions != null){

            Collections.reverse(transactions);
            for (Transaction transaction : transactions) {
                String dateFormated = transaction.getDate().format(formatter);

                if( user.getId().equals(transaction.getUser().getId())){
                    userRetrieved = BotApplication.jda.retrieveUserById(transaction.getOriginUser().getId()).complete();
                    emoji = Emoji.fromMarkdown("<:money_increased:861040590409302026>");
                    message = String.format("de %s", userRetrieved.getName());
                }
                else{
                    userRetrieved = BotApplication.jda.retrieveUserById(transaction.getUser().getId()).complete();
                    emoji = Emoji.fromMarkdown("<:money_decreased:861038910112923668>");
                    message = String.format("para %s", userRetrieved.getName());
                }

                beforeMessage = String.format("%s [%s] %s %.2f",
                        emoji,
                        dateFormated.toUpperCase(),
                        BotEnumeration.CURRENCY.getText(),
                        transaction.getValor()
                );

                embed.addField(beforeMessage, message);

            }

        } else {
            embed.addField("Nenhuma transação disponível",
                    ""
            );
        }

        embed.addField(
                "Saldo",
                String.format("%s %.2f", BotEnumeration.CURRENCY.getText(), user.getSaldo())
        );

        return embed;

    }


}
