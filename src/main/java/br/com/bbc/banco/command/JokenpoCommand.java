package br.com.bbc.banco.command;

import br.com.bbc.banco.configuration.BotApplication;
import br.com.bbc.banco.embed.Embed;
import br.com.bbc.banco.embed.JokenpoEmbed;
import br.com.bbc.banco.enumeration.BotEnumeration;
import br.com.bbc.banco.enumeration.TransactionType;
import br.com.bbc.banco.exception.PlayerInvalidoException;
import br.com.bbc.banco.model.Jokenpo;
import br.com.bbc.banco.model.Transaction;
import br.com.bbc.banco.model.User;
import br.com.bbc.banco.service.JokenpoService;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.Button;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static br.com.bbc.banco.util.GenericUtils.convertStringToBigDecimalReplacingComma;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;
import static net.dv8tion.jda.api.interactions.commands.OptionType.USER;

@Component
public class JokenpoCommand extends Command{

    @Getter private final String name = "jokenpo";
    @Getter private final String description = "jogar jokenpo";
    @Getter private final List<OptionData> options = Arrays.asList(
            new OptionData(USER, "pessoa", "Pessoa você quer desafiar").setRequired(true),
            new OptionData(STRING, "valor", "Valor da aposta").setRequired(true)
    );

    @Override
    public void execute(SlashCommandEvent event) throws Exception{
        event.replyEmbeds(this.process(event.getUser(), event.getOption("valor").getAsString(), event.getOption("pessoa").getAsUser()))
            .addActionRow(
                    Button.primary("aceitarJokenpo", Emoji.fromUnicode("U+2714")),
                    Button.danger("recusarJokenpo", Emoji.fromUnicode("U+2716"))
            ).queue();
    }

    private MessageEmbed process(net.dv8tion.jda.api.entities.User author, String valueString, net.dv8tion.jda.api.entities.User other) throws Exception {
        if(author.getIdLong() == other.getIdLong()) throw new PlayerInvalidoException();


        User player1 = userService.findOrCreateById(author.getIdLong());
        User player2 = userService.findOrCreateById(other.getIdLong());
        BigDecimal value = convertStringToBigDecimalReplacingComma(valueString);

        Jokenpo jokenpo = new br.com.bbc.banco.model.Jokenpo();
        jokenpo.setPlayer1Id(player1.getId());
        jokenpo.setPlayer2Id(player2.getId());
        jokenpo.setValue(value);

        this.jokenpoService.create(jokenpo);

        Embed embed = new JokenpoEmbed(other, jokenpo.getId());

        String message = String.format("%s te desafiou!",author.getName());

        String underMessage = String.format("Valor: %s %.2f",
                BotEnumeration.CURRENCY.getText(),
                value
        );

        embed.addField(message,underMessage);

        return embed.build();
    }

    public void recusaJokenpo(ButtonClickEvent event) throws PlayerInvalidoException {
        Jokenpo jokenpo = this.jokenpoService.findById(Long.parseLong(event.getMessage().getEmbeds().get(0).getFooter().getText().split("#")[1]));
        if(jokenpo.getPlayer2Id() != event.getUser().getIdLong() && jokenpo.getPlayer1Id() != event.getUser().getIdLong()) throw new PlayerInvalidoException();
        event.getMessage().delete().queue();
    }

    public void aceitaJokenpo(ButtonClickEvent event) throws PlayerInvalidoException {
        Jokenpo jokenpo = this.jokenpoService.findById(Long.parseLong(event.getMessage().getEmbeds().get(0).getFooter().getText().split("#")[1]));
        if(jokenpo.getPlayer2Id() != event.getUser().getIdLong()) throw new PlayerInvalidoException();

        Embed embed = new JokenpoEmbed(event.getUser(), Long.parseLong(event.getMessage().getEmbeds().get(0).getFooter().getText().split("#")[1]));
        embed.addField("Escolha uma das opçoes abaixo","");

        event.editMessageEmbeds(embed.build())
            .setActionRow(
                    Button.secondary("U+270A", Emoji.fromUnicode("U+270A")),
                    Button.secondary("U+270B",Emoji.fromUnicode("U+270B")),
                    Button.secondary("U+270C",Emoji.fromUnicode("U+270C"))
            ).queue();

    }

    public void opcaoJokenpo(ButtonClickEvent event) throws Exception {
        MessageEmbed message = this.processOpcaoJokenpo(event.getUser(), event.getButton().getId(), Long.parseLong(event.getMessage().getEmbeds().get(0).getFooter().getText().split("#")[1]));
        if(message != null) {
            event.editMessageEmbeds(message).setActionRow(
                    Button.danger("recusarJokenpo", Emoji.fromUnicode("U+2716"))
            ).queue();
        }
    }

    private MessageEmbed processOpcaoJokenpo(net.dv8tion.jda.api.entities.User author, String option, Long jokenpoId) throws Exception{
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
