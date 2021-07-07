package br.com.bbc.banco.embed;

import br.com.bbc.banco.configuration.BotApplication;
import br.com.bbc.banco.enumeration.BotEnumeration;
import br.com.bbc.banco.model.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class Embeds {

    public static EmbedBuilder saldoEmbed(net.dv8tion.jda.api.entities.User author, User user, String mensagem, int cor){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("💰 Saldo Atual 💰");
        embed.addField(String.format("%s %s", BotEnumeration.CURRENCY.getValue(), user.getSaldo().toString()), mensagem, false);
        embed.setColor(cor);
        embed.setFooter("Solicitado por " + author.getName(), author.getAvatarUrl());

        return embed;
    }

    public static EmbedBuilder contaJaExiste(net.dv8tion.jda.api.entities.User author){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Erro!");
        embed.addField("Sua conta já existe!", "Digite /saldo para verificar seu saldo.", false);
        embed.setColor(0x7f2927);
        embed.setFooter("Solicitado por " + author.getName(), author.getAvatarUrl());

        return embed;
    }

    public static EmbedBuilder contaCriadaComSucesso(net.dv8tion.jda.api.entities.User author){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Sucesso!");
        embed.addField("Sua conta foi criada!", "Digite /saldo para verificar seu saldo.", false);
        embed.setColor(0x80b461);
        embed.setFooter("Solicitado por " + author.getName(), author.getAvatarUrl());

        return embed;
    }

    public static EmbedBuilder dailyEmbed(net.dv8tion.jda.api.entities.User author, User user, int valor, int cor){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("💰 Seu ganho do dia 💰");
        embed.addField("+ BC$" + valor, "Saldo: BC$ " + user.getSaldo().toString(), false);
        embed.setColor(cor);
        embed.setFooter("Solicitado por " + author.getName(), author.getAvatarUrl());

        return embed;
    }

    public static EmbedBuilder dailyEmbedError(net.dv8tion.jda.api.entities.User author, long horas, long minutos, long segundos, int cor){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Indisponível!");
        embed.addField("Você ainda precisa esperar:", horas + "h " + minutos + "m " + segundos + "s", true);
        embed.setColor(cor);
        embed.setFooter("Solicitado por " + author.getName(), author.getAvatarUrl());

        return embed;
    }

    public static EmbedBuilder criarApostaEmbed(net.dv8tion.jda.api.entities.User author, Bet bet, List<Option> options, int cor){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(String.format("Aposta %s criada!", bet.getNome()));
        for (Option option : options) {
            embed.addField(String.format("[%d] %s", option.getNumber() + 1, option.getText()), "", false);
        }
        embed.setColor(cor);
        embed.setFooter(String.format("ID da aposta: %d", bet.getId()));

        return embed;
    }

    public static EmbedBuilder criarApostaEmbedError(net.dv8tion.jda.api.entities.User author, int cor){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Informe pelo menos 2 opções!");
        embed.setColor(cor);
        embed.setFooter("Solicitado por " + author.getName(), author.getAvatarUrl());

        return embed;
    }

    public static EmbedBuilder apostasEmbed(net.dv8tion.jda.api.entities.User author, int cor, List<Bet> bets){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Apostas ativas no momento!");
        for (Bet bet : bets) {
            embed.addField(String.format("[%d] %s", bet.getId(), bet.getNome()), "", false);
        }
        embed.setColor(cor);
        embed.setFooter("Solicitado por " + author.getName(), author.getAvatarUrl());

        return embed;
    }

    public static EmbedBuilder extratoEmbed(net.dv8tion.jda.api.entities.User author, User user, List<Transaction> transactions, int cor){

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("💰 Extrato 💰");
        embed.setColor(cor);
        String beforeMessage;
        String mensagem;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yy");

        if(transactions != null){

            Collections.reverse(transactions);
            for (Transaction transaction : transactions) {
                String dateFormated = transaction.getDate().format(formatter);

                if( user.getId().equals(transaction.getUser().getId())){
                    net.dv8tion.jda.api.entities.User userRetrieved = BotApplication.jda.retrieveUserById(transaction.getOriginUser().getId()).complete();

                    beforeMessage = String.format("%s [%s] %s %.2f",
                            Emoji.fromMarkdown("<:money_increased:861040590409302026>"),
                            dateFormated.toUpperCase(),
                            BotEnumeration.CURRENCY.getValue(),
                            transaction.getValor()
                    );

                    mensagem = String.format("de %s", userRetrieved.getName()
                    );
                }
                else{
                    net.dv8tion.jda.api.entities.User userRetrieved = BotApplication.jda.retrieveUserById(transaction.getUser().getId()).complete();

                    beforeMessage = String.format("%s [%s] %s %.2f",
                            Emoji.fromMarkdown("<:money_decreased:861038910112923668>"),
                            dateFormated.toUpperCase(),
                            BotEnumeration.CURRENCY.getValue(),
                            transaction.getValor()
                    );

                    mensagem = String.format("para %s",
                            userRetrieved.getName()
                    );
                }

                embed.addField(beforeMessage, mensagem, false);
            }

        } else {
            embed.addField("Nenhuma transação disponível",
                    "",
                    false
            );
        }

//        embed.addBlankField(false);
        embed.addField(
                "Saldo",
                String.format("%s %.2f", BotEnumeration.CURRENCY.getValue(), user.getSaldo()),
                false
        );

        embed.setFooter("Solicitado por " + author.getName(), author.getAvatarUrl());

        return embed;
    }

    public static EmbedBuilder apostarEmbed(net.dv8tion.jda.api.entities.User author, Bet bet, Option option, String valor, int cor){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Aposta registrada!");
        embed.setColor(cor);
        embed.addField(String.format("Aposta %s:", bet.getNome()), "", false);
        embed.addField(String.format("Opção [%d] %s", option.getNumber() + 1, option.getText()), "", false);
        embed.setFooter("Solicitado por " + author.getName(), author.getAvatarUrl());

        return embed;
    }

    public static EmbedBuilder apostarEmbedErroBet(net.dv8tion.jda.api.entities.User author, int cor){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Não foi encontrada nenhuma aposta ativa com esse ID!");
        embed.setColor(cor);
        embed.setFooter("Solicitado por " + author.getName(), author.getAvatarUrl());

        return embed;
    }

    public static EmbedBuilder apostarEmbedErroOption(net.dv8tion.jda.api.entities.User author, Bet bet, long optionId, int cor){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(String.format("Opção %d não encontrada!", optionId));
        embed.addField(String.format("Opções da aposta %s", bet.getNome()), "", false);
        for (Option option : bet.getOptions()){
            embed.addField(String.format("[%d] %s", option.getNumber() + 1, option.getText()), "", false);
        }
        embed.setColor(cor);
        embed.setFooter("Solicitado por " + author.getName(), author.getAvatarUrl());

        return embed;
    }

    public static EmbedBuilder criarJokenpoEmbed(net.dv8tion.jda.api.entities.User author, net.dv8tion.jda.api.entities.User other , BigDecimal value, long id){
        EmbedBuilder embed = new EmbedBuilder();

        String title = String.format("%s Jokenpo %s",
            Emoji.fromUnicode("U+270A"),
            Emoji.fromUnicode("U+270B")
        );
        embed.setTitle(title);

        String message = String.format("%s te desafiou!",author.getName());

        String underMessage = String.format("Valor: %s %.2f",
            BotEnumeration.CURRENCY.getValue(),
            value
        );

        embed.addField(message,underMessage,false);

        String footer = String.format("Enviado para %s\nGameId#%d",
            other.getName(),
            id
        );

        embed.setFooter(footer, other.getAvatarUrl());

        return embed;
    }

    public static EmbedBuilder criarJokenpoGameEmbed(long gameId){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Escolha uma das opçoes abaixo");
        String footer = String.format("GameId#%s", gameId);
        embed.setFooter(footer);
        return embed;
    }

    public static EmbedBuilder jokenpoEmpate(String gameId){
        EmbedBuilder embed = new EmbedBuilder();

        String title = String.format("%s Jokenpo %s",
                Emoji.fromUnicode("U+270A"),
                Emoji.fromUnicode("U+270B")
        );
        embed.setTitle(title);
        embed.addField("Empatou","",true);
        String footer = String.format("GameId#%s", gameId);
        embed.setFooter(footer);
        return embed;
    }

    public static EmbedBuilder jokenpoGanhador(net.dv8tion.jda.api.entities.User winner , net.dv8tion.jda.api.entities.User loser, String gameId){

        EmbedBuilder embed = new EmbedBuilder();

        String title = String.format("%s Jokenpo %s",
                Emoji.fromUnicode("U+270A"),
                Emoji.fromUnicode("U+270B")
        );
        embed.setTitle(title);

        String message = String.format("%s Ganhou",
                winner.getName()
        );
        String messageAfter = String.format("mais sorte da proxima vez %s",
                loser.getName()
        );

        String footer = String.format("GameId#%s", gameId);
        embed.setFooter(footer);

        embed.addField(message,messageAfter,true);
        return embed;

    }
    public static EmbedBuilder jokenpoReply(String pick){
        EmbedBuilder embed = new EmbedBuilder();

        String title = String.format("%s Jokenpo %s",
                Emoji.fromUnicode("U+270A"),
                Emoji.fromUnicode("U+270B")
        );
        embed.setTitle(title);
        Emoji emoji = Emoji.fromUnicode("U+270A");
        switch (pick){
            case "pedra":
                emoji = Emoji.fromUnicode("U+270A");
            case "papel":
                emoji = Emoji.fromUnicode("U+270B");
            case "tesoura":
                emoji = Emoji.fromUnicode("U+270C");
        }

        String message = String.format("Você escolheu %s",emoji);
        embed.addField(message,"",true);

        return embed;
    }
}
