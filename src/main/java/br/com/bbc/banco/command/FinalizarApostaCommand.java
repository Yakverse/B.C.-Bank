package br.com.bbc.banco.command;

import br.com.bbc.banco.configuration.BotApplication;
import br.com.bbc.banco.embed.DefaultEmbed;
import br.com.bbc.banco.embed.Embed;
import br.com.bbc.banco.embed.ErrorEmbed;
import br.com.bbc.banco.embed.SucessEmbed;
import br.com.bbc.banco.enumeration.BotEnumeration;
import br.com.bbc.banco.enumeration.TransactionType;
import br.com.bbc.banco.model.*;
import lombok.Getter;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER;

@Component
public class FinalizarApostaCommand extends Command{

    @Getter private final String name = "finalizar";
    @Getter private final String description = "Finaliza uma aposta";
    @Getter private final List<OptionData> options = Arrays.asList(
            new OptionData(INTEGER, "id_aposta", "ID da aposta").setRequired(true),
            new OptionData(INTEGER, "numero_opcao", "Número da opção na aposta").setRequired(true)
    );

    @Override
    public void execute(SlashCommandEvent event) throws Exception{
        event.replyEmbeds(this.process(event.getUser(), event.getOption("id_aposta").getAsLong(), event.getOption("numero_opcao").getAsLong())).setEphemeral(true).queue();
    }

    @Override
    public void execute(MessageReceivedEvent event) throws Exception{
        String[] args = event.getMessage().getContentRaw().split(" ");
        event.getChannel().sendMessage(this.process(event.getAuthor(), Long.parseLong(args[1]), Long.parseLong(args[2]))).queue();
    }

    public MessageEmbed process(net.dv8tion.jda.api.entities.User author, long betId, long opcaoId) throws Exception {
        Bet bet = this.betService.findById(betId);
        if (bet == null){
            Embed embed = new ErrorEmbed(author,"Não foi encontrada nenhuma aposta ativa com esse ID!");
            embed.addField("Use /apostas para ver as apostas ativas.", "");
            return embed.build();
        }
        if (bet.getCreatedBy().getId() != author.getIdLong()) return new ErrorEmbed(author,"Somente o criador da aposta pode finaliza-la.").build();
        if (!bet.getIsOpen()) return new ErrorEmbed(author,String.format("[%d] %s já foi finalizada!", bet.getId(), bet.getNome())).build();

        bet.setEndDate(LocalDateTime.now());
        bet.setIsOpen(false);
        this.betService.update(bet);

        int totalBets = 0;
        Option optionWinner = null;
        for (Option option : bet.getOptions()){
            totalBets += option.getUser_bet().size();
            if (option.getNumber() + 1 == opcaoId) { optionWinner = option; }
        }

        if (optionWinner != null){
            optionWinner.setWinner(true);
            List<UserBet> userBetList = optionWinner.getUser_bet();
            for (UserBet userBet : userBetList){
                User bot = this.userService.findOrCreateById(BotApplication.jda.getSelfUser().getIdLong());
                User user = userBet.getUser();
                BigDecimal valor = userBet.getValor().add(userBet.getValor().multiply(BigDecimal.valueOf(1 - ((double) userBetList.size() / totalBets))));
                user.depositar(valor);
                this.userService.update(user);
                this.transactionService.update(new Transaction(valor, bot, this.userService.update(user), TransactionType.APOSTA));
                author.openPrivateChannel().queue(privateChannel -> {
                    privateChannel.sendMessage(new SucessEmbed(author,String.format("Você recebeu uma transferência de %s %.2f", BotEnumeration.CURRENCY.getText(), valor)).build()).queue();
                });
            }

            Embed embed = new DefaultEmbed(author,String.format("[%d] %s", bet.getId(), bet.getNome()));
            embed.addField(String.format("[%d] %s declarada vencedora!", optionWinner.getNumber() + 1, optionWinner.getText()), "");
            return embed.build();
        }

        Embed embed = new DefaultEmbed(author,String.format("Opcão [%d] não existe!", opcaoId));
        embed.addField("Use /aposta ou $aposta para ver as opções.", "");
        return embed.build();
    }
}
