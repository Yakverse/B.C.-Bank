package br.com.bbc.banco.embed;

import br.com.bbc.banco.configuration.Bot;
import br.com.bbc.banco.enumeration.BotEnumeration;
import br.com.bbc.banco.model.Bet;
import br.com.bbc.banco.model.Option;
import br.com.bbc.banco.model.Transaction;
import br.com.bbc.banco.model.User;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class Embeds {

    public static EmbedBuilder saldoEmbed(net.dv8tion.jda.api.entities.User author, User user, String mensagem, int cor){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("💰 Saldo Atual 💰");
        embed.addField(String.format("%s %s", BotEnumeration.CURRENCY.getValue(), user.getSaldo().toString()), mensagem, false);
        embed.setColor(cor);
        embed.setFooter("Solicitado por " + author.getName(), author.getAvatarUrl());

        return embed;
    }

    public static EmbedBuilder dailyEmbed(net.dv8tion.jda.api.entities.User author, User user, int valor, int cor){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("💰 Seu ganho do dia 💰");
        embed.addField("+ BC$" + valor, "Saldo: BC$ " + user.getSaldo().toString(), false);
        embed.setColor(cor);
        embed.setFooter("Solicitado por " + author.getName(), author.getAvatarUrl());

        return embed;
    }

    public static EmbedBuilder dailyEmbedError(net.dv8tion.jda.api.entities.User author, long horas, long minutos, long segundos, int cor){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Indisponível!");
        embed.addField("Você ainda precisa esperar:", horas + "h " + minutos + "m " + segundos + "s", true);
        embed.setColor(cor);
        embed.setFooter("Solicitado por " + author.getName(), author.getAvatarUrl());

        return embed;
    }

    public static EmbedBuilder criarApostaEmbed(net.dv8tion.jda.api.entities.User author, Bet bet, List<Option> options, int cor){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(String.format("Aposta %s criada!", bet.getNome()));
        for (Option option : options) {
            embed.addField(String.format("[%d] %s", option.getNumber() + 1, option.getText()), "", false);
        }
        embed.setColor(cor);
        embed.setFooter(String.format("ID da aposta: %d", bet.getId()));

        return embed;
    }

    public static EmbedBuilder criarApostaEmbedError(net.dv8tion.jda.api.entities.User author, int cor){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Informe pelo menos 2 opções!");
        embed.setColor(cor);
        embed.setFooter("Solicitado por " + author.getName(), author.getAvatarUrl());

        return embed;
    }

    public static EmbedBuilder apostasEmbed(net.dv8tion.jda.api.entities.User author, int cor, List<Bet> bets){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Apostas ativas no momento!");
        for (Bet bet : bets) {
            embed.addField(String.format("[%d] %s", bet.getId(), bet.getNome()), "", false);
        }
        embed.setColor(cor);
        embed.setFooter("Solicitado por " + author.getName(), author.getAvatarUrl());

        return embed;
    }

    public static EmbedBuilder extratoEmbed(net.dv8tion.jda.api.entities.User author, User user, List<Transaction> transactions, int cor){

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("💰 Extrato 💰");
        embed.setColor(cor);
        String beforeMessage;
        String mensagem;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yy");

        Collections.reverse(transactions);
        for (Transaction transaction : transactions) {
            String dateFormated = transaction.getDate().format(formatter);

            if( user.getId().equals(transaction.getUser().getId())){
                net.dv8tion.jda.api.entities.User userRetrieved = Bot.jda.retrieveUserById(transaction.getOriginUser().getId()).complete();

                beforeMessage = String.format("%s [%s] +%s %.2f",
                        Emoji.fromUnicode("\uD83D\uDFE2"),
                        dateFormated.toUpperCase(),
                        BotEnumeration.CURRENCY.getValue(),
                        transaction.getValor()
                );

                mensagem = String.format("de %s", userRetrieved.getName()
                );
            }
            else{
                net.dv8tion.jda.api.entities.User userRetrieved = Bot.jda.retrieveUserById(transaction.getUser().getId()).complete();

                beforeMessage = String.format("%s [%s] -%s %.2f",
                        Emoji.fromUnicode("\uD83D\uDD34"),
                        dateFormated.toUpperCase(),
                        BotEnumeration.CURRENCY.getValue(),
                        transaction.getValor()
                );

                mensagem = String.format("para %s",
                        userRetrieved.getName()
                );
            }

            embed.addField(beforeMessage, mensagem, false);
        }



//        embed.addBlankField(false);
        embed.addField(
                String.format("Saldo - %s %.2f", BotEnumeration.CURRENCY.getValue(), user.getSaldo()),
                "",
                false
        );

        embed.setFooter("Solicitado por " + author.getName(), author.getAvatarUrl());

        return embed;
    }
}
