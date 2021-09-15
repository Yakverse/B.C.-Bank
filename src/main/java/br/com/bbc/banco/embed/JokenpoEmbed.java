package br.com.bbc.banco.embed;

import br.com.bbc.banco.enumeration.BotEnumeration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class JokenpoEmbed extends Embed {
    long gameId;

    public JokenpoEmbed(net.dv8tion.jda.api.entities.User client , long gameId){
        super(String.format("%s Jokenpo %s",
                Emoji.fromUnicode("U+270A"),
                Emoji.fromUnicode("U+270B")
             ), BotEnumeration.GREEN.getNumber(), client);
        this.gameId = gameId;
    }

    public MessageEmbed build(){
        EmbedBuilder embed = super.embedBuilder();
        String footer = String.format("Enviado para %s\nGameId#%d",client.getName(), gameId);
        embed.setFooter(footer,client.getAvatarUrl());
        return embed.build();
    }

}
