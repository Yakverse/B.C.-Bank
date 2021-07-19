package br.com.bbc.banco.command;

import br.com.bbc.banco.embed.DefaultEmbed;
import br.com.bbc.banco.embed.Embeds;
import br.com.bbc.banco.embed.ErrorEmbed;
import br.com.bbc.banco.embed.SucessEmbed;
import br.com.bbc.banco.enumeration.BotEnumeration;
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

        String title = String.format("Aposta %s criada!", bet.getNome());
        Embeds embed = new SucessEmbed(author,title);

        for (Option option: listOption) {
            embed.addField(String.format("[%d] %s", option.getNumber() + 1, option.getText()), "");
        }
        embed.addField("",String.format("ID da aposta: %d", bet.getId()));

        return embed.build();
    }

    public MessageEmbed apostas(net.dv8tion.jda.api.entities.User author){
        List<Bet> listBet = this.betService.findAll();
        if (listBet == null || listBet.isEmpty()){
            return new DefaultEmbed(author,"Nenhuma aposta ativa no momento.").build();
        }
        Embeds embed = new DefaultEmbed(author,"Apostas ativas no momento!");
        for (Bet bet : listBet) {
            embed.addField(String.format("[%d] %s", bet.getId(), bet.getNome()), "");
        }
        return embed.build();
    }

    public MessageEmbed apostar(net.dv8tion.jda.api.entities.User author, long betId, long optionId, String valor) throws Exception {
        Bet bet = this.betService.findById(betId);
        if (bet == null) return new ErrorEmbed(author,"Não foi encontrada nenhuma aposta ativa com esse ID!").build();
        if (!bet.getIsOpen()){
            Embeds embed = new ErrorEmbed(author,String.format("%s já foi fechada!", bet.getNome()));
            for (Option option : bet.getOptions()) {
                if (option.isWinner()){
                    embed.addField("A opção vencedora foi:", String.format("[%d] %s", option.getNumber(), option.getText()));
                    return embed.build();
                }
            }
        }

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

                Embeds embed = new DefaultEmbed(author,"Aposta registrada!");
                embed.addField(String.format("Aposta %s:", bet.getNome()), "");
                embed.addField(String.format("Opção [%d] %s", option.getNumber() + 1, option.getText()), "");
                return embed.build();
            }
        }

        Embeds embed = new ErrorEmbed(author,"Opção %d não encontrada!");
        embed.addField(String.format("Opções da aposta %s", bet.getNome()), "");
        for (Option option : bet.getOptions()){
            embed.addField(String.format("[%d] %s", option.getNumber() + 1, option.getText()), "");
        }
        return embed.build();
    }

    public MessageEmbed aposta(net.dv8tion.jda.api.entities.User author, long betId){
        Bet bet = this.betService.findById(betId);
        if (bet == null){
            Embeds embed = new ErrorEmbed(author,"Não foi encontrada nenhuma aposta ativa com esse ID!");
            embed.addField("Use /apostas para ver as apostas ativas.", "");
            return embed.build();
        }

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

        Embeds embed = new DefaultEmbed(author,String.format("[%d] %s", bet.getId(), bet.getNome()));

        for (Option option : bet.getOptions()) {
            String message = String.format("[%d] %s:", option.getNumber() + 1, option.getText());
            String afterMessage = String.format("%d%% - %s%.2f", Math.round(((double) listQnt.get(option.getNumber()) / totalBets) * 100), BotEnumeration.CURRENCY.getText(), listTotal.get(option.getNumber()));
            embed.addField(message, afterMessage);
        }

        return embed.build();
    }

    public MessageEmbed finalizaAposta(net.dv8tion.jda.api.entities.User author, long betId, long opcaoId) throws Exception {
        Bet bet = this.betService.findById(betId);
        if (bet == null){
            Embeds embed = new ErrorEmbed(author,"Não foi encontrada nenhuma aposta ativa com esse ID!");
            embed.addField("Use /apostas para ver as apostas ativas.", "");
            return embed.build();
        }
        if (bet.getCreatedBy().getId() != author.getIdLong()) return new ErrorEmbed(author,"Somente o criador da aposta pode finaliza-la.").build();
        if (!bet.getIsOpen()) return new ErrorEmbed(author,String.format("[%d] %s já foi finalizada!", bet.getId(), bet.getNome())).build();


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

            Embeds embed = new DefaultEmbed(author,String.format("[%d] %s", bet.getId(), bet.getNome()));
            embed.addField(String.format("[%d] %s declarada vencedora!", optionWinner.getNumber() + 1, optionWinner.getText()), "");
            return embed.build();
        }
        Embeds embed = new DefaultEmbed(author,String.format("Opcão [%d] não existe!", opcaoId));
        embed.addField("Use /aposta ou $aposta para ver as opções.", "");
        return embed.build();
    }
}
