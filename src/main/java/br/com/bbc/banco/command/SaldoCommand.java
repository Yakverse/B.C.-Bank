package br.com.bbc.banco.command;

import br.com.bbc.banco.embed.DefaultEmbed;
import br.com.bbc.banco.embed.Embed;
import br.com.bbc.banco.enumeration.BotEnumeration;
import br.com.bbc.banco.model.User;
import lombok.Getter;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class SaldoCommand extends Command{

    @Getter private final String name = "saldo";
    @Getter private final String description = "Mostra o seu saldo atual";

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

        String subtitulo = String.format("%s %s", BotEnumeration.CURRENCY.getText(), user.getSaldo().toString());
        String mensagem;
        int cor;

        if (saldo.compareTo(BigDecimal.ZERO) >= 0){
            mensagem = "Seu saldo está positivo!";
            cor = BotEnumeration.GREEN.getNumber();
        } else{
            mensagem = "Seu saldo está negativo!";
            cor = BotEnumeration.RED.getNumber();
        }

        Embed embed = new DefaultEmbed(author,"💰 Saldo Atual 💰",cor);
        embed.addField(subtitulo,mensagem);

        return embed.build();
    }
}
