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

@Component
public class ApostarCommand extends Command{

    @Getter private final String name = "apostar";
    @Getter private final String description = "Aposta em uma opção";

    @Override
    public void execute(SlashCommandEvent event) throws Exception{
        event.replyEmbeds(this.process(event.getUser(), event.getOption("id_aposta").getAsLong(), event.getOption("numero_opcao").getAsLong(), event.getOption("valor").getAsString())).setEphemeral(true).queue();
    }

    @Override
    public void execute(MessageReceivedEvent event) throws Exception{
        String[] args = event.getMessage().getContentRaw().split(" ");
        event.getChannel().sendMessage(this.process(event.getAuthor(), Long.parseLong(args[1]), Long.parseLong(args[2]), args[3])).queue();
    }

    public MessageEmbed process(net.dv8tion.jda.api.entities.User author, long betId, long optionId, String valor) throws Exception {
        Bet bet = this.betService.findById(betId);
        if (bet == null) return Embeds.apostarEmbedErroBet(author, 0x00000).build();
        if (!bet.getIsOpen()) return Embeds.apostarEmbedErroFechada(author, bet, bet.getOptions(), 0x00000).build();

        User user = userService.findOrCreateById(author.getIdLong());

        for (Option option : bet.getOptions()) {
            if ((option.getNumber() + 1) == optionId){
                user.sacar(new BigDecimal(valor));
                this.userService.update(user);

                UserBet userBet = this.userBetService.findByOption_idAndUser_id(option.getId(), user.getId());

                if (userBet == null){
                    userBet = new UserBet();
                    userBet.setValor(new BigDecimal(valor));
                    userBet.setUser(user);
                    userBet.setOption(option);
                    this.userBetService.create(userBet);
                } else {
                    userBet.setValor(userBet.getValor().add(new BigDecimal(valor)));
                    this.userBetService.update(userBet);
                }

                return Embeds.apostarEmbed(author, bet, option, valor, 0x00000).build();
            }
        }

        return Embeds.apostarEmbedErroOption(author, bet, optionId, 0x00000).build();
    }
}