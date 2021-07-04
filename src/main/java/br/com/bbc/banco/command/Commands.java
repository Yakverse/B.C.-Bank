package br.com.bbc.banco.command;

import br.com.bbc.banco.embed.Embeds;
import br.com.bbc.banco.model.*;
import br.com.bbc.banco.service.*;
import br.com.bbc.banco.util.GenericUtils;
import br.com.bbc.banco.util.UserUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;

@Component
public class Commands {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private BetService betService;

    @Autowired
    private OptionService optionService;
    
    @Autowired
    private UserUtils userUtils;


    public MessageEmbed mostrarSaldo(net.dv8tion.jda.api.entities.User author){
        User user = userUtils.idToUser(author.getIdLong());
        BigDecimal saldo = user.getSaldo();
        String mensagem = "";
        int cor = 0x00000;

        if (saldo.compareTo(BigDecimal.ZERO) > 0){
            mensagem = "Seu saldo está positivo!";
            cor = 0x80b461;
        } else if (saldo.compareTo(BigDecimal.ZERO) == 0){
            mensagem = "Seu saldo está neutro";
            cor = 0xd0a843;
        } else{
            mensagem = "Seu saldo está negativo!";
            cor = 0x7f2927;
        }

        return Embeds.saldoEmbed(author, user, mensagem, cor).build();
    }

    public void transferir(net.dv8tion.jda.api.entities.User author, String valorString, net.dv8tion.jda.api.entities.User transferido) throws Exception {
        if(author.getIdLong() == transferido.getIdLong()) throw new Exception();

        BigDecimal valor = GenericUtils.convertStringToBigDecimalReplacingComma(valorString);

        User user = userUtils.idToUser(author.getIdLong());
        User para = userUtils.idToUser(transferido.getIdLong());

        user.transferir(valor, para);
        this.userService.update(user);
        this.userService.update(para);
        this.transactionService.update(new Transaction(valor,user,para));
    }

    public MessageEmbed daily(net.dv8tion.jda.api.entities.User author) throws Exception {
        User user = userUtils.idToUser(author.getIdLong());
        if (user.getUltimoDaily().until(LocalDateTime.now(), ChronoUnit.DAYS) >= 1) {
            Random rand = new Random();
            int valor = Math.round(100 * (rand.nextFloat() + 1));

            user.setSaldo(user.getSaldo().add(new BigDecimal(valor)));
            user.setUltimoDaily(LocalDateTime.now());
            this.userService.update(user);

            return Embeds.dailyEmbed(author, user, valor, 0x00000).build();
        }
        long dif = (user.getUltimoDaily().plusDays(1).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - System.currentTimeMillis()) / 1000;

        long segundos = dif % 60;
        long minutos = (dif / 60) % 60;
        long horas = (dif / 3600);

        return Embeds.dailyEmbedError(author, horas, minutos, segundos, 0x00000).build();
    }

    public MessageEmbed mostrarExtrato(net.dv8tion.jda.api.entities.User author){
        User user = userUtils.idToUser(author.getIdLong());

        List<Transaction> transactions = this.transactionService.findByUserId(user.getId());

        return Embeds.extratoEmbed(author, user, transactions, 0x00000).build();
    }

}
