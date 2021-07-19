package br.com.bbc.banco.embed;

import br.com.bbc.banco.enumeration.BotEnumeration;

public class DefaultEmbed extends Embeds{

    public DefaultEmbed(net.dv8tion.jda.api.entities.User client,String title) {
        super(title, BotEnumeration.BLACK.getNumber(), client);
    }

}
