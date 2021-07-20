package br.com.bbc.banco.command;

import br.com.bbc.banco.embed.DefaultEmbed;
import br.com.bbc.banco.embed.Embeds;
import br.com.bbc.banco.embed.ErrorEmbed;
import br.com.bbc.banco.embed.SucessEmbed;
import br.com.bbc.banco.model.Bet;
import br.com.bbc.banco.model.Option;
import br.com.bbc.banco.model.UserBet;
import lombok.Getter;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

@Component
public class CancelaAposta extends Command{

    @Getter private final String name = "cancela";
    @Getter private final String description = "Cancela uma aposta";

    @Override
    public void execute(SlashCommandEvent event) throws Exception{
        event.replyEmbeds(this.process(event.getUser(), event.getOption("id_aposta").getAsLong())).setEphemeral(true).queue();
    }

    @Override
    public void execute(MessageReceivedEvent event) throws Exception{
        String[] args = event.getMessage().getContentRaw().split(" ");
        event.getChannel().sendMessage(this.process(event.getAuthor(), Long.parseLong(args[1]))).queue();
    }

    public MessageEmbed process(User author, long betId) throws Exception {
        Bet bet = this.betService.findById(betId);
        if (bet == null){
            Embeds embed = new ErrorEmbed(author,"Não foi encontrada nenhuma aposta ativa com esse ID!");
            embed.addField("Use /apostas para ver as apostas ativas.", "");
            return embed.build();
        }
        if (bet.getCreatedBy().getId() != author.getIdLong()) return new ErrorEmbed(author,"Somente o criador da aposta pode finaliza-la.").build();

        for (Option option : bet.getOptions())
            for (UserBet userBet : option.getUser_bet()){
                br.com.bbc.banco.model.User user = userBet.getUser();
                user.depositar(userBet.getValor());
                this.userService.update(user);
            }

        this.betService.delete(bet);

        return new DefaultEmbed(author,String.format("Aposta [%d] %s cancelada.", bet.getId(), bet.getNome())).build();
    }

}
