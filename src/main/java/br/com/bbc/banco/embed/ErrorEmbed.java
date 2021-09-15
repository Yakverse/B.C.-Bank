package br.com.bbc.banco.embed;

import br.com.bbc.banco.enumeration.BotEnumeration;

public class ErrorEmbed extends Embed {

    public ErrorEmbed(net.dv8tion.jda.api.entities.User client){
        super("Erro",BotEnumeration.RED.getNumber(),client);
    }

    public ErrorEmbed(net.dv8tion.jda.api.entities.User client,String title){
        super(title,BotEnumeration.RED.getNumber(),client);
    }

}
