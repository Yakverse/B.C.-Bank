package br.com.bbc.banco.command;

import br.com.bbc.banco.model.User;
import br.com.bbc.banco.service.UserService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class Commands {

    @Autowired
    private UserService userService;

    public User criarUsuario(Long id){
        User user = new User();
        user.setId(id);
        return this.userService.create(user);
    }


    public void mostrarSaldo(GuildMessageReceivedEvent event){
        Long id = event.getMember().getIdLong();
        User user = this.userService.findById(event.getMember().getIdLong());
        if (user == null){
            user = this.criarUsuario(id);
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("💰 Saldo Atual 💰");
        embed.addField(user.getSaldo().toString(), "Você é pobre", false);
        embed.setColor(0x00000);
        embed.setFooter("Solicitado por " + event.getMember().getUser().getName(), event.getMember().getUser().getAvatarUrl());

        event.getChannel().sendMessage(embed.build()).queue();
    }
}
