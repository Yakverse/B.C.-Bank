package br.com.bbc.banco.event;

import br.com.bbc.banco.command.*;
import br.com.bbc.banco.embed.Embeds;
import br.com.bbc.banco.enumeration.BotEnumeration;
import br.com.bbc.banco.exception.SaldoInsuficienteException;
import br.com.bbc.banco.exception.ValorInvalidoException;
import br.com.bbc.banco.service.UserService;
import br.com.bbc.banco.util.Extras;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class Events extends ListenerAdapter {

    @Autowired
    private Bets bets;

    @Autowired
    private Jokenpos jokenpos;

    @Autowired
    private UserService userService;

    @Autowired
    private SaldoCommand saldoCommand;

    @Autowired
    private CriarCommand criarCommand;

    @Autowired
    private ConviteCommand conviteCommand;

    @Autowired
    private TransferirCommand transferirCommand;

    @Autowired
    private ExtratoCommand extratoCommand;

    @Autowired
    private DailyCommand dailyCommand;

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        Extras.asciibbc();
        log.info("Bot Online!");
    }

    @SneakyThrows
    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        switch (event.getName()){

            case "convite":
                this.conviteCommand.execute(event);
                break;

            case "saldo":
                this.saldoCommand.execute(event);
                break;

            case "criar":
                this.criarCommand.execute(event);
                break;

            case "transferir":
                this.transferirCommand.execute(event);
                break;

            case "daily":
                this.dailyCommand.execute(event);
                break;

            case "extrato":
                this.extratoCommand.execute(event);
                break;

            case "criaraposta":
                event.replyEmbeds(bets.criarAposta(event.getUser(), event.getOption("nome").getAsString(), event.getOption("opcao1").getAsString(), event.getOption("opcao2").getAsString())).queue();
                break;

            case "apostas":
                event.replyEmbeds(bets.apostas(event.getUser())).setEphemeral(true).queue();
                break;

            case "apostar":
                event.replyEmbeds(bets.apostar(event.getUser(), event.getOption("id_aposta").getAsLong(), event.getOption("numero_opcao").getAsLong(), event.getOption("valor").getAsString())).setEphemeral(true).queue();
                break;

            case "aposta":
                event.replyEmbeds(bets.aposta(event.getUser(), event.getOption("id_aposta").getAsLong())).setEphemeral(true).queue();
                break;

            case "finalizar":
                event.replyEmbeds(bets.finalizaAposta(event.getUser(), event.getOption("id_aposta").getAsLong(), event.getOption("numero_opcao").getAsLong())).setEphemeral(true).queue();
                break;

            case "jokenpo":
                event.replyEmbeds(jokenpos.jokenpo(event.getUser(),event.getOption("pessoa").getAsUser(), event.getOption("valor").getAsString()))
                        .addActionRow(
                            Button.primary("aceitarJokenpo", Emoji.fromUnicode("U+2714")),
                            Button.danger("recusarJokenpo", Emoji.fromUnicode("U+2716"))
                        ).queue();
                break;
        }
    };

    @SneakyThrows
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getMessage().getAuthor().isBot()) return;
        if (!event.getMessage().getAttachments().isEmpty()) return;

        String[] args = event.getMessage().getContentRaw().split(" ");
        String firstWord = args[0].substring(1);

        net.dv8tion.jda.api.entities.User author = event.getAuthor();
        Message message = event.getMessage();
        MessageChannel channel = event.getChannel();

        if(args[0].startsWith(BotEnumeration.PREFIX.getValue())) {

            if (firstWord.equalsIgnoreCase("extrato")) {
                this.extratoCommand.execute(event);
            }

            if (firstWord.equalsIgnoreCase("convite")) {
                this.conviteCommand.execute(event);
            }

            // Criar conta
            if (firstWord.equalsIgnoreCase("criar")) {
                this.criarCommand.execute(event);
            }

            // Saldo
            if (firstWord.equalsIgnoreCase("saldo")) {
                this.saldoCommand.execute(event);
            }

            //Transferir
            if (firstWord.equalsIgnoreCase("transferir")) {
                if (args.length > 3) throw new Exception();
                if (event.getMessage().getMentionedUsers().size() > 1) throw new Exception();
                this.transferirCommand.execute(event);
            }

            //Daily
            if (firstWord.equalsIgnoreCase("daily")){
                this.dailyCommand.execute(event);
            }

            //Criar Aposta
            if (firstWord.equalsIgnoreCase("criaraposta")){
                if (args.length < 4) channel.sendMessage(Embeds.criarApostaEmbedError(author, 0x00000).build()).queue();
                else {
                    String nome = args[1];

                    List<String> list = Arrays.asList(args);
                    list = list.subList(2, args.length);

                    String[] newarr = new String[list.size()];
                    list.toArray(newarr);

                    channel.sendMessage(bets.criarAposta(author, nome, newarr)).queue();
                }
            }

            //Apostas
            if (firstWord.equalsIgnoreCase("apostas")){
                channel.sendMessage(bets.apostas(author)).queue();
            }

            //Apostar
            if (firstWord.equalsIgnoreCase("apostar")){
                channel.sendMessage(bets.apostar(author, Long.parseLong(args[1]), Long.parseLong(args[2]), args[3])).queue();
            }

            //Aposta
            if (firstWord.equalsIgnoreCase("aposta")){
                channel.sendMessage(bets.aposta(author, Long.parseLong(args[1]))).queue();
            }

            //FinalizarAposta
            if (firstWord.equalsIgnoreCase("finalizar")){
                channel.sendMessage(bets.finalizaAposta(author, Long.parseLong(args[1]), Long.parseLong(args[2]))).queue();
            }

        }
    }

    @SneakyThrows
    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event){
        switch (event.getButton().getId()){
            case "aceitarJokenpo":
                event.editMessageEmbeds(jokenpos.respostaJokenpo(event.getUser(),event.getMessage().getEmbeds().get(0).getFooter().getText().split("#")[1],true,event.getMessage()))
                    .setActionRow(
                        Button.secondary("pedraJokenpo",Emoji.fromUnicode("U+270A")),
                        Button.secondary("papelJokenpo",Emoji.fromUnicode("U+270B")),
                        Button.secondary("tesouraJokenpo",Emoji.fromUnicode("U+270C"))
                    ).queue();
                break;
            case "recusarJokenpo":
                jokenpos.respostaJokenpo(event.getUser(),event.getMessage().getEmbeds().get(0).getFooter().getText().split("#")[1],false, event.getMessage());
                break;

            case "pedraJokenpo":
            case "papelJokenpo":
            case "tesouraJokenpo":
                MessageEmbed messageEmbed = jokenpos.escolheOpcao(event.getUser(),event.getButton().getId().split("Jokenpo")[0],event.getMessage().getEmbeds().get(0).getFooter().getText().split("#")[1]);
                if(messageEmbed != null){
                    event.editMessageEmbeds(messageEmbed).setActionRow(
                            Button.danger("recusarJokenpo", Emoji.fromUnicode("U+2716"))
                    ).queue();
                }
                else{
                    event.replyEmbeds(jokenpos.replyOption(event.getButton().getId())).setEphemeral(true).queue();
                }
        }
    }
}