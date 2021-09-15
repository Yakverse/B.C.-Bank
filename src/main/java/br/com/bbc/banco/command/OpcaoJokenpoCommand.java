package br.com.bbc.banco.command;

import br.com.bbc.banco.configuration.BotApplication;
import br.com.bbc.banco.embed.Embed;
import br.com.bbc.banco.embed.JokenpoEmbed;
import br.com.bbc.banco.enumeration.TransactionType;
import br.com.bbc.banco.exception.PlayerInvalidoException;
import br.com.bbc.banco.model.Jokenpo;
import br.com.bbc.banco.model.Transaction;
import br.com.bbc.banco.model.User;
import br.com.bbc.banco.service.JokenpoService;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OpcaoJokenpoCommand extends Command{

    @Autowired
    private JokenpoService jokenpoService;

    @Override
    public void execute(ButtonClickEvent event) throws Exception{
        MessageEmbed message = this.process(event.getUser(), event.getButton().getId(), Long.parseLong(event.getMessage().getEmbeds().get(0).getFooter().getText().split("#")[1]));
        if(message != null) {
            event.editMessageEmbeds(message).setActionRow(
                    Button.danger("recusarJokenpo", Emoji.fromUnicode("U+2716"))
            ).queue();
        }
    }

    public MessageEmbed process(net.dv8tion.jda.api.entities.User author, String option, Long jokenpoId) throws Exception{
        Jokenpo jokenpo = this.jokenpoService.findById(jokenpoId);

        if(jokenpo.getPlayer2Id() != author.getIdLong() && jokenpo.getPlayer1Id() != author.getIdLong()) throw new PlayerInvalidoException();
        else if(jokenpo.getPlayer1Id() == author.getIdLong() && jokenpo.getPlayer1Pick() == null) jokenpo.setPlayer1Pick(option);
        else if(jokenpo.getPlayer2Id() == author.getIdLong() && jokenpo.getPlayer2Pick() == null) jokenpo.setPlayer2Pick(option);

        jokenpoService.update(jokenpo);

        if(jokenpo.getPlayer1Pick() != null && jokenpo.getPlayer2Pick() != null){

            int winnerNumber = jokenpo.winner();

            //Empate
            if(winnerNumber == 0){
                Embed embed = new JokenpoEmbed(author,jokenpoId);
                embed.addField("Empatou", String.format("Ambos escolheram %s", Emoji.fromUnicode(jokenpo.getPlayer1Pick())));
                return embed.build();
            }

            //Separa Vencedor e Perdedor
            net.dv8tion.jda.api.entities.User winner;
            net.dv8tion.jda.api.entities.User loser;

            if (winnerNumber == 1){
                winner = BotApplication.jda.retrieveUserById(jokenpo.getPlayer1Id()).complete();
                loser = BotApplication.jda.retrieveUserById(jokenpo.getPlayer2Id()).complete();
            }
            else{
                winner = BotApplication.jda.retrieveUserById(jokenpo.getPlayer2Id()).complete();
                loser = BotApplication.jda.retrieveUserById(jokenpo.getPlayer1Id()).complete();
            }

            User userWinner = userService.findOrCreateById(winner.getIdLong());
            User userLoser = userService.findOrCreateById(loser.getIdLong());

            //Faz a transferencia de dinheiro do Perdedor pro Ganhador
            userLoser.transferir(jokenpo.getValue(),userWinner);
            userService.update(userLoser);
            userService.update(userWinner);

            transactionService.create(new Transaction(jokenpo.getValue(),userLoser,userWinner, TransactionType.JOKENPO));


            //Cria o embed de retorno
            Embed embed = new JokenpoEmbed(winner,jokenpoId);

            String message = String.format("%s Ganhou",
                    winner.getName()
            );
            String messageAfter = String.format("mais sorte da proxima vez %s",
                    loser.getName()
            );
            embed.addField(message,messageAfter);

            return embed.build();

        }

        return null;
    }
}
