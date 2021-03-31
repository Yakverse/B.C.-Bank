package br.com.bbc.banco;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Commands extends ListenerAdapter {

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        System.out.printf("[%s] Bot Online!%n", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss")));
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split(" ");

        // Saldo
        if((args[0]).equalsIgnoreCase(Bot.prefix + "saldo")){
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("💰 Saldo Atual 💰");
            embed.setDescription("Saldo sla");
            embed.addField("Teste", "AAAA", false);
            embed.setColor(0x00000);
            embed.setFooter("Solicitado por " + event.getMember().getUser().getName(), event.getMember().getUser().getAvatarUrl());

            event.getChannel().sendTyping().queue();
            event.getChannel().sendMessage(embed.build()).queue();
        }
    }
}
