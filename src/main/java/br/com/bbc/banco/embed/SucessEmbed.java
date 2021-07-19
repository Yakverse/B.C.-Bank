package br.com.bbc.banco.embed;

import br.com.bbc.banco.enumeration.BotEnumeration;

public class SucessEmbed extends Embeds{

    public SucessEmbed(net.dv8tion.jda.api.entities.User client){
        super("Sucesso!",BotEnumeration.GREEN.getNumber(), client);
    }

    public SucessEmbed(net.dv8tion.jda.api.entities.User client, String title){
        super(title,BotEnumeration.GREEN.getNumber(), client);
    }

}
