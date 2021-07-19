package br.com.bbc.banco.command;

import br.com.bbc.banco.embed.Embeds;
import br.com.bbc.banco.model.Bet;
import br.com.bbc.banco.model.Option;
import br.com.bbc.banco.model.UserBet;
import lombok.Getter;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class ApostaCommand extends Command {

    @Getter private final String name = "aposta";
    @Getter private final String description = "Descreve uma aposta";

    @Override
    public void execute(SlashCommandEvent event) throws Exception{
        event.replyEmbeds(this.process(event.getUser(), event.getOption("id_aposta").getAsLong())).setEphemeral(true).queue();
    }

    @Override
    public void execute(MessageReceivedEvent event) throws Exception{
        String[] args = event.getMessage().getContentRaw().split(" ");
        event.getChannel().sendMessage(this.process(event.getAuthor(), Long.parseLong(args[1]))).queue();
    }

    public MessageEmbed process(net.dv8tion.jda.api.entities.User author, long betId){
        Bet bet = this.betService.findById(betId);
        if (bet == null) return Embeds.apostaEmbedErro(author, betId, 0x00000).build();

        List<Integer> listQnt = new ArrayList<Integer>(Collections.nCopies(bet.getOptions().size(), 0));
        List<BigDecimal> listTotal = new ArrayList<BigDecimal>(Collections.nCopies(bet.getOptions().size(), new BigDecimal(0)));

        int totalBets = 0;
        for (Option option : bet.getOptions()) {
            List<UserBet> userBetList = option.getUser_bet();
            if (userBetList != null){
                listQnt.set(option.getNumber(), userBetList.size() );
                for (UserBet userBet : userBetList){
                    totalBets += 1;
                    if (listTotal.get(option.getNumber()) == null) listTotal.set(option.getNumber(), userBet.getValor());
                    else listTotal.set(option.getNumber(), listTotal.get(option.getNumber()).add(userBet.getValor()));
                }
            }
        }

        return Embeds.apostaEmbed(author, bet, bet.getOptions(), totalBets, listQnt, listTotal, 0x00000).build();
    }
}