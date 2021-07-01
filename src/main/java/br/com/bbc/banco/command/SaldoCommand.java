package br.com.bbc.banco.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class SaldoCommand {

    public static void mostrarSaldo(GuildMessageReceivedEvent event){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("💰 Saldo Atual 💰");
        embed.addField("BC$ 1000,00", "Você é pobre", false);
        embed.setColor(0x00000);
        embed.setFooter("Solicitado por " + event.getMember().getUser().getName(), event.getMember().getUser().getAvatarUrl());

        event.getChannel().sendMessage(embed.build()).queue();
    }
}
