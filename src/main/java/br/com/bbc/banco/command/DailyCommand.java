package br.com.bbc.banco.command;

import br.com.bbc.banco.embed.Embeds;
import br.com.bbc.banco.embed.ErrorEmbed;
import br.com.bbc.banco.embed.SucessEmbed;
import br.com.bbc.banco.enumeration.BotEnumeration;
import br.com.bbc.banco.model.User;
import lombok.Getter;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Random;

@Component
public class DailyCommand extends Command {

    @Getter private final String name = "daily";
    @Getter private final String description = "Recompensa diária";

    @Override
    public void execute(SlashCommandEvent event) throws Exception {
        event.replyEmbeds(this.process(event.getUser())).setEphemeral(true).queue();
    }

    @Override
    public void execute(MessageReceivedEvent event) throws Exception {
        event.getChannel().sendMessage(this.process(event.getAuthor())).queue();
    }

    private MessageEmbed process(net.dv8tion.jda.api.entities.User author) throws Exception{
        User user = userService.findOrCreateById(author.getIdLong());
        if (user.getUltimoDaily().until(LocalDateTime.now(), ChronoUnit.DAYS) >= 1) {
            Random rand = new Random();
            int valor = Math.round(100 * (rand.nextFloat() + 1));

            user.setSaldo(user.getSaldo().add(new BigDecimal(valor)));
            user.setUltimoDaily(LocalDateTime.now());
            this.userService.update(user);

            Embeds embed = new SucessEmbed(author,"💰 Seu ganho do dia 💰");
            embed.addField(
                    String.format("%s %d", BotEnumeration.CURRENCY.getText(), valor),
                    String.format("Saldo: %s %s", BotEnumeration.CURRENCY.getText(), user.getSaldo().toString())
            );
            return embed.build();
        }
        long dif = (user.getUltimoDaily().plusDays(1).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - System.currentTimeMillis()) / 1000;

        long segundos = dif % 60;
        long minutos = (dif / 60) % 60;
        long horas = (dif / 3600);

        Embeds embed = new ErrorEmbed(author,"Indisponivel!");
        embed.addField("Você ainda precisa esperar:", horas + "h " + minutos + "m " + segundos + "s");
        return embed.build();
    }
}
