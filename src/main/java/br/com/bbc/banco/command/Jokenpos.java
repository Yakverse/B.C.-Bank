package br.com.bbc.banco.command;

import br.com.bbc.banco.embed.Embeds;
import br.com.bbc.banco.model.Jokenpo;
import br.com.bbc.banco.model.User;
import br.com.bbc.banco.service.JokenpoService;
import br.com.bbc.banco.service.UserService;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Jokenpos {

    @Autowired
    private UserService userService;

    @Autowired
    private JokenpoService jokenpoService;


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
        long value = Long.parseLong(valueString);

        Jokenpo jokenpo = new br.com.bbc.banco.model.Jokenpo();
        jokenpo.setPlayer1Id(player1.getId());
        jokenpo.setPlayer2Id(player2.getId());
        jokenpo.setValue(value);

        this.jokenpoService.create(jokenpo);

        return Embeds.criarJokenpoEmbed(author,other,value, jokenpo.getId()).build();
    }


    public MessageEmbed respostaJokenpo(net.dv8tion.jda.api.entities.User author, String jokenpoId, boolean acepted) throws Exception {
        Jokenpo jokenpo = this.jokenpoService.findById(Long.parseLong(jokenpoId));

        if(jokenpo.getPlayer2Id() != author.getIdLong()) throw new Exception("Usuario não é o player correto");

        if(!acepted){
            //deleta messagem
            return null;
        }
        return Embeds.criarJokenpoGameEmbed(jokenpo.getId()).build();
    }


    public MessageEmbed escolheOpcao(net.dv8tion.jda.api.entities.User author, String option, String jokenpoId) throws Exception {
        Jokenpo jokenpo = this.jokenpoService.findById(Long.parseLong(jokenpoId));

        if(jokenpo.getPlayer2Id() != author.getIdLong() && jokenpo.getPlayer1Id() != author.getIdLong()) throw new Exception("Usuario não é o player correto");
        else if(jokenpo.getPlayer1Id() == author.getIdLong() && jokenpo.getPlayer1Pick() == null)jokenpo.setPlayer1Pick(option);
        else if(jokenpo.getPlayer2Pick() == null) jokenpo.setPlayer2Pick(option);

        jokenpoService.update(jokenpo);

        if(jokenpo.getPlayer1Pick() != null && jokenpo.getPlayer2Pick() != null){

            int winner = jokenpoWinner(jokenpo);

            if(winner == 0) return Embeds.jokenpoEmpate().build();
            if(winner == 1) return Embeds.jokenpoGanhador(jokenpo.getPlayer1Id(),jokenpo.getPlayer2Id()).build();
            if(winner == 2) return Embeds.jokenpoGanhador(jokenpo.getPlayer1Id(),jokenpo.getPlayer2Id()).build();

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
            case "tesoura":
                if(pick2.equals("pedra")) return 2;
                return 1;
        }
        //Ta aqui pro java nao reclamar
        return -1;
    }



}
