package br.com.bbc.banco.command;

import br.com.bbc.banco.embed.Embed;
import br.com.bbc.banco.embed.ErrorEmbed;
import br.com.bbc.banco.embed.SucessEmbed;
import br.com.bbc.banco.exception.ContaJaExisteException;
import lombok.Getter;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

@Component
public class CriarCommand extends Command{

    @Getter private final String name = "criar";
    @Getter private final String description = "Criar uma conta no BBC";

    @Override
    public void execute(SlashCommandEvent event) throws Exception {
        event.replyEmbeds(this.process(event.getUser())).setEphemeral(true).queue();
    }

    @Override
    public void execute(MessageReceivedEvent event) throws Exception {
        event.getChannel().sendMessage(this.process(event.getAuthor())).queue();
    }

    private MessageEmbed process(net.dv8tion.jda.api.entities.User author) {
        Embed embed;
        try{
            this.userService.create(new br.com.bbc.banco.model.User(author.getIdLong()));
            embed = new SucessEmbed(author);
            embed.addField("Sua conta foi criada!", "Digite /saldo para verificar seu saldo.");
        } catch (ContaJaExisteException e){
            embed = new ErrorEmbed(author);
            embed.addField("Sua conta já existe!", "Digite /saldo para verificar seu saldo.");

        }
        return embed.build();
    }
}
