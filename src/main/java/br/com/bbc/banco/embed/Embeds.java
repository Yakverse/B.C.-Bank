package br.com.bbc.banco.embed;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import java.util.ArrayList;
import java.util.List;

public abstract class Embeds {

    String title;
    int color;
    List<MessageEmbed.Field> fields = new ArrayList<>();
    net.dv8tion.jda.api.entities.User client;

    public Embeds(String title, int color, net.dv8tion.jda.api.entities.User client){
        this.title = title;
        this.color = color;
        this.client = client;
    }

    public Embeds(String title, int color, net.dv8tion.jda.api.entities.User client, MessageEmbed.Field field){
        this.title = title;
        this.color = color;
        this.client = client;
        this.fields.add(field);
    }

    public Embeds(String title, int color, net.dv8tion.jda.api.entities.User client, List<MessageEmbed.Field> fields){
        this.title = title;
        this.color = color;
        this.client = client;
        this.fields = fields;
    }

    public EmbedBuilder embedBuilder(){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(title);
        embed.setColor(color);

        for (MessageEmbed.Field field: fields) {
            embed.addField(field);
        }
        embed.setFooter("Solicitado por " + client.getName(), client.getAvatarUrl());
        return embed;
    }

    public MessageEmbed build(){
        return embedBuilder().build();
    }

    public void addField(String subtitulo, String texto){
        fields.add(new MessageEmbed.Field(subtitulo, texto, false));
    }

    public void addField(MessageEmbed.Field field){
        fields.add(field);
    }

    public static MessageEmbed.Field makeField(String subtitulo, String texto){
        return new MessageEmbed.Field(subtitulo, texto, false);
    }

}
