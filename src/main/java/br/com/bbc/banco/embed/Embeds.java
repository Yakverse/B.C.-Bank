package br.com.bbc.banco.embed;

import br.com.bbc.banco.enumeration.BotEnumeration;
import br.com.bbc.banco.model.User;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Embeds {

    public static EmbedBuilder saldoEmbed(net.dv8tion.jda.api.entities.User author, User user, String mensagem, int cor){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("💰 Saldo Atual 💰");
        embed.addField(String.format("%s %s", BotEnumeration.CURRENCY.getValue(), user.getSaldo().toString()), mensagem, false);
        embed.setColor(cor);
        embed.setFooter("Solicitado por " + author.getName(), author.getAvatarUrl());

        return embed;
    }

    public static EmbedBuilder extratoEmbed(net.dv8tion.jda.api.entities.User author, User user, String mensagem, int cor){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("💰 Saldo Atual 💰");
        embed.addField("EM CONSTRUÇÃO", mensagem, false);
        embed.setColor(cor);
        embed.setFooter("Solicitado por " + author.getName(), author.getAvatarUrl());

        return embed;
    }
}
