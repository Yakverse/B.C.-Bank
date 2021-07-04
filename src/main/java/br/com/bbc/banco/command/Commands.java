package br.com.bbc.banco.command;

import br.com.bbc.banco.embed.Embeds;
import br.com.bbc.banco.model.*;
import br.com.bbc.banco.service.*;
import br.com.bbc.banco.util.GenericUtils;
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
    private JokenpoService jokenpoService;

    public User criarUsuario(Long id){
        User user = new User(id);
        return this.userService.create(user);
    }

    public User checkUser(net.dv8tion.jda.api.entities.User author){
        Long id = author.getIdLong();
        User user = this.userService.findById(id);
        if (user == null){
            user = this.criarUsuario(id);
        }
        return user;
    }


    public MessageEmbed mostrarSaldo(net.dv8tion.jda.api.entities.User author){
        User user = checkUser(author);
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

        User user = checkUser(author);
        User para = checkUser(transferido);

        user.transferir(valor, para);
        this.userService.update(user);
        this.userService.update(para);
        criaTransacao(valor,user,para);
    }

    public MessageEmbed daily(net.dv8tion.jda.api.entities.User author) throws Exception {
        User user = checkUser(author);
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
        User user = checkUser(author);

        List<Transaction> transactions = this.transactionService.findByUserId(user.getId());

        return Embeds.extratoEmbed(author, user, transactions, 0x00000).build();
    }

    public void criaTransacao(BigDecimal valor, User user, User posTransferido ){
        Transaction transaction = new Transaction();
        transaction.setValor(valor);
        transaction.setOriginUser(user);
        transaction.setUser(posTransferido);
        this.transactionService.update(transaction);
    }

    public MessageEmbed criarAposta(net.dv8tion.jda.api.entities.User author, String name, String... options){
        User user = checkUser(author);

        Bet bet = new Bet();
        bet.setNome(name);
        bet.setEndDate(LocalDateTime.now().plusDays(1));
        bet.setCreatedBy(user);
        bet = this.betService.create(bet);

        List<Option> listOption = new ArrayList<>();

        for (int i = 0; i < options.length; i++) {
            Option optionObj = new Option();
            optionObj.setText(options[i]);
            optionObj.setNumber(i);
            optionObj.setBet(bet);
            listOption.add(this.optionService.create(optionObj));
        }

        return Embeds.criarApostaEmbed(author, bet, listOption, 0x00000).build();
    }

    public MessageEmbed apostas(net.dv8tion.jda.api.entities.User author){
        return Embeds.apostasEmbed(author, 0x00000, this.betService.findAll()).build();
    }

    public MessageEmbed jokenpo(net.dv8tion.jda.api.entities.User author, net.dv8tion.jda.api.entities.User other, String valueString){
        User player1 = checkUser(author);
        User player2 = checkUser(other);
        long value = Long.parseLong(valueString);

        Jokenpo jokenpo = new Jokenpo();
        jokenpo.setPlayer1Id(player1.getId());
        jokenpo.setPlayer2Id(player2.getId());
        jokenpo.setValue(value);

        this.jokenpoService.create(jokenpo);

        return Embeds.criaJokenpoEmbed(author,other,value).build();

        //Player2 aceita ou recusa ou ignora o jokenpo
        //Player1 e Player2 Dão suas jogadas ou
    }
}
