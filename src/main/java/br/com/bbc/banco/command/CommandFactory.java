package br.com.bbc.banco.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommandFactory {

    private final CriarCommand criarCommand;
    private final SaldoCommand saldoCommand;
    private final ConviteCommand conviteCommand;
    private final TransferirCommand transferirCommand;
    private final ExtratoCommand extratoCommand;
    private final DailyCommand dailyCommand;
    private final CriarApostaCommand criarApostaCommand;
    private final ApostasCommand apostasCommand;
    private final ApostarCommand apostarCommand;
    private final ApostaCommand apostaCommand;
    private final FinalizarApostaCommand finalizarApostaCommand;
    private final CancelaAposta cancelaAposta;
    private final JokenpoCommand jokenpoCommand;

    public CommandFactory(CriarApostaCommand criarApostaCommand, CriarCommand criarCommand, SaldoCommand saldoCommand, ConviteCommand conviteCommand, TransferirCommand transferirCommand, CancelaAposta cancelaAposta, ExtratoCommand extratoCommand, DailyCommand dailyCommand, JokenpoCommand jokenpoCommand, ApostasCommand apostasCommand, ApostarCommand apostarCommand, ApostaCommand apostaCommand, FinalizarApostaCommand finalizarApostaCommand) {
        this.criarApostaCommand = criarApostaCommand;
        this.criarCommand = criarCommand;
        this.saldoCommand = saldoCommand;
        this.conviteCommand = conviteCommand;
        this.transferirCommand = transferirCommand;
        this.cancelaAposta = cancelaAposta;
        this.extratoCommand = extratoCommand;
        this.dailyCommand = dailyCommand;
        this.jokenpoCommand = jokenpoCommand;
        this.apostasCommand = apostasCommand;
        this.apostarCommand = apostarCommand;
        this.apostaCommand = apostaCommand;
        this.finalizarApostaCommand = finalizarApostaCommand;
    }


    public Command factory(String command) throws Exception {
        switch (command){
            case "convite":
                return this.conviteCommand;

            case "saldo":
                return this.saldoCommand;

            case "criar":
                return this.criarCommand;

            case "transferir":
                return this.transferirCommand;

            case "daily":
                return this.dailyCommand;

            case "extrato":
                return this.extratoCommand;

            case "criaraposta":
                return this.criarApostaCommand;

            case "apostas":
                return this.apostasCommand;

            case "apostar":
                return this.apostarCommand;

            case "aposta":
                return this.apostaCommand;

            case "finalizar":
                return this.finalizarApostaCommand;

            case "cancelar":
                return this.cancelaAposta;

            case "jokenpo":
                return this.jokenpoCommand;

            default:
                throw new Exception();
        }
    }
}
