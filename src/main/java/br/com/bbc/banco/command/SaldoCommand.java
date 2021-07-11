package br.com.bbc.banco.command;

import br.com.bbc.banco.embed.Embeds;
import br.com.bbc.banco.model.User;
import br.com.bbc.banco.service.UserService;
import lombok.Getter;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class SaldoCommand extends Command{

    public final String name = "saldo";
    public final String description = "Mostra o seu saldo";

    @Override
    public void execute(SlashCommandEvent event) throws Exception{
        event.replyEmbeds(this.process(event.getUser())).setEphemeral(true).queue();
    }

    @Override
    public void execute(MessageReceivedEvent event) throws Exception{
        event.getChannel().sendMessage(this.process(event.getAuthor())).queue();
    }

    private MessageEmbed process(net.dv8tion.jda.api.entities.User author){
        User user = this.userService.findOrCreateById(author.getIdLong());
        BigDecimal saldo = user.getSaldo();

        String mensagem = "";
        int cor = 0x00000;
        if (saldo.compareTo(BigDecimal.ZERO) > 0){
            mensagem = "Seu saldo está positivo!";
            cor = 0x80b461;
        } else if (saldo.compareTo(BigDecimal.ZERO) == 0){
            mensagem = "Seu saldo está neutro";
            cor = 0xd0a843;
        } else{
            mensagem = "Seu saldo está negativo!";
            cor = 0x7f2927;
        }

        return Embeds.saldoEmbed(author, user, mensagem, cor).build();
    }
}
