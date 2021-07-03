package br.com.bbc.banco.command;

import br.com.bbc.banco.embed.Embeds;
import br.com.bbc.banco.model.Transaction;
import br.com.bbc.banco.model.User;
import br.com.bbc.banco.service.TransactionService;
import br.com.bbc.banco.service.UserService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.Collection;
import java.util.List;

@Component
public class Commands {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    public User criarUsuario(Long id){
        User user = new User();
        user.setId(id);
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
        return Embeds.saldoEmbed(author, user, "Você é pobre", 0x00000).build();
    }

    public BigDecimal convertStringToBigDecimal(String string){
        string = string.replace(',','.');
        return new BigDecimal(string);
    }

    public void checkValor(BigDecimal valor) throws Exception {
        if (valor.compareTo(BigDecimal.ZERO) <= 0) throw new Exception();
    }

    public void depositar(net.dv8tion.jda.api.entities.User author, String valorString) throws Exception {
        BigDecimal valor = this.convertStringToBigDecimal(valorString);
        this.checkValor(valor);

        User user = checkUser(author);

        BigDecimal saldoAtual = user.getSaldo();
        BigDecimal novoSaldo = saldoAtual.add(valor);
        user.setSaldo(novoSaldo);
        this.userService.update(user);
    }


    public void sacar(net.dv8tion.jda.api.entities.User author, String valorString) throws Exception {
        BigDecimal valor = this.convertStringToBigDecimal(valorString);
        this.checkValor(valor);
        User user = checkUser(author);

        BigDecimal saldoAtual = user.getSaldo();
        BigDecimal novoSaldo = saldoAtual.subtract(valor);
        user.setSaldo(novoSaldo);
        this.userService.update(user);
    }


    public void transferir(net.dv8tion.jda.api.entities.User author, String valorString, net.dv8tion.jda.api.entities.User transferido) throws Exception {
        if(author.getIdLong() == transferido.getIdLong()) throw new Exception();

        BigDecimal valor = this.convertStringToBigDecimal(valorString);
        this.checkValor(valor);

        User user = checkUser(author);
        User posTransferido = checkUser(transferido);

        criaTransacao(valor,user,posTransferido);

        BigDecimal saldoAtual = user.getSaldo();
        BigDecimal novoSaldo = saldoAtual.subtract(valor);
        user.setSaldo(novoSaldo);
        this.userService.update(user);

        saldoAtual = posTransferido.getSaldo();
        novoSaldo = saldoAtual.add(valor);
        posTransferido.setSaldo(novoSaldo);
        this.userService.update(posTransferido);
    }

    public MessageEmbed daily(net.dv8tion.jda.api.entities.User author) throws Exception {
        User user = checkUser(author);
        if (user.getUltimoDaily().until(LocalDateTime.now(), ChronoUnit.DAYS) >= 1) {
            Random rand = new Random();
            Integer valor = Math.round(100 * (rand.nextFloat() + 1));

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


    public void erro(net.dv8tion.jda.api.entities.User author, MessageChannel channel){

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Valor Inválido");
        embed.addField("Coloca um valor válido imbecil","Você é burro",false);
        embed.setColor(0x00000);
        embed.setFooter("Solicitado por " + author.getName(), author.getAvatarUrl());

        channel.sendMessage(embed.build()).queue();
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
}
