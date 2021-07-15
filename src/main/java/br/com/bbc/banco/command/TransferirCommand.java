package br.com.bbc.banco.command;

import br.com.bbc.banco.embed.Embeds;
import br.com.bbc.banco.enumeration.TransactionType;
import br.com.bbc.banco.exception.ContaJaExisteException;
import br.com.bbc.banco.exception.SaldoInsuficienteException;
import br.com.bbc.banco.exception.ValorInvalidoException;
import br.com.bbc.banco.model.Transaction;
import br.com.bbc.banco.model.User;
import br.com.bbc.banco.util.GenericUtils;
import lombok.Getter;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;
import static net.dv8tion.jda.api.interactions.commands.OptionType.USER;

@Component
public class TransferirCommand extends Command {

    @Getter private final String name = "transferir";
    @Getter private final String description = "Transfeir dinheiro";
    @Getter private final List<OptionData> options = Arrays.asList(
            new OptionData(STRING, "valor", "O quanto você vai transferir").setRequired(true),
            new OptionData(USER, "pessoa", "Pessoa que recebe o dinheiro").setRequired(true)
    );

    @Override
    public void execute(SlashCommandEvent event) throws Exception {
        event.replyEmbeds(this.process(event.getUser(), event.getOption("valor").getAsString(), event.getOption("pessoa").getAsUser())).setEphemeral(true).queue();
    }

    @Override
    public void execute(MessageReceivedEvent event) throws Exception {
        event.getChannel().sendMessage(this.process(event.getAuthor(), event.getMessage().getContentRaw().split(" ")[1], event.getMessage().getMentionedUsers().get(0))).queue();
    }

    private MessageEmbed process(net.dv8tion.jda.api.entities.User author, String valorString, net.dv8tion.jda.api.entities.User transferido) throws Exception{
        try{
            if(author.getIdLong() == transferido.getIdLong()) throw new Exception();
            BigDecimal valor = GenericUtils.convertStringToBigDecimalReplacingComma(valorString);
            User user = userService.findOrCreateById(author.getIdLong());
            User para = userService.findOrCreateById(transferido.getIdLong());
            user.transferir(valor, para);
            this.transactionService.update(new Transaction(valor, this.userService.update(user), this.userService.update(para), TransactionType.TRANFERENCIA));
            transferido.openPrivateChannel().queue(privateChannel -> {
                privateChannel.sendMessage(Embeds.recebimentoDeTranferencia(author, valor).build()).queue();
            });
            return Embeds.transferenciaRealizadaComSucesso(author, valor, transferido).build();
        } catch (SaldoInsuficienteException saldoInsuficienteException){
            return Embeds.saldoInsuficiente(author).build();
        } catch (ValorInvalidoException valorInvalidoException){
            return Embeds.valorInvalido(author).build();
        } catch (Exception e){
            return Embeds.erroAoRelizarTransferencia(author).build();
        }
    }

}
