package br.com.bbc.banco.command;

import br.com.bbc.banco.embed.Embeds;
import br.com.bbc.banco.model.Bet;
import br.com.bbc.banco.model.Option;
import br.com.bbc.banco.model.User;
import br.com.bbc.banco.model.UserBet;
import lombok.Getter;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class FinalizarApostaCommand extends Command{

    @Getter
    private final String name = "finalizar";
    @Getter private final String description = "Finaliza uma aposta";

    @Override
    public void execute(SlashCommandEvent event) throws Exception{
        event.replyEmbeds(this.process(event.getUser(), event.getOption("id_aposta").getAsLong(), event.getOption("numero_opcao").getAsLong())).setEphemeral(true).queue();
    }

    @Override
    public void execute(MessageReceivedEvent event) throws Exception{
        String[] args = event.getMessage().getContentRaw().split(" ");
        event.getChannel().sendMessage(this.process(event.getAuthor(), Long.parseLong(args[1]), Long.parseLong(args[2]))).queue();
    }

    public MessageEmbed process(net.dv8tion.jda.api.entities.User author, long betId, long opcaoId) throws Exception {
        Bet bet = this.betService.findById(betId);
        if (bet == null) return Embeds.apostaEmbedErro(author, betId, 0x00000).build();
        if (bet.getCreatedBy().getId() != author.getIdLong()) return Embeds.apostaFinalizadaEmbedErroAuthor(author, 0x00000).build();
        if (!bet.getIsOpen()) return Embeds.apostaFinalizadaEmbedErroFechada(author, bet, 0x00000).build();

        bet.setEndDate(LocalDateTime.now());
        bet.setIsOpen(false);
        this.betService.update(bet);

        int totalBets = 0;
        Option optionWinner = null;
        for (Option option : bet.getOptions()){
            totalBets += option.getUser_bet().size();
            if (option.getNumber() + 1 == opcaoId) { optionWinner = option; }
        }

        if (optionWinner != null){
            optionWinner.setWinner(true);
            List<UserBet> userBetList = optionWinner.getUser_bet();
            for (UserBet userBet : userBetList){
                User user = userBet.getUser();
                user.depositar(userBet.getValor().add(userBet.getValor().multiply(BigDecimal.valueOf(1 - ((double) userBetList.size() / totalBets)))));
                this.userService.update(user);
            }
            return Embeds.apostaFinalizadaEmbed(author, bet, optionWinner, 0x00000).build();
        }

        return Embeds.apostaFinalizadaEmbedErroOpcao(author, opcaoId, 0x00000).build();
    }
}
