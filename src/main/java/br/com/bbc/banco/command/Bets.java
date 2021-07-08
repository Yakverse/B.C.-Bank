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
        return Embeds.apostasEmbed(author, 0x00000, this.betService.findAll()).build();
    }

    public MessageEmbed apostar(net.dv8tion.jda.api.entities.User author, long betId, long optionId, String valor) throws Exception {
        User user = userService.findOrCreateById(author.getIdLong());
        Bet bet = this.betService.findById(betId);
        if (bet == null) return Embeds.apostarEmbedErroBet(author, 0x00000).build();

        UserBet userBet = new UserBet();
        userBet.setValor(new BigDecimal(valor));
        userBet.setUser(user);

        for (Option option : bet.getOptions()) {
            if ((option.getNumber() + 1) == optionId){
                user.sacar(new BigDecimal(valor));
                this.userService.update(user);

                userBet.setOption(option);
                this.userBetService.create(userBet);
                return Embeds.apostarEmbed(author, bet, option, valor, 0x00000).build();
            }
        }

        return Embeds.apostarEmbedErroOption(author, bet, optionId, 0x00000).build();
    }
}
