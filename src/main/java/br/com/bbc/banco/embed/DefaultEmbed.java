package br.com.bbc.banco.embed;

import br.com.bbc.banco.enumeration.BotEnumeration;

public class DefaultEmbed extends Embed {

    public DefaultEmbed(net.dv8tion.jda.api.entities.User client,String title) {
        super(title, BotEnumeration.BLACK.getNumber(), client);
    }

    public DefaultEmbed(net.dv8tion.jda.api.entities.User client,String title, int color) {
        super(title, color, client);
    }

}
