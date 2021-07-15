package br.com.bbc.banco.command;

import br.com.bbc.banco.embed.Embeds;
import br.com.bbc.banco.enumeration.BotEnumeration;
import br.com.bbc.banco.exception.ContaJaExisteException;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import org.springframework.stereotype.Component;

@Component
public class ConviteCommand extends Command{

    @Getter private final String name = "convite";
    @Getter private final String description = "Te envia o convite para o bot";

    @Override
    public void execute(SlashCommandEvent event) throws Exception {
        event.reply(String.format("%s", (Emoji.fromUnicode("\u200E"))))
                .addActionRow(this.process())
                .queue();
    }

    @Override
    public void execute(MessageReceivedEvent event) throws Exception {
        event.getChannel().sendMessage(String.format("%s", (Emoji.fromUnicode("\u200E"))))
                .setActionRow(this.process())
                .queue();
    }

    public net.dv8tion.jda.api.interactions.components.Component process() throws Exception{
        return Button.link(String.format("%s", BotEnumeration.INVITE_LINK.getValue()), "Convite")
                .withEmoji(Emoji.fromMarkdown("<:charlao_normal_icon:861075166553047060>"))
                .withStyle(ButtonStyle.LINK);
    }

}
