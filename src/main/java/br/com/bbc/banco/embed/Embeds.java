package br.com.bbc.banco.embed;

import br.com.bbc.banco.model.User;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Embeds {

    public static EmbedBuilder saldoEmbed(net.dv8tion.jda.api.entities.User author, User user, String mensagem, int cor){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("💰 Saldo Atual 💰");
        embed.addField("BCT$ " + user.getSaldo().toString(), mensagem, false);
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
}
