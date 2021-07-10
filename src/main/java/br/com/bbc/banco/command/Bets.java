package br.com.bbc.banco.command;

import br.com.bbc.banco.embed.Embeds;
import br.com.bbc.banco.model.Bet;
import br.com.bbc.banco.model.Option;
import br.com.bbc.banco.model.User;
import br.com.bbc.banco.model.UserBet;
import br.com.bbc.banco.service.BetService;
import br.com.bbc.banco.service.OptionService;
import br.com.bbc.banco.service.UserBetService;
import br.com.bbc.banco.service.UserService;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class Bets {

    @Autowired
    private UserService userService;

    @Autowired
    private BetService betService;

    @Autowired
    private OptionService optionService;

    @Autowired
    private UserBetService userBetService;

    public MessageEmbed criarAposta(net.dv8tion.jda.api.entities.User author, String name, String... options){
        User user = userService.findOrCreateById(author.getIdLong());

        Bet bet = new Bet();
        bet.setNome(name);
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
        List<Bet> listBet = this.betService.findAll();
        if (listBet == null || listBet.isEmpty()) return Embeds.semApostas(author, 0x00000).build();

        return Embeds.apostasEmbed(author, 0x00000, listBet).build();
    }

    public MessageEmbed apostar(net.dv8tion.jda.api.entities.User author, long betId, long optionId, String valor) throws Exception {
        Bet bet = this.betService.findById(betId);
        if (bet == null) return Embeds.apostarEmbedErroBet(author, 0x00000).build();
        if (!bet.getIsOpen()) return Embeds.apostarEmbedErroFechada(author, bet, bet.getOptions(), 0x00000).build();

        User user = userService.findOrCreateById(author.getIdLong());

        for (Option option : bet.getOptions()) {
            if ((option.getNumber() + 1) == optionId){
                user.sacar(new BigDecimal(valor));
                this.userService.update(user);

                UserBet userBet = this.userBetService.findByOption_idAndUser_id(option.getId(), user.getId());

                if (userBet == null){
                    userBet = new UserBet();
                    userBet.setValor(new BigDecimal(valor));
                    userBet.setUser(user);
                    userBet.setOption(option);
                    this.userBetService.create(userBet);
                } else {
                    userBet.setValor(userBet.getValor().add(new BigDecimal(valor)));
                    this.userBetService.update(userBet);
                }

                return Embeds.apostarEmbed(author, bet, option, valor, 0x00000).build();
            }
        }

        return Embeds.apostarEmbedErroOption(author, bet, optionId, 0x00000).build();
    }

    public MessageEmbed aposta(net.dv8tion.jda.api.entities.User author, long betId){
        Bet bet = this.betService.findById(betId);
        if (bet == null) return Embeds.apostaEmbedErro(author, betId, 0x00000).build();

        List<Integer> listQnt = new ArrayList<Integer>(Collections.nCopies(bet.getOptions().size(), 0));
        List<BigDecimal> listTotal = new ArrayList<BigDecimal>(Collections.nCopies(bet.getOptions().size(), new BigDecimal(0)));

        int totalBets = 0;
        for (Option option : bet.getOptions()) {
            List<UserBet> userBetList = option.getUser_bet();
            if (userBetList != null){
                listQnt.set(option.getNumber(), userBetList.size() );
                for (UserBet userBet : userBetList){
                    totalBets += 1;
                    if (listTotal.get(option.getNumber()) == null) listTotal.set(option.getNumber(), userBet.getValor());
                    else listTotal.set(option.getNumber(), listTotal.get(option.getNumber()).add(userBet.getValor()));
                }
            }
        }

        return Embeds.apostaEmbed(author, bet, bet.getOptions(), totalBets, listQnt, listTotal, 0x00000).build();
    }

    public MessageEmbed finalizaAposta(net.dv8tion.jda.api.entities.User author, long betId, long opcaoId) throws Exception {
        Bet bet = this.betService.findById(betId);
        if (bet == null) return Embeds.apostaEmbedErro(author, betId, 0x00000).build();
        if (bet.getCreatedBy().getId() != author.getIdLong()) return Embeds.apostaFinalizadaEmbedErroAuthor(author, 0x00000).build();
        if (!bet.getIsOpen()) return Embeds.apostaFinalizadaEmbedErroFechada(author, bet, 0x00000).build();

        bet.setEndDate(LocalDateTime.now());
        bet.setIsOpen(false);
        this.betService.update(bet);

        int totalBets = 0;
        Option optionWinner = null;
        for (Option option : bet.getOptions()){
            totalBets += option.getUser_bet().size();
            if (option.getNumber() + 1 == opcaoId) { optionWinner = option; }
        }

        if (optionWinner != null){
            optionWinner.setWinner(true);
            List<UserBet> userBetList = optionWinner.getUser_bet();
            for (UserBet userBet : userBetList){
                User user = userBet.getUser();
                user.depositar(userBet.getValor().add(userBet.getValor().multiply(BigDecimal.valueOf(1 - ((double) userBetList.size() / totalBets)))));
                this.userService.update(user);
            }
            return Embeds.apostaFinalizadaEmbed(author, bet, optionWinner, 0x00000).build();
        }

        return Embeds.apostaFinalizadaEmbedErroOpcao(author, opcaoId, 0x00000).build();
    }
}
