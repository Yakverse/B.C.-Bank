package br.com.bbc.banco.command;

import br.com.bbc.banco.exception.PlayerInvalidoException;
import br.com.bbc.banco.model.Jokenpo;
import br.com.bbc.banco.service.JokenpoService;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RecusaJokenpoCommand extends Command {

    @Autowired
    private JokenpoService jokenpoService;

    @Override
    public void execute(ButtonClickEvent event) throws Exception{
        this.process(event.getUser(), event.getMessage().getEmbeds().get(0).getFooter().getText().split("#")[1], event.getMessage());
    }

    public void process(net.dv8tion.jda.api.entities.User author, String jokenpoId, Message message) throws Exception{
        Jokenpo jokenpo = this.jokenpoService.findById(Long.parseLong(jokenpoId));
        if(jokenpo.getPlayer2Id() != author.getIdLong() && jokenpo.getPlayer1Id() != author.getIdLong()) throw new PlayerInvalidoException();
        message.delete().queue();
    }
}
