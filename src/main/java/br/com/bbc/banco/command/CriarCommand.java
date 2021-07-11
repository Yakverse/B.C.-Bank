package br.com.bbc.banco.command;

import br.com.bbc.banco.embed.Embeds;
import br.com.bbc.banco.exception.ContaJaExisteException;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

@Component
public class CriarCommand extends Command{

    public final String name = "criar";
    public final String description = "Criar conta no BBC";

    @Override
    public void execute(SlashCommandEvent event) throws Exception {
        event.replyEmbeds(this.process(event.getUser())).setEphemeral(true).queue();
    }

    @Override
    public void execute(MessageReceivedEvent event) throws Exception {
        event.getChannel().sendMessage(this.process(event.getAuthor())).queue();
    }

    private MessageEmbed process(net.dv8tion.jda.api.entities.User author) throws Exception{
        try{
            this.userService.create(new br.com.bbc.banco.model.User(author.getIdLong()));
            return Embeds.contaCriadaComSucesso(author).build();
        } catch (ContaJaExisteException e){
            return Embeds.contaJaExiste(author).build();
        }
    }
}
