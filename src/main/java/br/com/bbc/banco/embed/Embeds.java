package br.com.bbc.banco.embed;

import br.com.bbc.banco.configuration.Bot;
import br.com.bbc.banco.enumeration.BotEnumeration;
import br.com.bbc.banco.model.Transaction;
import br.com.bbc.banco.model.User;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.time.format.DateTimeFormatter;
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

    public static EmbedBuilder extratoEmbed(net.dv8tion.jda.api.entities.User author, User user, List<Transaction> transactions, int cor){

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("💰 Extrato 💰");
        embed.setColor(cor);
        String mensagem;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy hh:mm:ss");


        for (Transaction transaction : transactions) {
            String dateFormated = transaction.getDate().format(formatter);

            if( user.getId().equals(transaction.getUser().getId())){
                mensagem = String.format("Você recebeu %s %.2f de %s em %s",
                        BotEnumeration.CURRENCY.getValue(), transaction.getValor(),
                        Bot.jda.getUserById(transaction.getOriginUser().getId()).getName(), dateFormated
                );
            }
            else{
                mensagem = String.format("Você enviou %s %.2f para %s em %s",
                        BotEnumeration.CURRENCY.getValue(), transaction.getValor(),
                        Bot.jda.getUserById(transaction.getUser().getId()).getName(), dateFormated
                );
            }

            embed.addField("", mensagem, false);
        }

        embed.setFooter("Solicitado por " + author.getName(), author.getAvatarUrl());

        return embed;
    }
}
