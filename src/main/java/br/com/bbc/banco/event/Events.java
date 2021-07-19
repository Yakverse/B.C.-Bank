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

    @Autowired
    private criarApostaCommand criarApostaCommand;

    @Autowired
    private ApostasCommand apostasCommand;

    @Autowired
    private ApostarCommand apostarCommand;

    @Autowired
    private ApostaCommand apostaCommand;

    @Autowired
    private FinalizarApostaCommand finalizarApostaCommand;

    @Autowired
    private CancelaAposta cancelaAposta;

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
                this.criarApostaCommand.execute(event);
                break;

            case "apostas":
                this.apostasCommand.execute(event);
                break;

            case "apostar":
                this.apostarCommand.execute(event);
                break;

            case "aposta":
                this.apostaCommand.execute(event);
                break;

            case "finalizar":
                this.finalizarApostaCommand.execute(event);
                break;

            case "cancelar":
                this.cancelaAposta.execute(event);
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
                this.criarApostaCommand.execute(event);
            }

            //Apostas
            if (firstWord.equalsIgnoreCase("apostas")){
                this.apostasCommand.execute(event);
            }

            //Apostar
            if (firstWord.equalsIgnoreCase("apostar")){
                if (args.length < 4) throw new Exception();
                this.apostarCommand.execute(event);
            }

            //Aposta
            if (firstWord.equalsIgnoreCase("aposta")){
                if (args.length < 1) throw new Exception();
                this.apostaCommand.execute(event);
            }

            //FinalizarAposta
            if (firstWord.equalsIgnoreCase("finalizar")){
                if (args.length < 3) throw new Exception();
                this.finalizarApostaCommand.execute(event);
            }

            if (firstWord.equalsIgnoreCase("cancelar")){
                if (args.length < 2) throw new Exception();
                this.cancelaAposta.execute(event);
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