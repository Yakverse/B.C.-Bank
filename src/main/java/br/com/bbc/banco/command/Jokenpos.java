package br.com.bbc.banco.command;

import br.com.bbc.banco.configuration.Bot;
import br.com.bbc.banco.embed.Embeds;
import br.com.bbc.banco.enumeration.TransactionType;
import br.com.bbc.banco.exception.PlayerErradoException;
import br.com.bbc.banco.model.Jokenpo;
import br.com.bbc.banco.model.Transaction;
import br.com.bbc.banco.model.User;
import br.com.bbc.banco.service.JokenpoService;
import br.com.bbc.banco.service.TransactionService;
import br.com.bbc.banco.service.UserService;
import br.com.bbc.banco.util.UserUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static br.com.bbc.banco.util.GenericUtils.convertStringToBigDecimalReplacingComma;

@Component
public class Jokenpos {

    @Autowired
    private UserService userService;

    @Autowired
    private JokenpoService jokenpoService;

    @Autowired
    private  UserUtils userUtils;

    @Autowired
    private TransactionService transactionService;


    public User checkUser(net.dv8tion.jda.api.entities.User author){
        Long id = author.getIdLong();
        User user = this.userService.findById(id);
        if (user == null){
            user = this.userService.create(new User(id));
        }
        return user;
    }


    public MessageEmbed jokenpo(net.dv8tion.jda.api.entities.User author, net.dv8tion.jda.api.entities.User other, String valueString){
        User player1 = checkUser(author);
        User player2 = checkUser(other);
        BigDecimal value = convertStringToBigDecimalReplacingComma(valueString);

        Jokenpo jokenpo = new br.com.bbc.banco.model.Jokenpo();
        jokenpo.setPlayer1Id(player1.getId());
        jokenpo.setPlayer2Id(player2.getId());
        jokenpo.setValue(value);

        this.jokenpoService.create(jokenpo);

        return Embeds.criarJokenpoEmbed(author,other,value, jokenpo.getId()).build();
    }


    public MessageEmbed respostaJokenpo(net.dv8tion.jda.api.entities.User author, String jokenpoId, boolean acepted, Message message) throws Exception {
        Jokenpo jokenpo = this.jokenpoService.findById(Long.parseLong(jokenpoId));

        if(jokenpo.getPlayer2Id() != author.getIdLong() && jokenpo.getPlayer1Id() != author.getIdLong()) throw new PlayerErradoException();

        if(!acepted){
            message.delete().queue();
            return null;
        }
        return Embeds.criarJokenpoGameEmbed(jokenpo.getId()).build();
    }


    public MessageEmbed escolheOpcao(net.dv8tion.jda.api.entities.User author, String option, String jokenpoId) throws Exception {
        Jokenpo jokenpo = this.jokenpoService.findById(Long.parseLong(jokenpoId));

        if(jokenpo.getPlayer2Id() != author.getIdLong() && jokenpo.getPlayer1Id() != author.getIdLong()) throw new PlayerErradoException();
        else if(jokenpo.getPlayer1Id() == author.getIdLong() && jokenpo.getPlayer1Pick() == null){
            jokenpo.setPlayer1Pick(option);


        }
        else if(jokenpo.getPlayer2Pick() == null) jokenpo.setPlayer2Pick(option);

        jokenpoService.update(jokenpo);

        if(jokenpo.getPlayer1Pick() != null && jokenpo.getPlayer2Pick() != null){

            int winnerNumber = jokenpoWinner(jokenpo);
            if(winnerNumber == 0) return Embeds.jokenpoEmpate(jokenpoId).build();
            net.dv8tion.jda.api.entities.User winner;
            net.dv8tion.jda.api.entities.User loser;


            if (winnerNumber == 1){
                winner = Bot.jda.retrieveUserById(jokenpo.getPlayer1Id()).complete();
                loser = Bot.jda.retrieveUserById(jokenpo.getPlayer2Id()).complete();
            }
            else{
                winner = Bot.jda.retrieveUserById(jokenpo.getPlayer2Id()).complete();
                loser = Bot.jda.retrieveUserById(jokenpo.getPlayer1Id()).complete();
            }

            User userWinner = userUtils.idToUser(winner.getIdLong());
            User userLoser = userUtils.idToUser(loser.getIdLong());

            userLoser.transferir(jokenpo.getValue(),userWinner);
            userService.update(userLoser);
            userService.update(userWinner);

                transactionService.create(new Transaction(jokenpo.getValue(),userLoser,userWinner, TransactionType.JOKENPO));

            return Embeds.jokenpoGanhador(winner, loser, jokenpoId).build();

        }

        return null;
    }

    private int jokenpoWinner(Jokenpo jokenpo){
        String pick1 = jokenpo.getPlayer1Pick();
        String pick2 = jokenpo.getPlayer2Pick();

        if(pick1.equals(pick2)) return 0;
        switch (pick1){
            case "pedra":
                if(pick2.equals("papel")) return 2;
                return 1;
            case "papel":
                if(pick2.equals("tesoura")) return 2;
                return 1;
            default:
                if(pick2.equals("pedra")) return 2;
                return 1;
        }
    }


    public MessageEmbed replyOption(String pick){
        return Embeds.jokenpoReply(pick).build();
    }


}
