package br.com.bbc.banco.embed;

import br.com.bbc.banco.model.User;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Embeds {

    public static EmbedBuilder saldoEmbed(MessageReceivedEvent event, User user, String mensagem, int cor){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("💰 Saldo Atual 💰");
        embed.addField("BC$ " + user.getSaldo().toString(), mensagem, false);
        embed.setColor(cor);
        embed.setFooter("Solicitado por " + event.getAuthor().getName(), event.getAuthor().getAvatarUrl());

        return embed;
    }
}
