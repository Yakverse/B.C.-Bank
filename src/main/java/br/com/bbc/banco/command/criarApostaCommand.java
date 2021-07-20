package br.com.bbc.banco.command;

import br.com.bbc.banco.embed.Embeds;
import br.com.bbc.banco.embed.ErrorEmbed;
import br.com.bbc.banco.embed.SucessEmbed;
import br.com.bbc.banco.model.Bet;
import br.com.bbc.banco.model.Option;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class criarApostaCommand extends Command{

    @Getter private final String name = "criaraposta";
    @Getter private final String description = "Cria uma aposta";

    @Override
    public void execute(SlashCommandEvent event) throws Exception{
        event.replyEmbeds(this.process(event.getUser(), event.getOption("nome").getAsString(), event.getOption("opcao1").getAsString(), event.getOption("opcao2").getAsString())).setEphemeral(true).queue();
    }

    @Override
    public void execute(MessageReceivedEvent event) throws Exception{
        String[] args = event.getMessage().getContentRaw().split(" ");

        if (args.length < 4){
            Embeds embed = new ErrorEmbed(event.getAuthor(),"Informe pelo menos 2 opções!");
            event.getChannel().sendMessage(embed.build()).queue();
        }
        else {
            String nome = args[1];

            List<String> list = Arrays.asList(args);
            list = list.subList(2, args.length);

            String[] newarr = new String[list.size()];
            list.toArray(newarr);

            event.getChannel().sendMessage(this.process(event.getAuthor(), nome, newarr)).queue();
        }
    }

    private MessageEmbed process(User author, String name, String... options){
        br.com.bbc.banco.model.User user = userService.findOrCreateById(author.getIdLong());

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
}
