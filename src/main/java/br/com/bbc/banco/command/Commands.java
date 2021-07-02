package br.com.bbc.banco.command;

import br.com.bbc.banco.embed.Embeds;
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


    public void mostrarSaldo(MessageReceivedEvent event){
        User user = checkUser(event);

        EmbedBuilder embed = Embeds.saldoEmbed(event, user, "Você é pobre", 0x00000);
        event.getChannel().sendMessage(embed.build()).queue();
    }

    public void depositar(MessageReceivedEvent event, BigDecimal valor){
        User user = checkUser(event);

        BigDecimal saldoAtual = user.getSaldo();
        BigDecimal novoSaldo = saldoAtual.add(valor);
        user.setSaldo(novoSaldo);
        this.userService.update(user, event.getMember().getIdLong());

        this.mostrarSaldo(event);
    }


    public void sacar(MessageReceivedEvent event, BigDecimal valor){
        User user = checkUser(event);

        BigDecimal saldoAtual = user.getSaldo();
        BigDecimal novoSaldo = saldoAtual.subtract(valor);
        user.setSaldo(novoSaldo);
        this.userService.update(user, event.getMember().getIdLong());

        this.mostrarSaldo(event);
    }

    public void transferir(MessageReceivedEvent event, BigDecimal valor, net.dv8tion.jda.api.entities.User transferido){
        User user = checkUser(event);
        User posTransferido = checkUserDiscord(transferido);

        BigDecimal saldoAtual = user.getSaldo();
        BigDecimal novoSaldo = saldoAtual.subtract(valor);
        user.setSaldo(novoSaldo);
        this.userService.update(user, user.getId());

        saldoAtual = posTransferido.getSaldo();
        novoSaldo = saldoAtual.add(valor);
        posTransferido.setSaldo(novoSaldo);
        this.userService.update(posTransferido, posTransferido.getId());


    }


    public void erro(MessageReceivedEvent event){

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Valor Inválido");
        embed.addField("Coloca um valor válido imbecil","Você é burro",false);
        embed.setColor(0x00000);
        embed.setFooter("Solicitado por " + event.getMember().getUser().getName(), event.getMember().getUser().getAvatarUrl());

        event.getChannel().sendMessage(embed.build()).queue();
    }

}
